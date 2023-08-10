@file:Suppress("ControlFlowWithEmptyBody")

package pers.shawxingkwok.sample.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
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
        processors += HolderProcessor(ItemMsgSendBinding::inflate){
            // ...
        }

        processors += HolderProcessor(ItemMsgReceiveBinding::inflate){
            // ...
        }
    }

    // main logic
    override fun arrange(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += msgs.map { msg ->
            // This customed class is for the shared functionality in multiple `viewHolders`.
            class MsgHolderBinder<VB: ViewBinding>(
                inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
                getTextView: (VB) -> TextView,
            )
                : HolderBinder<VB>(
                    inflate = inflate,
                    id = msg.id,
                    contentId = msg.text,
                    onBindHolder = { holder ->
                        getTextView(holder.binding).text = msg.text

                        // This listener could be set in `onHoldersCreated` in which `msg` is got via
                        // `adapterPosition`. However, the saved few memories are less valuable than
                        // the convenience of getting `msg` here.
                        holder.itemView.setOnLongClickListener {
                            // Generally, here popups up a window allowing for coping and forwarding `msg`.
                            return@setOnLongClickListener true
                        }
                    },
                )

            if (msg.isFromMe)
                MsgHolderBinder(ItemMsgSendBinding::inflate){ it.tv }
            else
                MsgHolderBinder(ItemMsgReceiveBinding::inflate){ it.tv }
        }
    }
}