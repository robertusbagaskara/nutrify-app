package com.yusril.nutrify.ui.statistic

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.yusril.nutrify.R
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.databinding.FragmentStatisticsBinding
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModel()
    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var auth: FirebaseAuth
    private var dayValue: ArrayList<String> = ArrayList()
    private var caloriesValue: ArrayList<Int> = ArrayList()
    private lateinit var dateLocalFormat: String
    private var entries: ArrayList<Entry> = ArrayList()
    private var mapFood: MutableMap<String, Int> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val dateLocal = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        dateLocalFormat = dateLocal.format(formatter)

        viewModel.id = currentUser?.uid.toString()
        viewModel.date = dateLocalFormat


        changeButtonColor(b = true, b1 = false, b2 = false, b3 = false)
        getCaloriesPerDay(7, 1)

        binding.btnWeek1.apply {
            text = getTextButton(7, 1)
            setOnClickListener {
                changeButtonColor(b = true, b1 = false, b2 = false, b3 = false)
                getCaloriesPerDay(7, 1)
            }
        }
        binding.btnWeek2.apply {
            text = getTextButton(14, 8)
            setOnClickListener {
                changeButtonColor(b = false, b1 = true, b2 = false, b3 = false)
                getCaloriesPerDay(14, 8)
            }
        }
        binding.btnWeek3.apply {
            text = getTextButton(21, 15)
            setOnClickListener {
                changeButtonColor(b = false, b1 = false, b2 = true, b3 = false)
                getCaloriesPerDay(21, 15)
            }
        }
        binding.btnWeek4.apply {
            text = getTextButton(28, 22)
            setOnClickListener {
                changeButtonColor(b = false, b1 = false, b2 = false, b3 = true)
                getCaloriesPerDay(28, 22)
            }
        }
//        monthYearPicker()
        getConsumingChart()

    }

    private fun getConsumingChart() {
        val lowerDateLocal = LocalDateTime.now().minusDays(30.toLong())
        val upperDateLocal = LocalDateTime.now().minusDays(0.toLong())
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val lower = lowerDateLocal.format(formatter).toInt()
        val upper = upperDateLocal.format(formatter).toInt()
        viewModel.getStatisticFood(lower, upper).observe(viewLifecycleOwner, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> {
                        Log.d("getstats", "loading")
                    }
                    is Resource.Success -> {
                        Log.d("resource com", it.toString())
                        mapFood.clear()
                        it.data.map { data ->
                            data.foods.map { foodItem ->
                                if (mapFood.containsKey(foodItem.name)) {
                                    mapFood[foodItem.name] = 1 + mapFood[foodItem.name]!!
                                } else {
                                    mapFood[foodItem.name] = 1
                                }
                            }

                        }
                        showConsumingChart(mapFood)
                        Log.d("getstats com", "berhasil")
                    }
                    is Resource.Error -> {
                        Log.d("getstats", "error")
                    }
                }
            }
        })


    }

    private fun showConsumingChart(mapFood: MutableMap<String, Int>) {
        val mapFoodSorted = mapFood.toSortedMap()
        Log.d("FOOD", mapFoodSorted.toString())

        val xAxisValueFood = arrayListOf<String>()
        val valueFood = arrayListOf<Int>()
        mapFoodSorted.map {
            xAxisValueFood.add(it.key)
            valueFood.add(it.value)
        }

        val entries = ArrayList<BarEntry>()

        if (mapFoodSorted.size > 7) {
            for (a in 0 until 6) {
                entries.add(BarEntry(a.toFloat(), valueFood[a].toFloat()))
            }

        } else {
            for (a in 0 until xAxisValueFood.size) {
                entries.add(BarEntry(a.toFloat(), valueFood[a].toFloat()))
            }
        }

        val bc = BarDataSet(entries, "Consuming")
        bc.apply {
            color = R.color.green_500
        }

        binding.consumingChart.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValueFood)
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
            notifyDataSetChanged()
            invalidate()
        }

    }

    private fun changeButtonColor(b: Boolean, b1: Boolean, b2: Boolean, b3: Boolean) {
        binding.btnWeek1.setBackgroundColor(requireContext().getColor(R.color.grey))
        binding.btnWeek1.setTextColor(requireContext().getColor(R.color.material_on_background_emphasis_medium))
        binding.btnWeek2.setBackgroundColor(requireContext().getColor(R.color.grey))
        binding.btnWeek2.setTextColor(requireContext().getColor(R.color.material_on_background_emphasis_medium))
        binding.btnWeek3.setBackgroundColor(requireContext().getColor(R.color.grey))
        binding.btnWeek3.setTextColor(requireContext().getColor(R.color.material_on_background_emphasis_medium))
        binding.btnWeek4.setBackgroundColor(requireContext().getColor(R.color.grey))
        binding.btnWeek4.setTextColor(requireContext().getColor(R.color.material_on_background_emphasis_medium))
        when {
            b -> {
                binding.btnWeek1.setBackgroundColor(requireContext().getColor(R.color.green_500))
                binding.btnWeek1.setTextColor(requireContext().getColor(R.color.white))
            }
            b1 -> {
                binding.btnWeek2.setBackgroundColor(requireContext().getColor(R.color.green_500))
                binding.btnWeek2.setTextColor(requireContext().getColor(R.color.white))
            }
            b2 -> {
                binding.btnWeek3.setBackgroundColor(requireContext().getColor(R.color.green_500))
                binding.btnWeek3.setTextColor(requireContext().getColor(R.color.white))
            }
            b3 -> {
                binding.btnWeek4.setBackgroundColor(requireContext().getColor(R.color.green_500))
                binding.btnWeek4.setTextColor(requireContext().getColor(R.color.white))
            }
        }
    }

    private fun getTextButton(lower: Int, upper: Int): String {
        val lowerDateLocal = LocalDateTime.now().minusDays(lower.toLong())
        val upperDateLocal = LocalDateTime.now().minusDays(upper.toLong())
        val formatterDate = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
        val lowerDate = lowerDateLocal.format(formatterDate)
        val upperDate = upperDateLocal.format(formatterDate)
        return getString(R.string.week_button, lowerDate, upperDate)
    }

    @SuppressLint("SimpleDateFormat")
