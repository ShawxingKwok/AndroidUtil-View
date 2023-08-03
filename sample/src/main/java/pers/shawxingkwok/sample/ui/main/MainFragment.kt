package pers.shawxingkwok.sample.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dylanc.viewbinding.nonreflection.binding
import pers.shawxingkwok.androidutil.view.collectOnResume
import pers.shawxingkwok.androidutil.view.onClick
import pers.shawxingkwok.androidutil.view.withView
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by binding(FragmentMainBinding::bind)
    private val vm: MainViewModel by viewModels()
    private val msgAdapter by withView(::MsgAdapter)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = msgAdapter
        }

        vm.msgsFlow.collectOnResume{
            msgAdapter.msgs = it
            binding.etMsg.text.clear()
            // Always use update() or update{ ... } after changing data in the adapter.
            msgAdapter.update {
                binding.rv.scrollToPosition(msgAdapter.itemCount - 1)
            }
        }

        binding.btnSend.onClick {
            vm.sendMsg(binding.etMsg.text.toString())
        }
    }
}