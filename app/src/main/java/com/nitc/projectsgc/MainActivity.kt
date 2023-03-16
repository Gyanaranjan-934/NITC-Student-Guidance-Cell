package com.nitc.projectsgc

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.nitc.projectsgc.Login.LoginFragment
import com.nitc.projectsgc.databinding.ActivityMainBinding
import com.nitc.projectsgc.register.RegisterFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val fragmentManager : FragmentManager  = supportFragmentManager
//        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()
//        val loginFragment = LoginFragment()
//
//        fragmentTransaction.add(R.id.navHostFragment,loginFragment)
//        fragmentTransaction.commit()

        binding.root
    }

}