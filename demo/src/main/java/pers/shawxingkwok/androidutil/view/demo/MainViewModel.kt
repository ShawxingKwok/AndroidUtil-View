package pers.shawxingkwok.androidutil.view.demo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel(){
    //  from database in real cases
    private val _items = (1..50)
        .map{ Msg(it.toLong(), it % 2 == 0, it.toString()) }
        .let(::MutableStateFlow)

    val items: Flow<List<Msg>> get() = _items

    fun sendMsg(text: String){
        val newMsg = Msg(_items.value.last().id + 1, true, text)
        _items.update { it + newMsg }
    }
}