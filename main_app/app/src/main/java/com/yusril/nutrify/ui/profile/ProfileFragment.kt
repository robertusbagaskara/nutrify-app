package com.yusril.nutrify.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.yusril.nutrify.R
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.User
import com.yusril.nutrify.databinding.FragmentProfileBinding
import com.yusril.nutrify.ui.auth.login.LoginActivity
import com.yusril.nutrify.ui.profile.edtprofile.EditProfileActivity
import org.koin.android.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val name = user?.displayName

        viewModel.id = user?.uid.toString()
        viewModel.getProfile().observe(viewLifecycleOwner, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> {
                        showLoading(false)
                        populateProfile(it)
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        showError(it)
                    }
                }
            }
        })

        binding.tvName.text = name

        binding.btnSignout.setOnClickListener {
            showLogoutAlert()
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(context?.applicationContext, EditProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showError(it: Resource.Error<User>) {
        Toast.makeText(context?.applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.profileParent.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.profileParent.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun populateProfile(it: Resource.Success<User>) {
        binding.tvWeight.text = resources.getString(R.string.weight, it.data.weight)
        binding.tvHeight.text = resources.getString(R.string.height, it.data.height)
        binding.tvBirthDate.text = it.data.birth_date
    }

    private fun signOut() {
        auth.signOut()
        Intent(context?.applicationContext, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    private fun showLogoutAlert() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.sign_out))
                .setMessage(resources.getString(R.string.confirm_sign_out))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.sign_out)) { _, _ ->
                    signOut()
                }
                .show()
        }
    }
}