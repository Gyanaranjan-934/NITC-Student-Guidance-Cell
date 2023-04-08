package com.nitc.projectsgc.mentors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.databinding.FragmentAddMentorBinding
import com.nitc.projectsgc.databinding.FragmentMentorProfileBinding

class MentorProfileFragment: Fragment() {
    private val sharedViewModel:SharedViewModel by activityViewModels()
    lateinit var binding:FragmentMentorProfileBinding
    lateinit var mentorLive:LiveData<Mentor>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMentorProfileBinding.inflate(inflater,container,false)

        mentorLive = if(sharedViewModel.userType == "Admin" || sharedViewModel.userType == "Student") MentorsAccess(requireContext()).getMentor(sharedViewModel.mentorTypeForProfile,sharedViewModel.mentorIDForProfile)
        else MentorsAccess(requireContext()).getMentor(sharedViewModel.currentMentor.type,sharedViewModel.currentUserID)
        if (mentorLive != null) {
            mentorLive.observe(viewLifecycleOwner) { mentor ->
                if (mentor != null) {
                    when (sharedViewModel.userType) {
                        "Admin" -> {
                            binding.passwordInputLayoutInMentorProfileFragment.visibility =
                                View.VISIBLE
                            binding.updateButtonInMentorProfileFragment.visibility = View.VISIBLE
                        }
                        "Mentor" -> {
                            binding.passwordInputLayoutInMentorProfileFragment.visibility =
                                View.VISIBLE
                            binding.updateButtonInMentorProfileFragment.visibility = View.VISIBLE
                            binding.nameFieldInMentorProfileFragment.isEnabled = false
                            binding.mentorTypeInputInMentorProfileFragment.isEnabled = false
                        }
                        else -> {
                            binding.passwordInputLayoutInMentorProfileFragment.visibility =
                                View.GONE
                            binding.updateButtonInMentorProfileFragment.visibility = View.GONE
                            binding.nameFieldInMentorProfileFragment.isEnabled = false
                            binding.emailFieldInMentorProfileFragment.isEnabled = false
                            binding.mentorTypeInputInMentorProfileFragment.isEnabled = false
                            binding.phoneNumberInMentorProfileFragment.isEnabled = false
                            binding.passwordFieldInMentorProfileFragment.isEnabled = false
                        }
                    }
                    binding.headingTVInMentorProfileFragment.text = mentor.name
                    binding.nameFieldInMentorProfileFragment.setText(mentor.name)
                    binding.emailFieldInMentorProfileFragment.setText(mentor.email)
                    binding.mentorTypeInputInMentorProfileFragment.setText(mentor.type)
                    binding.phoneNumberInMentorProfileFragment.setText(mentor.phone)
                    binding.passwordFieldInMentorProfileFragment.setText(mentor.password)
                }
            }
        }else{
            Toast.makeText(context,"Couldn't get details",Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

}