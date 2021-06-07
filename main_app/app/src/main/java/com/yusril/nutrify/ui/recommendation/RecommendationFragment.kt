package com.yusril.nutrify.ui.recommendation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.databinding.FragmentRecommendationBinding
import com.yusril.nutrify.ui.statistic.StatisticsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RecommendationFragment : Fragment() {
    private lateinit var binding: FragmentRecommendationBinding
    private val viewModel: RecommendationViewModel by viewModel()
    private val statViewModel: StatisticsViewModel by viewModel()
    private lateinit var auth: FirebaseAuth
    private lateinit var mAdapter: RecommendationAdapter
    private var remaining: Int = 0

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
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val dateLocal = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val date = dateLocal.format(formatter)
        val limit = dateLocal.format(formatter).toInt()

        statViewModel.id = currentUser!!.uid
        statViewModel.date = date
        Log.d("DATE", date)
        statViewModel.getTotalCaloriesPerDay(limit, limit).observe(viewLifecycleOwner, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> {
                        Log.d("getstats", "loading")
                    }
                    is Resource.Success -> {
                        Log.d("resource", it.toString())
                        it.data.map { data ->
                            Log.d("getstats", "ini didalam iterasi")
                            remaining = data.remaining
                        }

                        Log.d("getstats remaining", remaining.toString())
                        getRecommendation(remaining)
                        Log.d("getstats", "berhasil")
                    }
                    is Resource.Error -> {
                        Log.d("getstats", "error")
                    }
                }
            }
        })

        showRecyclerView()
        showLoading(true)

    }

    private fun getRecommendation(remaining: Int) {
        viewModel.getRecommendation(remaining).observe(viewLifecycleOwner, {
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