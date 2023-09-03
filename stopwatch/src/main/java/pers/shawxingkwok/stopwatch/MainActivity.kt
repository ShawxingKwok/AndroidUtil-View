package pers.shawxingkwok.stopwatch

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pers.shawxingkwok.androidutil.view.onClick
import pers.shawxingkwok.ktutil.updateIf
import pers.shawxingkwok.stopwatch.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private val adapter = StopwatchAdapter()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(this)

        // the way of collecting flow is different in Fragment.
        vm.duration.onEach {
            binding.tvDuration.text = StopwatchUtil.formatDuration(it)
        }
        .launchIn(lifecycleScope)

        vm.intervals.onEach {
            val sizeChanged = it.size != adapter.intervals.size
            adapter.intervals = it
            adapter.update(sizeChanged)
        }
        .launchIn(lifecycleScope)

        var timer: Timer? = null

        vm.isRunning.onEach { isRunning ->
            when{
                !isRunning -> {
                    timer?.cancel()
                    timer = null
                }
                timer == null ->
                    timer = timer(period = 10) {
                        vm.addDuration()
                        vm.updateTopIntervals()
                    }
                // else -> do nothing if recovered after onStop
            }

            // switch button stop/start
            val tv = binding.tvRight

            if (isRunning) {
                tv.text = "Stop"
                tv.setBackgroundResource(R.drawable.circle_dark_red)
                tv.setTextColor(StopwatchUtil.lightRed)
            } else {
                tv.text = "Start"
                tv.setBackgroundResource(R.drawable.circle_dark_green)
                tv.setTextColor(StopwatchUtil.lightGreen)
            }
        }
        .launchIn(lifecycleScope)

        vm.tvLeftState.onEach {(duration, isRunning) ->
            val tv = binding.tvLeft
            when{
                // disabled
                duration == 0 -> {
                    tv.setBackgroundResource(R.drawable.circle_dark_grey)
                    tv.text = "Lap"
                    tv.setTextColor(StopwatchUtil.whiteGrey)
                    tv.isClickable = false
                }
                // lap
                isRunning -> {
                    tv.setBackgroundResource(R.drawable.circle_light_grey)
                    tv.text = "Lap"
                    tv.setTextColor(StopwatchUtil.white)
                    tv.isClickable = true
                    // This OnClickListener is variable, which also explains why there is a
                    // function `setFixedListeners` at last.
                    tv.onClick { _ ->
                        vm.insertIntervals()

                        val layoutManager = binding.rv.layoutManager as LinearLayoutManager
                        val topVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                            .updateIf({ it == -1 }) { 0 }
                        binding.rv.scrollToPosition(topVisiblePosition)
                    }
                }
                // reset
                else ->{
                    tv.setBackgroundResource(R.drawable.circle_light_grey)
                    tv.text = "Reset"
                    tv.setTextColor(StopwatchUtil.white)
                    tv.isClickable = true
                    tv.onClick {
                        vm.resetDuration()
                        vm.clearIntervals()
                    }
                }
            }
        }
        .launchIn(lifecycleScope)

        binding.tvRight.onClick {
            vm.switchIsRunning()
        }
    }
}