package pers.shawxingkwok.sample.ui.main

import android.view.View
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.ItemMsgReceiveBinding
import pers.shawxingkwok.sample.databinding.ItemMsgSendBinding

// In an one-to-one chat page
class MsgAdapter : KRecyclerViewAdapter() {
    // mutable data source
    var msgs: List<Msg> = emptyList()

    /**
     * Not essential.
     *
     * Makes some bindings unrelated to [msgs] in freshly created viewHolders.
     * Here takes the example of loading avatars.
     */
    override fun onHoldersCreated(processors: MutableList<HolderProcessor<ViewBinding>>) {
        processors += HolderProcessor(ItemMsgSendBinding::inflate){
            // Flexibly loaded from local or remote in real cases.
            it.binding.imgAvatar.setImageResource(R.drawable.male)
        }

        processors += HolderProcessor(ItemMsgReceiveBinding::inflate){
            it.binding.imgAvatar.setImageResource(R.drawable.female)
        }
    }

    // This function may be needless.
    private fun sharedProcess(msg: Msg, tv: TextView, itemView: View){
        tv.text = msg.text

        /**
         * Set onLongClickListener in message item view.
         *
         * This is doable in onHoldersCreated to save memories,
         * which is, however, less valuable than the convenience of
         * getting msg here.
         */
        itemView.setOnLongClickListener {
            // Generally, here popups up a window allowing for
            // coping, forwarding `msg`, and so on.
            return@setOnLongClickListener true
        }
    }

    /**
     * Arranges viewHolders as [List]. Add HolderBinder to [binders] with
     *
     * `HolderBinder(XxBinding::inflate, id, contentId){ holder -> }`
     */
    override fun arrange(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += msgs.map { msg ->
            if (msg.isFromMe)
                HolderBinder(
                    inflate = ItemMsgSendBinding::inflate,
                    id = msg.id, // for distinguishing among same kind of items.
                                 // id could be same across different kinds of items.
                    contentId = msg.text // Msg is generally a data class, allowing for using msg
                                         // directly which is a little less efficient.
                ){
                    sharedProcess(msg, it.binding.tv, it.itemView)
                }
            else
                HolderBinder(ItemMsgReceiveBinding::inflate, msg.id, msg.text){
                    sharedProcess(msg, it.binding.tv, it.itemView)
                }
        }
    }
}