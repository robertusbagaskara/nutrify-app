package com.yusril.nutrify.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.yusril.nutrify.R
import com.yusril.nutrify.databinding.FragmentProfileBinding
import com.yusril.nutrify.ui.MainActivity
import com.yusril.nutrify.ui.auth.login.LoginActivity
import com.yusril.nutrify.ui.profile.edtprofile.EditProfileActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val name = user?.displayName

        binding.tvName.text = name
        binding.tvWeight.text = resources.getString(R.string.weight, 70)
        binding.tvHeight.text = resources.getString(R.string.height, 169)
        binding.tvBirthDate.text = "03-12-1999"

        binding.btnSignout.setOnClickListener {
            showLogoutAlert()
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(context?.applicationContext, EditProfileActivity::class.java)
            startActivity(intent)
        }

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
//        MaterialAlertDialogBuilder(context?.applicationContext!!)
//            .setTitle(resources.getString(R.string.sign_out))
//            .setMessage(resources.getString(R.string.confirm_sign_out))
//            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setPositiveButton(resources.getString(R.string.sign_out)) { _, _ ->
//                signOut()
//            }
//            .show()
    }
}