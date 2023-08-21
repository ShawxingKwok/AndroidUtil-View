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
import pers.shawxingkwok.ktutil.updateIf

/**
 * See [doc](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#krecyclerviewadapter)
 */
public abstract class KRecyclerViewAdapter
    : RecyclerView.Adapter<KRecyclerViewAdapter.ViewBindingHolder<ViewBinding>>()
{
    private val updateCallback = AdapterListUpdateCallback(this)

    private val calculatingScope = CoroutineScope(Dispatchers.Default)
    private val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var calculatingJob: Job? = null

    private val creators = mutableListOf<HolderCreator<ViewBinding>>()
    private lateinit var oldBinders: List<HolderBinder<ViewBinding>>
    private var newBinders = mutableListOf<HolderBinder<ViewBinding>>()

    private var isInitialized = false
    private fun initialize(){
        isInitialized = true
        registerProcessRequiredHolderCreators()
        arrangeHolderBinders()
        oldBinders = newBinders
    }

    private val diffCallback = object : DiffUtil.Callback() {
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
    }

    /**
     * [HolderCreator] could be automatically created for building [ViewBindingHolder]
     * which is the subclass of [ViewHolder]。But there is no initial process. You could register process-required
     * [HolderCreator]s in this function to do some fixed tasks only once.
     * It's more efficient but not essential. I suggest only doing time-consuming tasks here.
     */
    protected abstract fun registerProcessRequiredHolderCreators()

    /**
     * Build [HolderBinder] in this function according to the order.
     */
    protected abstract fun arrangeHolderBinders()

    /**
     * Notifies [KRecyclerViewAdapter] to update and call [onFinish].
     *
     * Note that [update] may be called too frequently, which makes some previous [onFinish] omitted.
     */
    public fun update(onFinish: (() -> Unit)? = null) {
        if (!isInitialized){
            initialize()
            onFinish?.invoke()
            return
        }
        calculatingJob?.cancel()
        newBinders = mutableListOf()
        arrangeHolderBinders()

        fun execute(act: () -> Unit) {
            oldBinders = newBinders
            act()
            onFinish?.invoke()
        }

        when {
            // fast simple remove all
            newBinders.none() -> {
                val removedCount = oldBinders.size
                execute {
                    updateCallback.onRemoved(0, removedCount)
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

        // compute in another thread
        // and switch to the main thread and update UI
        calculatingJob = calculatingScope.launch{
            val result = DiffUtil.calculateDiff(diffCallback)

            // withContext(Dispatchers.Main.Immediate) may be half cancelled.
            mainScope.launch {
                execute {
                    result.dispatchUpdatesTo(updateCallback)
                }
            }
        }
    }

    final override fun getItemViewType(position: Int): Int {
        val binder = oldBinders[position]

        return creators.indexOfFirst {
            it.inflate == binder.inflate
        }
        .updateIf({ i ->  i == -1 }) {
            creators += HolderCreator(binder.inflate){}
            return creators.lastIndex
        }
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<ViewBinding> {
        return creators[viewType].create(parent)
    }

    final override fun onBindViewHolder(holder: ViewBindingHolder<ViewBinding>, position: Int) {
        oldBinders[position].onBind(holder)
    }

    final override fun onBindViewHolder(
        holder: ViewBindingHolder<ViewBinding>,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    final override fun getItemCount(): Int {
        if (!isInitialized) initialize()
        return oldBinders.size
    }

    public open class ViewBindingHolder<out VB : ViewBinding>(public val binding: VB) : ViewHolder(binding.root)

    /**
     * Usage example
     * ```
     * HolderCreator(ItemXxBinding::inflate){
     *     ...
     * }
     */
    protected open inner class HolderCreator<out VB : ViewBinding> (
        internal val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        private val process: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit,
    ){
        init {
            require(creators.none{ it.inflate == inflate }){
                "HolderCreators are distinct by 'inflate', but you register repeatedly."
            }
            creators += this
        }

        internal fun create(parent: ViewGroup): ViewBindingHolder<VB> {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = inflate(layoutInflater, parent, false)
            return ViewBindingHolder(binding).also(process)
        }
    }

    /**
     * Usage example
     * ```
     * HolderBinder(ItemXxBinding::inflate, id, contentId){
     *     ...
     * }
     * ```
     * @param inflate is the function inflate in the corresponding ViewBinding subclass.
     * @param id distinguishes among HolderBinders sharing same [inflate].
     * This is suggested null if [inflate] is unique.
     * @param contentId notifies content to update. This is suggested null if the content is fixed.
     * @param onBind does work those in [onBindViewHolder] before.
     */
    protected open inner class HolderBinder<out VB : ViewBinding>(
        internal val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
        internal val id: Any?,
        internal val contentId: Any?,
        internal val onBind: (holder: ViewBindingHolder<@UnsafeVariance VB>) -> Unit
    ){
        init {
            newBinders.add(this)
        }
    }
}