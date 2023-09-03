package pers.shawxingkwok.stopwatch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _duration = MutableStateFlow(0)
    private val _intervals = MutableStateFlow(intArrayOf())
    private val _isRunning = MutableStateFlow(false)

    val duration: StateFlow<Int> = _duration
    val intervals: StateFlow<IntArray> = _intervals
    val isRunning: StateFlow<Boolean> = _isRunning
    val tvLeftState = combine(duration, isRunning){ a, b -> a to b }

    fun addDuration(){
        _duration.value++
    }

    fun resetDuration(){
        _duration.value = 0
    }

    fun insertIntervals(){
        _intervals.update { intArrayOf(0) + it }
    }

    fun updateTopIntervals(){
        _intervals.value =
            if (_intervals.value.none())
                intArrayOf(1)
            else
                _intervals.value.clone().also { it[0]++ }
    }

    fun clearIntervals(){
        _intervals.value = intArrayOf()
    }

    fun switchIsRunning(){
        _isRunning.update { !it }
    }
}