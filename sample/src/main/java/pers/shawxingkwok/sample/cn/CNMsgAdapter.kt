package pers.shawxingkwok.sample.cn

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.ItemMsgReceiveBinding
import pers.shawxingkwok.sample.databinding.ItemMsgSendBinding
import pers.shawxingkwok.sample.ui.main.Msg

class CNMsgAdapter : KRecyclerViewAdapter() {
    // 供外部修改的数据源，任意类型/份数
    var msgs: List<Msg> = emptyList()

    /**
     * 加工刚创建好的 ViewHolder，一般处理一些耗时且与数据源无关的任务，非刚需。
     */
    override fun onHoldersCreated(processors: MutableList<HolderProcessor<ViewBinding>>) {
        // 这里加载头像，一般从本地或远程获取
        processors += HolderProcessor(ItemMsgSendBinding::inflate){
            it.binding.imgAvatar.setImageResource(R.drawable.male)
        }

        processors += HolderProcessor(ItemMsgReceiveBinding::inflate){
            it.binding.imgAvatar.setImageResource(R.drawable.female)
        }
    }

    // 以 List 形式排列组合 ViewHolder
    // 用 HolderBinder(XxBinding:inflate, id, contentId){ holder -> } 填充 binders。
    override fun arrange(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += msgs.map { msg ->
            // 可设一个方法做些共享的处理
            fun <VB: ViewBinding> MsgHolderBinder(
                inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
                getTextView: (VB) -> TextView,
                // 这个参数在本示例中是不需要的，但很常见
                onBindHolder: (holder: ViewBindingHolder<VB>) -> Unit
            ) =
                HolderBinder(
                    inflate = inflate,
                    id = msg.id, // 用于区分同类 Item，不同 ViewBinding 之间的 item id 可以相同
                    contentId = msg.text // 用于告知 item 内容是否要更新，
                                         // msg 一般为 data class, 亦可偷懒直接用 msg
                ) { holder ->
                    getTextView(holder.binding).text = msg.text

                    // 在消息上设置长按监听。虽然在 onHoldersCreated 设置监听中能省点内存，
                    // 但那里需要通过 adapterPosition 获取数据，不直观。
                    holder.itemView.setOnLongClickListener {
                        // 这里一般弹窗，提示复制、转发等等。
                        return@setOnLongClickListener true
                    }

                    onBindHolder(holder)
                }

            // msg item
            if (msg.isFromMe)
                MsgHolderBinder(ItemMsgSendBinding::inflate, { it.tv }){
                    // 继续对 holder 做些处理，其中的 view 可通过 it.binding 获取
                }
            else
                MsgHolderBinder(ItemMsgReceiveBinding::inflate, { it.tv }){

                }
        }
    }
}