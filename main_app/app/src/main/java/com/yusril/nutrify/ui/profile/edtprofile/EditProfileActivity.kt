package com.yusril.nutrify.ui.profile.edtprofile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.yusril.nutrify.R
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.databinding.ActivityEditProfileBinding
import com.yusril.nutrify.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerFragment.DialogDateListener {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    var uid: String = ""
    val user: User = User()
    private val viewModel: ProfileViewModel by viewModel()

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(true)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        uid = currentUser?.uid.toString()
        viewModel.id = uid

        binding.btnInputBirth.setOnClickListener(this)
        binding.btnSave.setOnClickListener {
            setUserProfile(user)
        }


        val genderList: Array<String> = resources.getStringArray(R.array.gender)
        val adapter = ArrayAdapter(this, R.layout.drop_down_item, genderList)

        binding.inputGender.setAdapter(adapter)

        Log.d("viewmodel uid", viewModel.id)
        viewModel.getProfile().observe(this, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> {
                        showLoading(false)
                        populateProfile(it)
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        showError()
                    }
                }
            }
        })


    }

    private fun setUserProfile(inputUser: User) {
        lifecycleScope.launch {
            var _user = inputUser
            _user = User(
                binding.tvBirthDate.text.toString(),
                binding.inputHeight.text.toString().toInt(),
                binding.inputWeight.text.toString().toInt(),
                binding.inputGender.text.toString()
            )

            viewModel.setProfile(_user)
        }
    }

    private fun populateProfile(it: Resource.Success<User>) {
        binding.inputFullname.setText(auth.currentUser?.displayName)
        binding.inputEmail.setText(auth.currentUser?.email)

        binding.inputWeight.setText(it.data.weight.toString())
        binding.inputHeight.setText(it.data.height.toString())
        binding.inputGender.setText(it.data.gender)
        binding.tvBirthDate.text = it.data.birth_date
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.editProfileActivity.rootView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.editProfileActivity.rootView.visibility = View.VISIBLE
        }
    }

    private fun showError() {
        Toast.makeText(this, "Terjadi error", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_input_birth -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.tvBirthDate.text = dateFormat.format(calendar.time)
    }
}