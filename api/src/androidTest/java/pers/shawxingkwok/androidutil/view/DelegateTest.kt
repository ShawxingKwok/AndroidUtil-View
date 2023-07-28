package pers.shawxingkwok.androidutil.view

import org.junit.Test
import pers.shawxingkwok.androidutil.KLog
import pers.shawxingkwok.ktutil.KReadWriteProperty
import kotlin.reflect.KProperty

class DelegateTest {

    val s by foo().also { KLog.d("") }

    @Test
    fun start(){
    }

    fun foo() = object : KReadWriteProperty<Any?, String> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            KLog.d("")
            return ""
        }

        override fun onDelegate(thisRef: Any?, property: KProperty<*>) {
            KLog.d("")
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            KLog.d("")
        }
    }
}