package pers.shawxingkwok.androidutil.view

import android.view.View

/**
 * Sets a [View.OnClickListener] with a more [T] in [act].
 *
 * Usage sample
 * ```
 * binding.btn.onClick{ it: Button
 *     ...
 * }
 */
public inline fun <T: View> T.onClick(crossinline act: (T) -> Unit){
    setOnClickListener{
        act(this)
    }
}