package pers.shawxingkwok.androidutil.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * See [docs](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#kfragment)
 */
public abstract class KDialogFragment<VB: ViewBinding>(private val bindingKClass: KClass<VB>) : Fragment() {
    private val actionsOnCreateView: MutableList<(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) -> Unit> = mutableListOf()
    private val actionsOnViewCreated: MutableList<(view: View, savedInstanceState: Bundle?) -> Unit> = mutableListOf()
    private val actionsOnDestroyView: MutableList<() -> Unit> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        actionsOnCreateView.forEach { it(inflater, container, savedInstanceState) }
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionsOnViewCreated.forEach { it(view, savedInstanceState) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionsOnDestroyView.forEach { it() }
    }

    //region binding
    private var _binding: VB? = null

    /**
     * An automatically initialized [VB].
     *
     * Note: alive only between inclusive [onCreateView] and exclusive [onDestroyView].
     */
    protected val binding: VB get(){
        require(_binding != null){
            "In ${javaClass.canonicalName}, binding is alive between inclusive " +
                    "'onCreateView' and exclusive 'onDestroyView'."
        }

        return _binding!!
    }

    init {
        actionsOnCreateView += { inflater, container, _ ->
            require(bindingKClass != ViewBinding::class){
                "VB should be a subtype of ViewBinding."
            }
            _binding = bindingKClass.inflate(inflater, container, false)
        }

        actionsOnDestroyView += {
            _binding = null
        }
    }
    //endregion

    //region withView
    private object UNINITIALIZED

    /**
     * Delegates a value alive between inclusive [onViewCreated] and exclusive [onDestroyView].
     *
     * Usage example:
     *
     * ```
     * val/var adapter by withView{ Adapter() }
     */
    protected fun <T> withView(initialize: () -> T): ReadWriteProperty<KFragment<VB>, T> =
        object  : ReadWriteProperty<KFragment<VB>, T> {
            var t: Any? = UNINITIALIZED

            init {
                actionsOnViewCreated += { _, _ -> t = initialize() }
                actionsOnDestroyView += { t = UNINITIALIZED }
            }

            private fun requireSafe(prop: KProperty<*>){
                require(t != UNINITIALIZED){
                    "Call ${this@KDialogFragment.javaClass.canonicalName}.${prop.name} between " +
                            "inclusive `onViewCreated` and exclusive `onDestroyView`."
                }
            }

            override fun getValue(thisRef: KFragment<VB>, property: KProperty<*>): T {
                requireSafe(property)
                @Suppress("UNCHECKED_CAST")
                return t as T
            }

            override fun setValue(thisRef: KFragment<VB>, property: KProperty<*>, value: T) {
                requireSafe(property)
                t = value
            }
        }
    //endregion
}