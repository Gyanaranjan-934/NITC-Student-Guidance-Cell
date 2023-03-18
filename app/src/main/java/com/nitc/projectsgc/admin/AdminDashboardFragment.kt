package com.nitc.projectsgc.admin

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.R
import com.nitc.projectsgc.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {
    lateinit var binding : FragmentAdminDashboardBinding
    var userType = "Student"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentAdminDashboardBinding.inflate(inflater, container, false)


        binding.mentorLoginTypeButtonInAdminDashboardFragment.setOnClickListener {
            binding.mentorLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.mentorLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.WHITE)
            binding.studentLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.studentLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            binding.mentorLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            userType = "Mentor"
        }
        binding.studentLoginTypeButtonInAdminDashboardFragment.setOnClickListener {
            binding.mentorLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            binding.studentLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.studentLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.WHITE)
            binding.mentorLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            userType = "Student"
        }






        binding.addMentorButtonInAdminDashboard.setOnClickListener{
            findNavController().navigate(R.id.addMentorFragment)
        }


        return binding.root
    }


}