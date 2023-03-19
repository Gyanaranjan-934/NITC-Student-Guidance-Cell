package com.nitc.projectsgc.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.Login.access.LoginAccess
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentDashBoardBinding
import com.nitc.projectsgc.student.access.BasicStudentAccess

class StudentDashboardFragment: Fragment() {
    private val sharedViewModel:SharedViewModel by activityViewModels()

    lateinit var binding:FragmentStudentDashBoardBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentDashBoardBinding.inflate(inflater,container,false)

        var studentLive = context?.let { BasicStudentAccess(it,sharedViewModel).getStudent() }
        studentLive!!.observe(viewLifecycleOwner){student->
            if(student != null){
                sharedViewModel.currentStudent = student
            }
        }
        binding.logoutButtonInStudentDashboardFragment.setOnClickListener {
            var logoutLive = context?.let { it1 -> LoginAccess(it1,this,sharedViewModel).logout() }
            logoutLive!!.observe(viewLifecycleOwner){logoutSuccess->
                if(logoutSuccess){
                    findNavController().navigate(R.id.loginFragment)
                }
            }

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