package pers.shawxingkwok.androidutil.view.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import pers.shawxingkwok.androidutil.view.KFragment
import pers.shawxingkwok.androidutil.view.collectOnResume
import pers.shawxingkwok.androidutil.view.demo.databinding.FragmentMainBinding
import pers.shawxingkwok.androidutil.view.onClick

class MainFragment : KFragment<FragmentMainBinding>(FragmentMainBinding::class) {
    private val vm: MainViewModel by viewModels()

    private val msgAdapter: MsgAdapter by withView {
        MsgAdapter().also { binding.rv.adapter = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.items.collectOnResume{
            msgAdapter.msgs = it

            msgAdapter.update {
                binding.rv.scrollToPosition(msgAdapter.itemCount - 1)
                binding.etMsg.text.clear()
            }
        }

        binding.btnSend.onClick {
            vm.sendMsg(binding.etMsg.text.toString())
        }
    }
}