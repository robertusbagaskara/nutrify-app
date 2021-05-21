package com.yusril.nutrify.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.tvName.text = "Bambang"
        binding.tvWeight.text = resources.getString(R.string.weight, 70)
        binding.tvHeight.text = resources.getString(R.string.height, 169)
        binding.tvBirthDate.text = "03-12-1999"
    }
}