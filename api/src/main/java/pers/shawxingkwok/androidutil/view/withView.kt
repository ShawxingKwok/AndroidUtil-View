package pers.shawxingkwok.androidutil.view

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import pers.shawxingkwok.ktutil.KReadOnlyProperty
import pers.shawxingkwok.ktutil.KReadWriteProperty
import java.lang.Exception
import kotlin.reflect.KProperty

private object UNINITIALIZED

/**
 * See [docs](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#withview).
 */
@Suppress("UnusedReceiverParameter")
public fun <T> Fragment.withView(initialize: () -> T): KReadOnlyProperty<Fragment, T> =
    object : KReadOnlyProperty<Fragment, T>{
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

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            try {
                thisRef.viewLifecycleOwner
            }catch (e: Exception){
                error(
                    "Can't access ${thisRef.javaClass.canonicalName}.${property.name} " +
                    "before `onCreateView()` and after `onDestroyView()`."
                )
            }

            if (t === UNINITIALIZED) t = initialize()

            @Suppress("UNCHECKED_CAST")
            return t as T
        }
    }

/**
 * See [docs](https://shawxingkwok.github.io/ITWorks/docs/android/util-view/#withview).
 */
@Suppress("UnusedReceiverParameter")
public fun <T> ComponentActivity.withView(initialize: () -> T): KReadOnlyProperty<ComponentActivity, T> =
    object : KReadOnlyProperty<ComponentActivity, T>{
        var t: Any? = UNINITIALIZED

        override fun onDelegate(thisRef: ComponentActivity, property: KProperty<*>) {
            thisRef.lifecycle.addObserver(object : DefaultLifecycleObserver{
                override fun onCreate(owner: LifecycleOwner) {
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
    }