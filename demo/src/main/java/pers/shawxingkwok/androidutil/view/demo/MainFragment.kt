package pers.shawxingkwok.androidutil.view.demo

import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import pers.shawxingkwok.androidutil.KLog
import pers.shawxingkwok.androidutil.view.KFragment
import pers.shawxingkwok.androidutil.view.collectOnResume
import pers.shawxingkwok.androidutil.view.demo.databinding.FragmentMainBinding
import kotlin.reflect.KClass

class MainFragment : KFragment<FragmentMainBinding>(FragmentMainBinding::class) {
    private val vm: MainViewModel by viewModels()

    private val rvAdapter: RvAdapter by withView {
        RvAdapter(
            scope = viewLifecycleOwner.lifecycleScope,
            users = vm.items.value
        )
        .also { binding.rv.adapter = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.items.collectOnResume{
            rvAdapter.users = it
            rvAdapter.update()
        }

        binding.btn.setOnClickListener {
            val newTopUserId = rvAdapter.users.last().id + 1
            vm.items.value = rvAdapter.users + User(newTopUserId)
        }
    }
}