package pers.shawxingkwok.sample.ui.main

import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.sample.databinding.ItemMsgReceiveBinding
import pers.shawxingkwok.sample.databinding.ItemMsgSendBinding

class MsgAdapter : KRecyclerViewAdapter() {
    // any sets of data of any types
    var msgs: List<Msg> = emptyList()

    // In case you need to make some fixed processing of `ViewHolder` after its automatic
    // creation regardless of `position`.
    override fun onHoldersCreated(processors: MutableList<HolderProcessor<ViewBinding>>) {
        processors += HolderProcessor(ItemMsgSendBinding::inflate){
            // Here is allowed to set Listeners on view with adapterPosition to get data, which is,
            // however, unclear. Just take this step in arrange which costs ignorable more memories.
        }

        processors += HolderProcessor(ItemMsgReceiveBinding::inflate){

        }
    }

    // main logic
    override fun arrange(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += msgs.map { msg ->
            if (msg.isFromMe)
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