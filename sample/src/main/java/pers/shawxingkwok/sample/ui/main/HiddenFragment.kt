package pers.shawxingkwok.sample.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class HiddenFragment : Fragment() {
    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("SAMPLE", viewLifecycleOwner.toString())
        Log.d("SAMPLE", viewLifecycleOwnerLiveData.value.toString())

        viewLifecycleOwnerLiveData.observe(this){
            Log.d("SAMPLE", it.toString())
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}