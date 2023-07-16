package pers.shawxingkwok.androidutil.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import kotlin.reflect.KClass

private val cache: MutableMap<KClass<out ViewBinding>, Method> = mutableMapOf()

/**
 * Returns [VB] via reflection, which helps you design view utils.
 */
public fun <VB: ViewBinding> KClass<VB>.inflate(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attachToRoot: Boolean,
): VB {
    require(this != ViewBinding::class)

    @Suppress("UNCHECKED_CAST")
    return cache.getOrPut(this){
        java.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
    }
    .invoke(null, inflater, container, attachToRoot) as VB
}

/**
 * @see inflate
 */
public fun <VB: ViewBinding> KClass<VB>.inflate(inflater: LayoutInflater): VB =
    inflate(inflater, null, false)