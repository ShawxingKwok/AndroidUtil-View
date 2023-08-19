package pers.shawxingkwok.sample

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("pers.shawxingkwok.sample", appContext.packageName)

        val scope = CoroutineScope(Dispatchers.Default)
        var x = 0
        val job = scope.launch {
            repeat(100){
                x++
            }
            withContext(Dispatchers.Main.immediate){
                repeat(1000_000_00){ x++ }
            }
        }
        runBlocking {
            delay(10)
            job.cancel()
        }
        Log.d("KLOG", "useAppContext: $x")
    }
}