//    private fun monthYearPicker() {
//        val calendar = Calendar.getInstance()
//        val monthSelected = calendar.get(Calendar.MONTH)
//        val yearSelected = calendar.get(Calendar.YEAR)
//
//        val dialogFragment = MonthYearPickerDialogFragment.getInstance(
//            monthSelected, yearSelected, "Select"
//        )
//        binding.btnMonthPicker.setOnClickListener {
//            activity?.let { dialogFragment.show(it.supportFragmentManager, null) }
//            dialogFragment.setOnDateSetListener { year, monthOfYear ->
//                val monthFormat = SimpleDateFormat("MMMM")
//                calendar.set(Calendar.MONTH, monthOfYear)
//                val month = monthFormat.format(calendar.time)
//                binding.btnMonthPicker.text = "$month $year"
//            }
//        }
//    }

    private fun getCaloriesPerDay(lowerLimit: Int, upperLimit: Int) {
        val lowerDateLocal = LocalDateTime.now().minusDays(lowerLimit.toLong())
        val upperDateLocal = LocalDateTime.now().minusDays(upperLimit.toLong())
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val lower = lowerDateLocal.format(formatter).toInt()
        val upper = upperDateLocal.format(formatter).toInt()
        viewModel.getTotalCaloriesPerDay(lower, upper).observe(viewLifecycleOwner, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> {
                        Log.d("getstats", "loading")
                    }
                    is Resource.Success -> {
                        Log.d("resource", it.toString())
                        caloriesValue.clear()
                        dayValue.clear()
                        entries.clear()
                        it.data.map { data ->
                            caloriesValue.add(data.total)
                            dayValue.add(data.day_name)
                            Log.d("getstats", "ini didalam iterasi")
                        }

                        showCaloriesChart()
                        Log.d("getstats", caloriesValue.toString())
                        Log.d("getstats", dayValue.toString())
                        Log.d("getstats", "berhasil")
                    }
                    is Resource.Error -> {
                        Log.d("getstats", "error")
                    }
                }
            }
        })
    }

    private fun showCaloriesChart() {

        val xAxisValue = dayValue

        if (dayValue.isNotEmpty()) {
            for (a in 0 until dayValue.size) {
                entries.add(Entry(a.toFloat(), caloriesValue[a].toFloat()))
                Log.d("TAG", a.toString())
                Log.d("TAG", caloriesValue[a].toString())
            }
        }

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
            notifyDataSetChanged()
            invalidate()
        }
    }
}