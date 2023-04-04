package com.nitc.projectsgc.alerts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentAllAlertsBinding

class AllAlertsFragment:Fragment() {

    lateinit var binding:FragmentAllAlertsBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllAlertsBinding.inflate(inflater,container,false)

        binding.viewPagerInAllAlertsFragment.adapter = AllAlertsAdapter(childFragmentManager,lifecycle)
        binding.tabLayoutInAllAlertsFragment.addTab(binding.tabLayoutInAllAlertsFragment.newTab().setText("Events"))
        binding.tabLayoutInAllAlertsFragment.addTab(binding.tabLayoutInAllAlertsFragment.newTab().setText("News"))

        Log.d("userType","in alerts user type = "+sharedViewModel.userType)
//        binding.tabLayoutInStudentDashboard.addTab(binding.tabLayoutInStudentDashboard.newTab().setText("Profile"))
        TabLayoutMediator(binding.tabLayoutInAllAlertsFragment,binding.viewPagerInAllAlertsFragment){tab,position->
            when(position){
                0-> tab.text = "Events"
                1-> tab.text = "News"
            }
        }.attach()

        return binding.root
    }

}