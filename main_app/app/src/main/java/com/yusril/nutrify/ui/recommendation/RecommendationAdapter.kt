package com.yusril.nutrify.ui.recommendation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yusril.nutrify.R
import com.yusril.nutrify.core.domain.model.RecommendationList
import com.yusril.nutrify.databinding.RowItemRecommendationBinding

class RecommendationAdapter : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    private val listRecommendation = ArrayList<RecommendationList>()

    fun setRecommendation(items: List<RecommendationList>?) {
        if(items == null) return
        listRecommendation.clear()
        listRecommendation.addAll(items)
        notifyDataSetChanged()

        Log.d("getstats adapter", listRecommendation.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_item_recommendation, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listRecommendation[position])
    }

    override fun getItemCount(): Int = listRecommendation.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = RowItemRecommendationBinding.bind(itemView)
        fun bind(rec: RecommendationList) {
            with(binding) {
                tvRecFoodName.text = rec.name
                tvRecFoodKkal.text =
                    itemView.resources.getString(R.string.total_calories, rec.calories)
            }
        }
    }
}