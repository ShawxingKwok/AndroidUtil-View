package pers.shawxingkwok.androidutil.view

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

/**
 * Is used in [Fragment.onCreateView] or [Fragment.onViewCreated] to
 * collect [this] with [collector] after every [Fragment.onResume].
 *
 * Switch from
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     viewLifecycleOwner.lifecycleScope.launch {
 *         repeatOnLifecycle(Lifecycle.State.RESUMED) {
 *             flow.collect {
 *                 ...
 *             }
 *         }
 *     }
 * }
 * ```
 * to
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     flow.collectOnResume{
 *         ...
 *     }
 * }
 * ```
 */
context(Fragment)
@Deprecated("", ReplaceWith("collectOnResumed", "pers.shawxingkwok.androidutil.view.collectOnResumed"))
public fun <T> Flow<T>.collectOnResume(collector: FlowCollector<T>) {
    require(
        viewLifecycleOwnerLiveData.value != null
        && viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.INITIALIZED
    ){
        "Use this function in 'onCreateView' or 'onViewCreated'."
    }

    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            collect(collector)
        }
    }
}

/**
 * Is used in [Fragment.onCreateView] or [Fragment.onViewCreated] to
 * collect [this] with [collector] after every [Fragment.onResume].
 *
 * Switch from
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     viewLifecycleOwner.lifecycleScope.launch {
 *         repeatOnLifecycle(Lifecycle.State.RESUMED) {
 *             flow.collect {
 *                 ...
 *             }
 *         }
 *     }
 * }
 * ```
 * to
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     flow.collectOnResumed{
 *         ...
 *     }
 * }
 * ```
 */
context(Fragment)
public fun <T> Flow<T>.collectOnResumed(collector: FlowCollector<T>){
    require(
        viewLifecycleOwnerLiveData.value != null
        && viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.INITIALIZED
    ){
        "Use this function in 'onCreateView' or 'onViewCreated'."
    }

    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            collect(collector)
        }
    }
}

/**
 * Is used in [Fragment.onCreateView] or [Fragment.onViewCreated] to
 * collect [this] with [act] after every [Fragment.onResume].
 *
 * Switch from
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     viewLifecycleOwner.lifecycleScope.launch {
 *         repeatOnLifecycle(Lifecycle.State.RESUMED) {
 *             flow.collect {
 *                 ...
 *             }
 *         }
 *     }
 * }
 * ```
 * to
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     flow.collectOnResumed{
 *         ...
 *     }
 * }
 * ```
 */
context(Fragment)
public inline fun <T> Flow<T>.collectOnResumed(crossinline act: suspend CoroutineScope.(T) -> Unit){
    require(
        viewLifecycleOwnerLiveData.value != null
                && viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.INITIALIZED
    ){
        "Use this function in 'onCreateView' or 'onViewCreated'."
    }

    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            collect{ act(it) }
        }
    }
}

/**
 * Is used in [Fragment.onCreateView] or [Fragment.onViewCreated] to
 * collect [this] with [collector] after every [Fragment.onStart].
 *
 * Switch from
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     viewLifecycleOwner.lifecycleScope.launch {
 *         repeatOnLifecycle(Lifecycle.State.STARTED) {
 *             flow.collect {
 *                 ...
 *             }
 *         }
 *     }
 * }
 * ```
 * to
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     flow.collectOnStarted{
 *         ...
 *     }
 * }
 * ```
 */
context(Fragment)
public fun <T> Flow<T>.collectOnStarted(collector: FlowCollector<T>){
    require(
        viewLifecycleOwnerLiveData.value != null
        && viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.INITIALIZED
    ){
        "Use this function in 'onCreateView' or 'onViewCreated'."
    }

    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect(collector)
        }
    }
}

/**
 * Is used in [Fragment.onCreateView] or [Fragment.onViewCreated] to
 * collect [this] with [act] after every [Fragment.onStart].
 *
 * Switch from
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     viewLifecycleOwner.lifecycleScope.launch {
 *         repeatOnLifecycle(Lifecycle.State.STARTED) {
 *             flow.collect {
 *                 ...
 *             }
 *         }
 *     }
 * }
 * ```
 * to
 * ```kotlin
 * override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *     super.onViewCreated(view, savedInstanceState)
 *
 *     flow.collectOnStarted{
 *         ...
 *     }
 * }
 * ```
 */
context(Fragment)
public inline fun <T> Flow<T>.collectOnStarted(crossinline act: suspend CoroutineScope.(T) -> Unit){
    require(
        viewLifecycleOwnerLiveData.value != null
                && viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.INITIALIZED
    ){
        "Use this function in 'onCreateView' or 'onViewCreated'."
    }

    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect{ act(it) }
        }
    }
}