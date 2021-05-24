package com.yusril.nutrify.ui.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.FragmentStatisticsBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticsFragment : Fragment(), View.OnClickListener {

    private lateinit var statisticsViewModel: StatisticsViewModel
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnWeek1.setOnClickListener(this)
        binding.btnWeek2.setOnClickListener(this)
        binding.btnWeek3.setOnClickListener(this)
        binding.btnWeek4.setOnClickListener(this)


        monthYearPicker()
        showCaloriesChart()
        showConsumingChart()

    }

    private fun monthYearPicker() {
        val calendar = Calendar.getInstance()
        val monthSelected = calendar.get(Calendar.MONTH)
        val yearSelected = calendar.get(Calendar.YEAR)

        val dialogFragment = MonthYearPickerDialogFragment.getInstance(
            monthSelected, yearSelected, "Select"
        )
        binding.btnMonthPicker.setOnClickListener {
            activity?.let { dialogFragment.show(it.supportFragmentManager, null) }
            dialogFragment.setOnDateSetListener { year, monthOfYear ->
                val monthFormat = SimpleDateFormat("MMMM")
                calendar.set(Calendar.MONTH, monthOfYear)
                val month = monthFormat.format(calendar.time)
                binding.btnMonthPicker.text = "$month $year"
            }
        }
    }

    private fun showConsumingChart() {
        val xAxisValue =
            arrayOf("", "Banana", "Rice", "Meatball", "Coca Cola", "Chicken", "Beef", "Wagyu")
        val entries = ArrayList<BarEntry>()

        entries.add(BarEntry(1f, 23f))
        entries.add(BarEntry(2f, 20f))
        entries.add(BarEntry(3f, 18f))
        entries.add(BarEntry(4f, 18f))
        entries.add(BarEntry(5f, 15f))
        entries.add(BarEntry(6f, 14f))
        entries.add(BarEntry(7f, 11f))

        val bc = BarDataSet(entries, "Consuming")
        bc.apply {
            color = R.color.green_500
        }

        binding.consumingChart.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValue)
            data = BarData(bc)
            setTouchEnabled(false)
            setPinchZoom(false)
            description.text = "food"
            setNoDataText("No data yet!")
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
        }
    }

    private fun showCaloriesChart() {
        val xAxisValue = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val entries = ArrayList<Entry>()

        entries.add(Entry(1f, 100f))
        entries.add(Entry(2f, 0f))
        entries.add(Entry(3f, 800f))
        entries.add(Entry(4f, 1000f))
        entries.add(Entry(5f, 120f))
        entries.add(Entry(6f, 500f))
        entries.add(Entry(7f, 0f))

        val vl = LineDataSet(entries, "My calories")
        vl.apply {
            lineWidth = 3f
            color = R.color.green_500
            valueTextColor = R.color.material_on_surface_emphasis_medium
        }

        binding.caloriesChart.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValue)
            xAxis.labelRotationAngle = 0f
            data = LineData(vl)
            setTouchEnabled(false)
            setPinchZoom(false)
            description.text = "Day"
            setNoDataText("No data yet!")
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
        }

    }

    override fun onClick(btn: View?) {

    }
}