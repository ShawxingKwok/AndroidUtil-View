package pers.shawxingkwok.androidutil.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class FlowCollectTest {
    @Test
    fun start(){
        val scenario = launchFragmentInContainer<MyFragment>()
        scenario.onFragment{

        }
    }

    class MyFragment : Fragment(){
        val flow = flowOf(1)

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            flow.collectOnStarted{
                assert(lifecycle.currentState == Lifecycle.State.STARTED)
            }
            val collector = { it: Int ->
                assert(lifecycle.currentState == Lifecycle.State.STARTED)
            }
            flow.collectOnStarted(collector)

            flow.collectOnResumed{
                assert(lifecycle.currentState == Lifecycle.State.RESUMED)
            }
            val _collector = { it: Int ->
                assert(lifecycle.currentState == Lifecycle.State.RESUMED)
            }
            flow.collectOnResumed(_collector)
        }
    }
}