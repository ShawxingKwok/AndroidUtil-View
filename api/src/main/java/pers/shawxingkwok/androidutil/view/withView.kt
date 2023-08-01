package pers.shawxingkwok.androidutil.view

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import pers.shawxingkwok.ktutil.KReadWriteProperty
import java.lang.Exception
import kotlin.reflect.KProperty

private object UNINITIALIZED

/**
 * See [docs](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#withview).
 */
@Suppress("UnusedReceiverParameter")
public fun <T> Fragment.withView(initialize: () -> T): KReadWriteProperty<Fragment, T> =
    object : KReadWriteProperty<Fragment, T>{
        var t: Any? = UNINITIALIZED

        override fun onDelegate(thisRef: Fragment, property: KProperty<*>) {
            thisRef.viewLifecycleOwnerLiveData.observe(thisRef){
                if (t === UNINITIALIZED) t = initialize()

                it.lifecycle.addObserver(object : DefaultLifecycleObserver{
                    override fun onDestroy(owner: LifecycleOwner) {
                        super.onDestroy(owner)
                        t = UNINITIALIZED
                    }
                })
            }
        }

        fun requireRightState(thisRef: Fragment, property: KProperty<*>){
            // permitted before onCreateView
            thisRef.viewLifecycleOwnerLiveData.value ?: return

            // forbidden after onDestroyView
            try {
                thisRef.viewLifecycleOwner
            }catch (e: Exception){
                error(
                    "Can't access ${thisRef.javaClass.canonicalName}.${property.name} after onDestroyView()"
                )
            }
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            requireRightState(thisRef, property)
            if (t === UNINITIALIZED) t = initialize()

            @Suppress("UNCHECKED_CAST")
            return t as T
        }

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
            requireRightState(thisRef, property)
            t = value
        }
    }

/**
 * See [docs](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#withview).
 */
@Suppress("UnusedReceiverParameter")
public fun <T> ComponentActivity.withView(initialize: () -> T): KReadWriteProperty<ComponentActivity, T> =
    object : KReadWriteProperty<ComponentActivity, T>{
        var t: Any? = UNINITIALIZED

        override fun onDelegate(thisRef: ComponentActivity, property: KProperty<*>) {
            thisRef.lifecycle.addObserver(object : DefaultLifecycleObserver{
                override fun onCreate(owner: LifecycleOwner) {
                    super.onCreate(owner)
                    if (t === UNINITIALIZED)
                        t = initialize()
                }
            })
        }

        override fun getValue(thisRef: ComponentActivity, property: KProperty<*>): T {
            if (t === UNINITIALIZED) t = initialize()

            @Suppress("UNCHECKED_CAST")
            return t as T
        }

        override fun setValue(thisRef: ComponentActivity, property: KProperty<*>, value: T) {
            t = value
        }
    }