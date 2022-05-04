package com.exemplo.task.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.exemplo.task.R
import com.exemplo.task.databinding.FragmentHomeBinding
import com.exemplo.task.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        configTabLayout()

        initClicks()
    }

    private fun initClicks() {


        binding.ibLogout.setOnClickListener {
            logoutApp()
        }
    }

    private fun logoutApp() {
        auth.signOut()

        findNavController().navigate(R.id.action_homeFragment_to_authentication)
    }

    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        adapter.addFragment(TodoFragment(), getString(R.string.text_todo))
        adapter.addFragment(DoingFragment(), getString(R.string.text_doing))
        adapter.addFragment(DoneFragment(), getString(R.string.text_done))

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        TabLayoutMediator(binding.tabs, binding.viewPager) {tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}