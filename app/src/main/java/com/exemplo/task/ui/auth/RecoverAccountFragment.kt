package com.exemplo.task.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.exemplo.task.R
import com.exemplo.task.databinding.FragmentRecoverAccountBinding
import com.exemplo.task.helper.BaseFragment
import com.exemplo.task.helper.FirebaseHelper
import com.exemplo.task.helper.initToolbar
import com.exemplo.task.helper.showBottomSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment: BaseFragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.btnSend.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val email = binding.edtEmail.text.toString().trim()

        if(email.isNotEmpty()) {
            hideKeyboard()

            binding.progressBar.isVisible = true

            sendPasswordResetEmail(email)
        } else {
//            binding.edtEmail.requestFocus()
//            binding.edtEmail.error = getString(R.string.text_inform_your_email)
            showBottomSheet(null, R.string.text_button_bottom_sheet, getString(R.string.text_inform_your_email))
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
//                    Toast.makeText(requireContext(), getString(R.string.success_recover_email_msg, email), Toast.LENGTH_LONG).show()
                    showBottomSheet(null, R.string.text_ok, getString(R.string.success_recover_email_msg, email))
                } else {
//                    Toast.makeText(requireContext(), FirebaseHelper.validateErrors(task.exception?.message.toString()), Toast.LENGTH_LONG).show()
                    showBottomSheet(null, R.string.text_button_bottom_sheet, getString(FirebaseHelper.validateErrors(task.exception?.message.toString())))
                }

                binding.progressBar.isVisible = false
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}