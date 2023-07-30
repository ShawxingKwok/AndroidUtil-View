@file:Suppress("LeakingThis")

package pers.shawxingkwok.androidutil.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.*
import pers.shawxingkwok.ktutil.lazyFast
import pers.shawxingkwok.ktutil.mutableLazy
import pers.shawxingkwok.ktutil.updateIf
import java.lang.Runnable
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * See [docs](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#krecyclerviewadapter)
 */
public abstract class KRecyclerViewAdapter
    : RecyclerView.Adapter<KRecyclerViewAdapter.ViewBindingHolder<ViewBinding>>()
{
    private val updateCallback = AdapterListUpdateCallback(this)

    final override fun getItemCount(): Int = holderBinders.size

    // Max generation of currently scheduled runnable
    private val maxScheduledGeneration = AtomicInteger(0)

    private val scope = run {
        val workQueue = LinkedBlockingQueue<Runnable>()
        val executor = ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, workQueue)
        CoroutineScope(executor.asCoroutineDispatcher())
    }

    /**
     * Notifies [KRecyclerViewAdapter] to update and call [onFinish].
     *
     * Note that [update] may be called too frequently, which makes some previous [onFinish] omitted.
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

            override fun areItemsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int,
            ): Boolean {
                val oldBinder = oldBinders[oldItemPosition]
                val newBinder = newBinders[newItemPosition]
                return oldBinder.inflate == newBinder.inflate
                        && oldBinder.id == newBinder.id
            }

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int,
            ): Boolean =
                oldBinders[oldItemPosition].contentId == newBinders[newItemPosition].contentId

            override fun getChangePayload(
                oldItemPosition: Int,
                newItemPosition: Int,
            ): Any? =
                null
        }

        // compute in another thread.
        scope.launch{
            val result = DiffUtil.calculateDiff(callback)

            // switch to the main thread and update UI if there is no submitted new list.
            if (runGeneration == maxScheduledGeneration.get())
                launch(Dispatchers.Main.immediate){
                    execute {
                        result.dispatchUpdatesTo(updateCallback)
                    }
                }
        }
    }

    private val holderProcessors: MutableList<HolderProcessor<ViewBinding>> by lazyFast {
        val list = mutableListOf<HolderProcessor<ViewBinding>>().also(::onHoldersCreated)

        require(list.distinctBy { it.inflate }.size == list.size){
            "Creation helpers are distinct by 'inflate', but you register repeatedly."
        }

        list
    }

    private var holderBinders: List<HolderBinder<ViewBinding>> by mutableLazy {
        mutableListOf<HolderBinder<ViewBinding>>().also(::arrange)
    }

    final override fun getItemViewType(position: Int): Int {
        val binder = holderBinders[position]

        return holderProcessors.indexOfFirst {
            it.inflate == binder.inflate
        }
        .updateIf({ i ->  i == -1 }) {
            holderProcessors += HolderProcessor(binder.inflate){}
            return holderProcessors.lastIndex
        }
    }

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<ViewBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val processor = holderProcessors[viewType]
        val binding = processor.inflate(layoutInflater, parent, false)
        return ViewBindingHolder(binding).also(processor.process)
    }

    final override fun onBindViewHolder(holder: ViewBindingHolder<ViewBinding>, position: Int) {
        holderBinders[position].onBindHolder(holder)
    }

    final override fun onBindViewHolder(
        holder: ViewBindingHolder<ViewBinding>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    protected open fun onHoldersCreated(processors: MutableList<HolderProcessor<ViewBinding>>){}

    protected abstract fun arrange(binders: MutableList<HolderBinder<ViewBinding>>)

    public open class HolderProcessor<out VB : ViewBinding> (
        internal val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        internal val process: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit,
    )

    public open class HolderBinder<out VB : ViewBinding>(
        internal val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        internal val id: Any?,
        internal val contentId: Any?,
        internal val onBindHolder: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit
    )

    public open class ViewBindingHolder<out VB : ViewBinding>(public val binding: VB) : ViewHolder(binding.root)
}