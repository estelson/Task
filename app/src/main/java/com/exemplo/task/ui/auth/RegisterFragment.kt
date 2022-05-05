package com.exemplo.task.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.exemplo.task.R
import com.exemplo.task.databinding.FragmentRegisterBinding
import com.exemplo.task.helper.BaseFragment
import com.exemplo.task.helper.FirebaseHelper
import com.exemplo.task.helper.initToolbar
import com.exemplo.task.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment: BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        // Initialize Firebase Auth
        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.btnRegister.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if(email.isNotEmpty()) {
            if(password.isNotEmpty()) {
                hideKeyboard()

                binding.progressBar.isVisible = true

                registerUser(email, password)
            } else {
                //                binding.edtPassword.requestFocus()
                //                binding.edtPassword.error = getString(R.string.text_inform_your_password)
                showBottomSheet(null, R.string.text_button_bottom_sheet, getString(R.string.text_inform_your_password))
            }
        } else {
            //            binding.edtEmail.requestFocus()
            //            binding.edtEmail.error = getString(R.string.text_inform_your_email)
            showBottomSheet(null, R.string.text_button_bottom_sheet, getString(R.string.text_inform_your_email))
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeFragment)
                } else {
//                    Toast.makeText(requireContext(), FirebaseHelper.validateErrors(task.exception?.message.toString()), Toast.LENGTH_LONG).show()
                    showBottomSheet(null, R.string.text_button_bottom_sheet, getString(FirebaseHelper.validateErrors(task.exception?.message.toString())))

                    binding.progressBar.isVisible = false
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}