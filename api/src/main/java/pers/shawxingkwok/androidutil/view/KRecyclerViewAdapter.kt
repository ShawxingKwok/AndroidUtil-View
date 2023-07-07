@file:Suppress("LeakingThis")

package pers.shawxingkwok.androidutil.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.*
import pers.shawxingkwok.ktutil.mutableLazy
import pers.shawxingkwok.ktutil.updateIf
import java.lang.Runnable
import java.lang.reflect.Method
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

/**
 * A simplified [RecyclerView.Adapter] supporting multiple kinds of items.
 *
 * See [detailed docs](https://shawxingkwok.github.io/ITWorks/docs/androidutilview/html/view/pers.shawxingkwok.androidutil.view/-k-recycler-view-adapter/)
 */
public abstract class KRecyclerViewAdapter(private val scope: CoroutineScope)
    : RecyclerView.Adapter<KRecyclerViewAdapter.ViewBindingHolder<ViewBinding>>()
{
    private val updateCallback = AdapterListUpdateCallback(this)

    /**
     * @suppress
     */
    final override fun getItemCount(): Int = holderBinders.size

    // Max generation of currently scheduled runnable
    private val maxScheduledGeneration = AtomicInteger(0)

    private val diffDispatcher = run {
        val workQueue = LinkedBlockingQueue<Runnable>()
        val executor = ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, workQueue)
        executor.asCoroutineDispatcher()
    }

    /**
     * Notifies [KRecyclerViewAdapter] to update and call [onFinish].
     *
     * Warning: [update] may be called too frequently, which makes some previous [onFinish] may be omitted.
     */
    public fun update(onFinish: (() -> Unit)? = null) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        val runGeneration = maxScheduledGeneration.addAndGet(1)
        val oldBinders = holderBinders
        val newBinders = mutableListOf<HolderBinder<ViewBinding>>().also(::arrange)

        fun execute(act: () -> Unit) {
            holderBinders = newBinders
            act()
            onFinish?.invoke()
        }

        when {
            oldBinders == newBinders -> return

            // fast simple remove all
            newBinders.none() -> {
                execute {
                    updateCallback.onRemoved(0, oldBinders.size)
                }
                return
            }

            // fast simple insert first
            oldBinders.none() -> {
                execute {
                    updateCallback.onInserted(0, newBinders.size)
                }
                return
            }
        }

        val callback = object : DiffUtil.Callback() {
            override fun getOldListSize() = oldBinders.size

            override fun getNewListSize(): Int = newBinders.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldBinder = oldBinders[oldItemPosition]
                val newBinder = newBinders[newItemPosition]
                return oldBinder.bindingKClass == newBinder.bindingKClass && oldBinder.id == newBinder.id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldBinders[oldItemPosition].contentId == newBinders[newItemPosition].contentId

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? = null
        }

        // compute in another thread.
        scope.launch(diffDispatcher) {
            val result = DiffUtil.calculateDiff(callback)

            // switch to the main thread and update UI if there is no submitted new list.
            if (runGeneration == maxScheduledGeneration.get())
                withContext(Dispatchers.Main.immediate){
                    execute {
                        result.dispatchUpdatesTo(updateCallback)
                    }
                }
        }
    }

    private var holderCreators: List<HolderCreator<ViewBinding>> by mutableLazy {
        val list = mutableListOf<HolderCreator<ViewBinding>>().also(::register)

        require(list.distinctBy { it.bindingKClass }.size == list.size){
            "Creation helpers are distinct by bindingKClass, but you register repeatedly."
        }

        list
    }

    private var holderBinders: List<HolderBinder<ViewBinding>> by mutableLazy {
        mutableListOf<HolderBinder<ViewBinding>>().also(::arrange)
    }

    /**
     * @suppress
     */
    final override fun getItemViewType(position: Int): Int {
        val binder = holderBinders[position]

        return holderCreators.indexOfFirst { holderCreator ->
            holderCreator.bindingKClass == binder.bindingKClass
        }
        .updateIf({ i ->  i == -1 }) {
            holderCreators += HolderCreator(binder.bindingKClass){}
            return holderCreators.lastIndex
        }
    }

    /**
     * @suppress
     */
    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<ViewBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val builder = holderCreators.elementAt(viewType)
        return builder.buildViewHolder(parent, layoutInflater)
    }

    /**
     * @suppress
     */
    final override fun onBindViewHolder(holder: ViewBindingHolder<ViewBinding>, position: Int) {
        holderBinders[position].onBindHolder(holder)
    }

    /**
     * Processes [ViewBindingHolder] after its creation via [creators].
     */
    protected open fun register(creators: MutableList<HolderCreator<ViewBinding>>){}

    /**
     * Arranges new items via [binders].
     */
    protected abstract fun arrange(binders: MutableList<HolderBinder<ViewBinding>>)

    /**
     * @suppress
     */
    public class HolderCreator<out VB : ViewBinding> (
        internal val bindingKClass: KClass<@UnsafeVariance VB>,
        internal val onHolderCreated: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit,
    ) {
        private companion object{
            private val cache = mutableMapOf<KClass<out ViewBinding>, Method>()
        }

        private val getBinding: Method =
            cache.getOrPut(bindingKClass){
                bindingKClass.java
                    .getMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.java
                    )
            }

        @Suppress("UNCHECKED_CAST")
        internal fun buildViewHolder(
            parent: ViewGroup,
            layoutInflater: LayoutInflater,
        )
            : ViewBindingHolder<@UnsafeVariance VB>
        {
            val binding = getBinding(null, layoutInflater, parent, false) as VB
            return ViewBindingHolder(binding).also(onHolderCreated)
        }
    }

    /**
     * @suppress
     */
    public class HolderBinder<out VB : ViewBinding>(
        internal val bindingKClass: KClass<@UnsafeVariance VB>,
        internal val id: Any?,
        internal val contentId: Any?,
        internal val onBindHolder: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit
    )

    /**
     * @suppress
     */
    public class ViewBindingHolder<out VB : ViewBinding> internal constructor(public val binding: VB) : ViewHolder(binding.root)
}