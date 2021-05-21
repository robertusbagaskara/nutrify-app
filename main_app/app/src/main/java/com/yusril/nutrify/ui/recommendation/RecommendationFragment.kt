package com.yusril.nutrify.ui.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.FragmentRecommendationBinding

class RecommendationFragment : Fragment() {
    private lateinit var binding: FragmentRecommendationBinding
    private lateinit var recommendationViewModel: RecommendationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recommendationViewModel = ViewModelProvider(this).get(RecommendationViewModel::class.java)
        val textView: TextView = binding.root.findViewById(R.id.text_notifications)
        recommendationViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
    }
}