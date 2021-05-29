package com.yusril.nutrify.ui.auth.register

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.ActivityRegisterBinding
import com.yusril.nutrify.ui.auth.login.LoginActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    private val inValidButtonColor = "#E9E9E9"
    private val validButtonColor = "#00796B"

    private val inputNameVal = MutableStateFlow("")
    private val inputEmailVal = MutableStateFlow("")
    private val inputPasswordVal = MutableStateFlow("")
    private val inputConfirmPasswordVal = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

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

            Log.d(
                "isValid",
                isValidName.toString() + isValidEmail.toString() + isValidPassword.toString() + isValidConfirmPassword.toString()
            )

            isValidName && isValidEmail && isValidPassword && isValidConfirmPassword
        }

        with(binding) {
            inputName.doOnTextChanged { text, _, _, _ ->
                inputNameVal.value = text.toString()
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

        lifecycleScope.launch {
            formIsValid.collect {
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


        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            btnLoading(true)
            register()
        }

        binding.btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun btnLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSignIn.text = ""
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnSignIn.text = getString(R.string.sign_in)
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
                    Log.d("register", "berhasil")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    btnLoading(false)
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("register", task.exception.toString())
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
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "User profile updated.")
                }
            }
    }

}