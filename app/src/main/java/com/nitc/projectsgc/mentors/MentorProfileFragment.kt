package com.nitc.projectsgc.mentors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.databinding.FragmentAddMentorBinding

class MentorProfileFragment: Fragment() {
    private val sharedViewModel:SharedViewModel by activityViewModels()
    lateinit var binding:FragmentAddMentorBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMentorBinding.inflate(inflater,container,false)

        var mentorLive = context?.let { MentorsAccess(it).getMentor(sharedViewModel.profileForMentorType,sharedViewModel.profileForMentorID) }
        if (mentorLive != null) {
            mentorLive.observe(viewLifecycleOwner) { mentor ->
                if (mentor != null) {
                    if(sharedViewModel.userType == "Admin") binding.passwordInputLayoutInAddMentorFragment.visibility = View.VISIBLE
                    else binding.passwordInputLayoutInAddMentorFragment.visibility = View.GONE
                    binding.addButtonInAddMentorFragment.visibility = View.GONE
                    binding.headingTVInAddMentorFragment.text = mentor.name
                    binding.nameFieldInAddMentorFragment.setText(mentor.name)
                    binding.nameFieldInAddMentorFragment.isEnabled = false
                    binding.emailFieldInAddMentorFragment.setText(mentor.email)
                    binding.emailFieldInAddMentorFragment.isEnabled = false
                    binding.mentorTypeInputInAddMentorFragment.setText(mentor.type)
                    binding.mentorTypeInputInAddMentorFragment.isEnabled = false
                    binding.phoneNumberInAddMentorFragment.setText(mentor.phone)
                    binding.phoneNumberInAddMentorFragment.isEnabled = false
                    binding.passwordFieldInAddMentorFragment.setText(mentor.password)
                    binding.passwordFieldInAddMentorFragment.isEnabled = false
                }
            }
        }
        return binding.root
    }

}