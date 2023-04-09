package com.nitc.projectsgc.student.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentProfileBinding

class StudentProfileFragment: Fragment() {

    lateinit var binding:FragmentStudentProfileBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentProfileBinding.inflate(inflater,container,false)
        if(sharedViewModel.userType == "Student" || sharedViewModel.userType == "Admin") {
            binding.headingTVStudentProfileFragment.text = sharedViewModel.currentStudent.name
            binding.nameFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.name)
            binding.emailFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.emailId)
            binding.phoneNumberStudentProfileFragment.setText(sharedViewModel.currentStudent.phoneNumber)
            binding.passwordFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.password)
            binding.emailFieldStudentProfileFragment.isEnabled = false

        }else{
            binding.passwordInputCardStudentProfileFragment.visibility = View.GONE
            binding.updateButtonStudentProfileFragment.visibility = View.GONE

        }
        binding.updateButtonStudentProfileFragment.visibility = View.VISIBLE


        binding.updateButtonStudentProfileFragment

        return binding.root
    }
}