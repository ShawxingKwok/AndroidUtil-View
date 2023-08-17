package pers.shawxingkwok.sample.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){
    //  from database in real cases
    private val _msgsFlow = MutableStateFlow(emptyList<Msg>())
    val msgsFlow: Flow<List<Msg>> get() = _msgsFlow

    private val newID: Long get() = _msgsFlow.value.size.toLong()

    fun sendMsg(text: String){
        val greeting = Msg(newID, true, text)
        _msgsFlow.update { it + greeting }

        if (text == "How are you")
            viewModelScope.launch {
                delay(1000)
                val reply = Msg(newID, false, "Good")
                _msgsFlow.update { it + reply }
            }
    }
}