package com.yusril.nutrify.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.yusril.nutrify.R
import com.yusril.nutrify.core.domain.model.Food
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.random.Random

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        viewModel.id = currentUser!!.uid
        viewModel.date = Calendar.getInstance().time.toString()

        showChart()

        binding.btnGenerateDummy.setOnClickListener {
            setStatistic()
        }
    }

    private fun setStatistic() {
        val currentTime = Calendar.getInstance().time
        val foods = arrayOf("nasi", "ikan", "ayam")
        val food = ArrayList<Food>()
        foods.map {
            val item = Food(
                name = it
            )
            food.add(item)
        }
        val listStats = ListStatistics(
            foods = food,
            total_calories = Random.nextInt(1, 700),
            time = currentTime.toString()
        )
        lifecycleScope.launch {
            viewModel.setStatistic(listStats)
        }
    }

    private fun showChart() {
        val entries = ArrayList<Entry>()

        entries.add(Entry(0f, 100f))
        entries.add(Entry(4f, 0f))
        entries.add(Entry(8f, 800f))
        entries.add(Entry(12f, 1000f))
        entries.add(Entry(16f, 120f))
        entries.add(Entry(20f, 500f))
        entries.add(Entry(24f, 0f))

        val vl = LineDataSet(entries, "My calories")
        vl.apply {
            lineWidth = 3f
            color = R.color.green_500
            valueTextColor = R.color.material_on_surface_emphasis_medium
        }

        binding.lineChart.apply {
            xAxis.labelRotationAngle = 0f
            data = LineData(vl)
            setTouchEnabled(false)
            setPinchZoom(false)
            description.text = "Hour"
            setNoDataText("No data yet!")
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
        }

    }
}