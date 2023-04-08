package com.nitc.projectsgc.mentors

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.nitc.projectsgc.Login.access.LoginAccess
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentMentorDashboardBinding
import com.nitc.projectsgc.mentors.access.MentorAppointmentsAccess
import com.nitc.projectsgc.mentors.adapters.MentorAppointmentsAdapter
import com.nitc.projectsgc.mentors.adapters.MentorAppointmentsPagerAdapter
import com.nitc.projectsgc.student.adapters.StudentAppointmentsPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MentorDashboardFragment : Fragment() {
    private val sharedViewModel : SharedViewModel  by activityViewModels()
    lateinit var binding: FragmentMentorDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMentorDashboardBinding.inflate(inflater, container, false)

        binding.tabLayoutInMentorDashboard.addTab(binding.tabLayoutInMentorDashboard.newTab().setText("Appointments"))
        binding.tabLayoutInMentorDashboard.addTab(binding.tabLayoutInMentorDashboard.newTab().setText("Profile"))
        binding.viewPagerInMentorDashboard.adapter = MentorAppointmentsPagerAdapter(childFragmentManager,lifecycle)


        TabLayoutMediator(binding.tabLayoutInMentorDashboard,binding.viewPagerInMentorDashboard){tab,position->
            when(position){
                0-> tab.text = "Appointments"
                1-> tab.text = "Profile"
            }
        }.attach()


        binding.notificationsButtonInMentorDashboardFragment.setOnClickListener {
            findNavController().navigate(R.id.allAlertsFragment)
        }

        binding.logoutButtonInMentorDashboardFragment.setOnClickListener {
            var logoutSuccess =
                context?.let { it1 -> LoginAccess(it1, this, sharedViewModel).logout() }
            if (logoutSuccess == true) {
                findNavController().navigate(R.id.loginFragment)
            } else {
                Toast.makeText(context, "Some error occurred. Try again", Toast.LENGTH_SHORT).show()
            }
//            }

        }


        var backCallBack = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backCallBack)
        return binding.root
    }
}