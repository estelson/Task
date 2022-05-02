package com.exemplo.task.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.exemplo.task.R
import com.exemplo.task.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment - Modo antigo com R.id.<componente>
        supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment // Com ViewBinding

    }
}