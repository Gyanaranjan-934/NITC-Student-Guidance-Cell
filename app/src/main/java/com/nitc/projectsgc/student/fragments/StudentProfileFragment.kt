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

    var changesMade = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentProfileBinding.inflate(inflater,container,false)
        binding.headingTVStudentProfileFragment.text = sharedViewModel.currentStudent.name
        binding.nameFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.name)
        binding.emailFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.emailId)
        binding.phoneNumberStudentProfileFragment.setText(sharedViewModel.currentStudent.phoneNumber)
        binding.passwordFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.password)
        binding.nameInputLayoutInStudentProfileFragment.isEnabled = false

        binding.updateButtonStudentProfileFragment.visibility = View.GONE
        binding.phoneCardStudentProfileFragment.setOnClickListener {
            binding.updateButtonStudentProfileFragment.visibility = View.VISIBLE
        }

        return binding.root
    }
}