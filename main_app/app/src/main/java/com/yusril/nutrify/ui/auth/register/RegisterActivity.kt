package com.yusril.nutrify.ui.auth.register

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.yusril.nutrify.R
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.databinding.ActivityRegisterBinding
import com.yusril.nutrify.ui.auth.login.LoginActivity
import com.yusril.nutrify.ui.profile.ProfileViewModel
import com.yusril.nutrify.ui.profile.edtprofile.DatePickerFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity(),
    DatePickerFragment.DialogDateListener {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: ProfileViewModel by viewModel()

    private val inValidButtonColor = "#E9E9E9"
    private val validButtonColor = "#00796B"

    private val inputNameVal = MutableStateFlow("")
    private val inputHeightVal = MutableStateFlow("")
    private val inputWeightVal = MutableStateFlow("")
    private val inputGenderVal = MutableStateFlow("")
    private val inputDateVal = MutableStateFlow("")
    private val inputEmailVal = MutableStateFlow("")
    private val inputPasswordVal = MutableStateFlow("")
    private val inputConfirmPasswordVal = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.top.continueText.text = "Sign up to continue"
        auth = FirebaseAuth.getInstance()

        val genderList: Array<String> = resources.getStringArray(R.array.gender)
        val adapter = ArrayAdapter(this, R.layout.drop_down_item, genderList)
        binding.inputGender.setAdapter(adapter)

        binding.dateForm.setStartIconOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
        }

        val formIsValid = combine(
            inputNameVal, inputEmailVal, inputPasswordVal, inputConfirmPasswordVal
        ) { name, email, password, confirmPassword ->
            val isValidName = name.isNotEmpty()
            val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isValidPassword = password.length > 6
            val isValidConfirmPassword = (password == confirmPassword)

            if (!isValidName) {
                binding.inputName.error = getString(R.string.name_not_valid)
            }
            if (!isValidEmail) {
                binding.inputEmail.error = getString(R.string.email_not_valid)
            }
            if (!isValidPassword) {
                binding.inputPassword.error = getString(R.string.password_not_valid)
            }
            if (!isValidConfirmPassword) {
                binding.inputConfirmPassword.error = getString(R.string.password_not_same)
            }

            isValidName && isValidEmail && isValidPassword && isValidConfirmPassword
        }


        with(binding) {
            inputName.doOnTextChanged { text, _, _, _ ->
                inputNameVal.value = text.toString()
            }
            inputHeight.doOnTextChanged { text, _, _, _ ->
                inputHeightVal.value = text.toString()
            }
            inputWeight.doOnTextChanged { text, _, _, _ ->
                inputWeightVal.value = text.toString()
            }
            inputGender.doOnTextChanged { text, _, _, _ ->
                inputGenderVal.value = text.toString()
            }
            inputEmail.doOnTextChanged { text, _, _, _ ->
                inputEmailVal.value = text.toString()
            }
            inputPassword.doOnTextChanged { text, _, _, _ ->
                inputPasswordVal.value = text.toString()
            }
            inputConfirmPassword.doOnTextChanged { text, _, _, _ ->
                inputConfirmPasswordVal.value = text.toString()
            }
        }

        binding.btnSignUp.setOnClickListener {
            btnLoading(true)
            register()
        }

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            formIsValid.collect {
                    Log.d("it", it.toString())
                    binding.btnSignUp.apply {
                        backgroundTintList = ColorStateList.valueOf(
                            Color.parseColor(
                                if (it) validButtonColor else inValidButtonColor
                            )
                        )
                        isClickable = it
                    }
            }
        }

    }

    private fun btnLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSignUp.text = ""
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnSignUp.text = getString(R.string.sign_up)
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun register() {
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    profileUpdate()
                    Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    btnLoading(false)
                    binding.textError.apply {
                        text = task.exception?.message
                        visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun profileUpdate() {
        val name = binding.inputName.text.toString().trim()
        val user = auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }
        user!!.updateProfile(profileUpdates)

        val inputUser = User(
            binding.inputDate.text.toString(),
            binding.inputHeight.text.toString().toInt(),
            binding.inputWeight.text.toString().toInt(),
            binding.inputGender.text.toString()
        )
        viewModel.id = user.uid
        lifecycleScope.launch {
            viewModel.setProfile(inputUser)
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.inputDate.doOnTextChanged { _, _, _, _ ->
            inputDateVal.value = dateFormat.format(calendar.time)
        }
        binding.inputDate.setText(dateFormat.format(calendar.time))
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
    }


}