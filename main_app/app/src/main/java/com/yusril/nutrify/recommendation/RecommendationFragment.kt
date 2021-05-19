package com.yusril.nutrify.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yusril.nutrify.R

class RecommendationFragment : Fragment() {

    private lateinit var recommendationViewModel: RecommendationViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        recommendationViewModel =
                ViewModelProvider(this).get(RecommendationViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_recommendation, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        recommendationViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}