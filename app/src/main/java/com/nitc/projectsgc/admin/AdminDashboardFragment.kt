package com.nitc.projectsgc.admin

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.nitc.projectsgc.Login.access.LoginAccess
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.adapters.MentorsAdapter
import com.nitc.projectsgc.admin.adapters.StudentsAdapter
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.admin.adapters.AdminDashboardPagerAdapter
import com.nitc.projectsgc.databinding.FragmentAdminDashboardBinding
import com.nitc.projectsgc.mentors.adapters.MentorAppointmentsPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AdminDashboardFragment : Fragment() {
    lateinit var binding : FragmentAdminDashboardBinding
    var userType = "Student"
    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentAdminDashboardBinding.inflate(inflater, container, false)


        binding.tabLayoutInAdminDashboard.addTab(binding.tabLayoutInAdminDashboard.newTab().setText("Students"))
        binding.tabLayoutInAdminDashboard.addTab(binding.tabLayoutInAdminDashboard.newTab().setText("Mentors"))
        binding.viewPagerInAdminDashboardFragment.adapter = AdminDashboardPagerAdapter(childFragmentManager,lifecycle)


        TabLayoutMediator(binding.tabLayoutInAdminDashboard,binding.viewPagerInAdminDashboardFragment){tab,position->
            when(position){
                0-> tab.text = "Students"
                1-> tab.text = "Mentors"
            }
        }.attach()





        binding.logoutButtonInAdminDashboardFragment.setOnClickListener {
            var logoutSuccess = context?.let { it1 -> LoginAccess(it1,this,sharedViewModel).logout() }
            if(logoutSuccess == true){
                findNavController().navigate(R.id.loginFragment)
            }else{
                Toast.makeText(context,"Some error occurred. Try again", Toast.LENGTH_SHORT).show()
            }
//            }

        }



        val backCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Call a method in your Fragment to handle the navigation
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backCallback)
        return binding.root
    }


}