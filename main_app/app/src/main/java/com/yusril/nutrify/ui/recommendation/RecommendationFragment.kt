package com.yusril.nutrify.ui.recommendation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.RecommendationList
import com.yusril.nutrify.databinding.FragmentRecommendationBinding
import org.koin.android.viewmodel.ext.android.viewModel

class RecommendationFragment : Fragment() {
    private lateinit var binding: FragmentRecommendationBinding
    private val viewModel: RecommendationViewModel by viewModel()
    private lateinit var mAdapter: RecommendationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showRecyclerView()
        showLoading(true)

        viewModel.getRecommendation(200).observe(viewLifecycleOwner, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> {
                        Log.d("getstats", "loading")
                    }
                    is Resource.Success -> {
                        Log.d("resource", it.toString())
                        if (it.data.isNullOrEmpty()) {
                            binding.tvNotfound.visibility = View.VISIBLE
                        } else {
                            binding.tvNotfound.visibility = View.GONE
                        }
                        Log.d("getstats fragment", it.data.toString())
                        showLoading(false)
                        mAdapter.setRecommendation(it.data)
                        Log.d("getstats", "berhasil")
                    }
                    is Resource.Error -> {
                        Log.d("getstats", "error")
                    }
                }
            }
        })


    }

    private fun showRecyclerView() {
        mAdapter = RecommendationAdapter()
        mAdapter.notifyDataSetChanged()

        binding.rvRecommendation.apply {
            layoutManager = LinearLayoutManager(context.applicationContext)
            adapter = mAdapter
            setHasFixedSize(true)
        }
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvNotfound.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}