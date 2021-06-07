package com.yusril.nutrify.ui.home

import android.os.Bundle
import android.util.Log
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
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.CaloryPerDay
import com.yusril.nutrify.core.domain.model.Food
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.databinding.FragmentHomeBinding
import com.yusril.nutrify.ui.profile.ProfileViewModel
import com.yusril.nutrify.ui.statistic.StatisticsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private val statViewModel: StatisticsViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var calendar: Calendar
    private lateinit var totalCaloriesPerDay: CaloryPerDay
    private var dateLocalFormat: String = ""
    private var dayLocalFormat: String = ""
    private var times: String = ""
    private var totalCalories: Int = 0
    private var userAge: Int = 0
    private var budget: Int = 0
    private var remaining: Int = 0
    private var entries: ArrayList<Entry> = ArrayList()

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

        calendar = Calendar.getInstance()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val dateLocal = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        dateLocalFormat = dateLocal.format(formatter)
        val currentTime = calendar.get(Calendar.HOUR_OF_DAY)
        times = currentTime.toString()

        val dayFormatter = DateTimeFormatter.ofPattern("EE", Locale.getDefault())
        dayLocalFormat = dateLocal.format(dayFormatter)

        viewModel.id = currentUser!!.uid
        viewModel.date = dateLocalFormat
        viewModel.time = times

        statViewModel.id = currentUser.uid
        statViewModel.date = dateLocalFormat

        profileViewModel.id = currentUser.uid

        binding.username.text = getString(R.string.welcome_greeting, currentUser.displayName)
        binding.caloriesTotal.text = totalCalories.toString()

        //setAlertCard
        profileViewModel.getProfile().observe(viewLifecycleOwner, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> Log.d("TAG", "Loading")
                    is Resource.Success -> {
                        loadingAlert(it.data)
                    }
                    is Resource.Error -> Log.d("TAG", "Loading")
                }
            }
        })
        showChart()

        binding.btnGenerateDummy.setOnClickListener {
            setStatistic()
        }
    }

    private fun loadingAlert(data: User) {
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(data.birth_date)
        val day = SimpleDateFormat("dd", Locale.getDefault()).format(startDate).toInt()
        val month = SimpleDateFormat("MM", Locale.getDefault()).format(startDate).toInt()
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(startDate).toInt()
        userAge = Period.between(
            LocalDate.of(year, month, day),
            LocalDate.now()
        ).years

        if (data.gender == "male") {
            budget = (((88.4 + 13.4 * data.weight) + (4.8 * data.height) - (5.68 * userAge)) * 1.55).toInt()
            binding.tvTotalBudget.text = budget.toString()
        } else {
            budget = (((447.6 + 9.25 * data.weight) + (3.1 * data.height) - (4.33 * userAge)) * 1.55).toInt()
            binding.tvTotalBudget.text = budget.toString()
        }
    }

    private fun setStatistic() {
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
            total_calories = Random.nextInt(100, 700),
            time = calendar.get(Calendar.HOUR_OF_DAY).toString(),
            date = dateLocalFormat.toInt()
        )
        lifecycleScope.launch {
            viewModel.setStatistic(listStats)
        }
    }

    private fun showChart() {
        runBlocking {
            viewModel.getStatisticToday().observe(viewLifecycleOwner, {
                if (it != null) {
                    when (it) {
                        is Resource.Loading -> {
                            Log.d("getstats", "loading")
                        }
                        is Resource.Success -> {
                            Log.d("resource", it.toString())
                            entries.add(
                                Entry(
                                    0f,
                                    0f
                                )
                            )
                            it.data.map { data ->
                                totalCalories += data.total_calories
                                entries.add(
                                    Entry(
                                        data.time.toInt().toFloat(),
                                        data.total_calories.toFloat()
                                    )
                                )
                            }

                            remaining = budget - totalCalories
                            if (remaining > 0) {
                                binding.tvTotalRemaining.text = remaining.toString()
                            } else {
                                binding.tvTotalRemaining.text = "0"
                            }
                            totalCaloriesPerDay =
                                CaloryPerDay(budget, totalCalories, remaining, dayLocalFormat, dateLocalFormat.toInt())
                            binding.caloriesTotal.text = totalCalories.toString()
                            GlobalScope.launch(Dispatchers.Default) {
                                statViewModel.setTotalCaloriesPerDay(totalCaloriesPerDay)
                            }

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
                                notifyDataSetChanged()
                                invalidate()
                            }
                            Log.d("getstats", "berhasil")
                        }
                        is Resource.Error -> {
                            Log.d("getstats", "error")
                        }
                    }
                }
            })
        }

    }
}