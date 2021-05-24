package com.yusril.nutrify.ui.profile.edtprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.ActivityEditProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener, DatePickerFragment.DialogDateListener {
    private lateinit var binding: ActivityEditProfileBinding

    companion object{
        private const val DATE_PICKER_TAG = "DatePicker"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnInputBirth.setOnClickListener(this)

        var genderList: Array<String> = resources.getStringArray(R.array.gender)
        val adapter = ArrayAdapter(this,R.layout.drop_down_item, genderList)

        binding.inputGender.setAdapter(adapter)

    }

    override fun onClick(v: View){
        when(v.id){
            R.id.btn_input_birth ->{
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int){
        val calendar = Calendar.getInstance()
        calendar.set(year,month,dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.tvBirthDate.text = dateFormat.format(calendar.time)
    }
}