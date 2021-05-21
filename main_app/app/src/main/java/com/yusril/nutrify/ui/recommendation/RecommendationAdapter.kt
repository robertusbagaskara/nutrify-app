package com.yusril.nutrify.ui.recommendation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yusril.nutrify.databinding.RowItemRecommendationBinding

class RecommendationAdapter: RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {
    class ViewHolder(private val binding: RowItemRecommendationBinding): RecyclerView.ViewHolder(binding.root) {
        //
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemsRecommendedBinding = RowItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemsRecommendedBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}