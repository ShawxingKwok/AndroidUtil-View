package pers.shawxingkwok.sample.cn

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dylanc.viewbinding.nonreflection.binding
import pers.shawxingkwok.androidutil.view.collectOnResume
import pers.shawxingkwok.androidutil.view.onClick
import pers.shawxingkwok.ktutil.fastLazy
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.FragmentMainBinding
import pers.shawxingkwok.sample.ui.main.MainViewModel
import pers.shawxingkwok.sample.ui.main.MsgAdapter

class CNMainFragment : Fragment(R.layout.fragment_main) {
    private val binding by binding(FragmentMainBinding::bind)
    private val vm: MainViewModel by viewModels()
    private val msgAdapter by fastLazy(::MsgAdapter)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.run {
            adapter = msgAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        vm.msgsFlow.collectOnResume{
            binding.etMsg.text.clear()
            msgAdapter.msgs = it

            /**
            * 更改 adapter 数据源后要通知 adapter 更新，
            * 但统一用 update / update { ... } 而不是 notify...
            *
            * 上面的 lambda 会在 RecyclerView 更新后执行。
            * 数据可能变化得特别频繁导致只执行最后的 update，之前的 lambda 可能被省略。
            */
            msgAdapter.update {
                binding.rv.scrollToPosition(msgAdapter.itemCount - 1)
            }
        }

        binding.btnSend.onClick {
            vm.sendMsg(binding.etMsg.text.toString())
        }
    }
}