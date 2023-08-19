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
import pers.shawxingkwok.androidutil.KLog
import pers.shawxingkwok.ktutil.fastLazy
import pers.shawxingkwok.ktutil.updateIf
import java.util.concurrent.atomic.AtomicInteger

/**
 * See [docs](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#krecyclerviewadapter)
 */
public abstract class KRecyclerViewAdapter
    : RecyclerView.Adapter<KRecyclerViewAdapter.ViewBindingHolder<ViewBinding>>()
{
    private val updateCallback = AdapterListUpdateCallback(this)

    private val scope = CoroutineScope(Dispatchers.Default)

    private var calculatingJob: Job? = null

    private var onUpdate = false

    private var oldBinders by fastLazy {
        onHoldersCreated()
        if (!onUpdate) arrange()
        newBinders.toList()
    }

    private val newBinders = mutableListOf<HolderBinder<ViewBinding>>()

    private val holderProcessors = mutableListOf<HolderProcessor<ViewBinding>>()

    /**
     * Build [HolderProcessor] in this function.
     */
    protected abstract fun onHoldersCreated()

    /**
     * Build [HolderBinder] in this function.
     */
    protected abstract fun arrange()

    /**
     * Notifies [KRecyclerViewAdapter] to update and call [onFinish].
     *
     * Note that [update] may be called too frequently, which makes some previous [onFinish] omitted.
     */
    public fun update(onFinish: (() -> Unit)? = null) {
        onUpdate = true
        calculatingJob?.cancel()
        newBinders.clear()
        arrange()

        fun execute(act: () -> Unit) {
            oldBinders = newBinders
            act()
            onFinish?.invoke()
        }

        when {
            // fast simple remove all
            newBinders.none() -> {
                val oldBinders = oldBinders
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

        // compute in another thread
        // and switch to the main thread and update UI
        calculatingJob = scope.launch{
            val result = DiffUtil.calculateDiff(callback)

            launch(Dispatchers.Main.immediate){
                execute {
                    result.dispatchUpdatesTo(updateCallback)
                }
            }
        }
    }

    final override fun getItemViewType(position: Int): Int {
        val binder = oldBinders[position]

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
        oldBinders[position].onBindHolder(holder)
    }

    final override fun onBindViewHolder(
        holder: ViewBindingHolder<ViewBinding>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    final override fun getItemCount(): Int = oldBinders.size

    public open inner class HolderProcessor<out VB : ViewBinding> (
        internal val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        internal val process: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit,
    ){
        init {
            // TODO(Test)
            require(holderProcessors.none{ it.inflate == inflate }){
                "Creation helpers are distinct by 'inflate', but you register repeatedly."
            }
            holderProcessors += this
        }
    }

    public open inner class HolderBinder<out VB : ViewBinding>(
        internal val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        internal val id: Any?,
        internal val contentId: Any?,
        internal val onBindHolder: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit
    ){
        init {
            newBinders += this
        }
    }

    public open class ViewBindingHolder<out VB : ViewBinding>(public val binding: VB) : ViewHolder(binding.root)
}