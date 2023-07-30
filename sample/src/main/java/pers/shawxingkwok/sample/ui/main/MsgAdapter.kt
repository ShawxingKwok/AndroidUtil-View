package pers.shawxingkwok.sample.ui.main

import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.sample.databinding.ItemMsgReceiveBinding
import pers.shawxingkwok.sample.databinding.ItemMsgSendBinding

class MsgAdapter : KRecyclerViewAdapter() {
    var msgs: List<Msg> = emptyList()

    override fun arrange(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += msgs.map { msg ->
            if (msg.fromMe)
                HolderBinder(
                    inflate = ItemMsgSendBinding::inflate,
                    id = msg.id,
                    contentId = msg.text,
                ){
                    it.binding.tv.text = msg.text
                }
            else
                HolderBinder(
                    inflate = ItemMsgReceiveBinding::inflate,
                    id = msg.id,
                    contentId = msg.text,
                ){
                    it.binding.tv.text = msg.text
                }
        }
    }
}