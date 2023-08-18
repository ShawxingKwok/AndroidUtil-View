package pers.shawxingkwok.sample.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.ItemMsgReceiveBinding
import pers.shawxingkwok.sample.databinding.ItemMsgSendBinding

class MsgAdapter : KRecyclerViewAdapter() {
    // any sets of source data of any types
    var msgs: List<Msg> = emptyList()

    /**
     * Override `onHoldersCreated` in case you need to make some fixed processing of `ViewHolder`
     * after its automatic creation regardless of `position`.
     */
    override fun onHoldersCreated(processors: MutableList<HolderProcessor<ViewBinding>>) {
        // Set contact avatars loaded from database or remote in real cases.

        processors += HolderProcessor(ItemMsgSendBinding::inflate){
            it.binding.imgAvatar.setImageResource(R.drawable.male)
        }

        processors += HolderProcessor(ItemMsgReceiveBinding::inflate){
            it.binding.imgAvatar.setImageResource(R.drawable.female)
        }
    }

    // main logic
    override fun arrange(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += msgs.map { msg ->
            // This function, which may be needless, is for the shared functionality in multiple `viewHolders`.
            fun <VB: ViewBinding> MsgHolderBinder(
                inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
                getTextView: (VB) -> TextView,
                // this parameter is needless in this case, but so common that here displays it
                onBindHolder: (holder: ViewBindingHolder<VB>) -> Unit
            ) =
                HolderBinder(
                    inflate = inflate,
                    id = msg.id,
                    contentId = msg.text
                ) { holder ->
                    getTextView(holder.binding).text = msg.text

                    // This listener could be set in `onHoldersCreated` in which `msg` is got via
                    // `adapterPosition`. However, the saved few memories are less valuable than
                    // the convenience of getting `msg` here.
                    holder.itemView.setOnLongClickListener {
                        // For a general message item in the chat page,
                        // here popups up a window allowing for coping and forwarding `msg`.
                        return@setOnLongClickListener true
                    }

                    onBindHolder(holder)
                }

            if (msg.isFromMe)
                MsgHolderBinder(ItemMsgSendBinding::inflate, ItemMsgSendBinding::tv){

                }
            else
                MsgHolderBinder(ItemMsgReceiveBinding::inflate, ItemMsgReceiveBinding::tv){

                }
        }
    }
}