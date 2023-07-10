package pers.shawxingkwok.androidutil.view.demo

import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.androidutil.view.demo.databinding.ItemMsgEndBinding
import pers.shawxingkwok.androidutil.view.demo.databinding.ItemMsgStartBinding

class MsgAdapter : KRecyclerViewAdapter() {
    var msgs: List<Msg> = emptyList()

    override fun arrange(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += msgs.map { msg ->
            if (msg.fromMe)
                HolderBinder(
                    bindingKClass = ItemMsgEndBinding::class,
                    id = msg.id,
                    contentId = msg.text,
                ){
                    it.binding.tv.text = msg.text
                }
            else
                HolderBinder(
                    bindingKClass = ItemMsgStartBinding::class,
                    id = msg.id,
                    contentId = msg.text,
                ){
                    it.binding.tv.text = msg.text
                }
        }
    }
}