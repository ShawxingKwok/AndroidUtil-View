package pers.shawxingkwok.sample.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dylanc.viewbinding.nonreflection.binding
import pers.shawxingkwok.androidutil.view.collectOnResume
import pers.shawxingkwok.androidutil.view.onClick
import pers.shawxingkwok.androidutil.view.withView
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by binding(FragmentMainBinding::bind)
    private val vm: MainViewModel by viewModels()

    private val msgAdapter: MsgAdapter by withView {
        MsgAdapter().also { binding.rv.adapter = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.msgsFlow.collectOnResume{
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