package com.yusril.nutrify.ui.auth.login

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
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.ActivityLoginBinding
import com.yusril.nutrify.ui.MainActivity
import com.yusril.nutrify.ui.auth.register.RegisterActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private val inValidButtonColor = "#E9E9E9"
    private val validButtonColor = "#00796B"

    private val inputEmailVal = MutableStateFlow("")
    private val inputPasswordVal = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val formIsValid = combine(
            inputEmailVal, inputPasswordVal
        ) { email, password ->
            val isValidEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            val isValidPassword = password.length > 6


            if (!isValidEmail) {
                binding.inputEmail.error = getString(R.string.email_not_valid)
            }
            if (!isValidPassword) {
                binding.inputPassword.error = getString(R.string.password_not_valid)
            }

            isValidEmail && isValidPassword
        }

        with(binding) {
            inputEmail.doOnTextChanged { text, _, _, _ ->
                inputEmailVal.value = text.toString()
            }
            inputPassword.doOnTextChanged { text, _, _, _ ->
                inputPasswordVal.value = text.toString()
            }
        }

        lifecycleScope.launch {
            formIsValid.collect {
                binding.btnSignIn.apply {
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
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            btnLoading(true)
            login()
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

    private fun login() {
        val email = binding.inputEmail.text.toString().trim()
        val password = binding.inputPassword.text.toString().trim()
        Log.d("input", email + password)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT)
                        .show()
                    Log.d("login", "berhasil")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    btnLoading(false)
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("login", task.exception.toString())
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}