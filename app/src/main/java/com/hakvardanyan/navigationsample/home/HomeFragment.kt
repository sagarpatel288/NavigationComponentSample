package com.hakvardanyan.navigationsample.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hakvardanyan.navigationsample.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater, container, false).run {
        binding = this
        root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
