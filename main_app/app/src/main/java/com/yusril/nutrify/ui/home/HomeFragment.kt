package com.yusril.nutrify.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

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

        showChart()
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