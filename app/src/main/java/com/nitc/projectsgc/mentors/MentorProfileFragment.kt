package com.nitc.projectsgc.mentors

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.AddMentorAccess
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.databinding.FragmentMentorProfileBinding
import kotlinx.coroutines.*

class MentorProfileFragment: Fragment() {
    private val sharedViewModel:SharedViewModel by activityViewModels()
    lateinit var binding:FragmentMentorProfileBinding
    lateinit var mentorLive:LiveData<Mentor>
    var oldPassword = "NA"
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
                            binding.mentorTypeInputInMentorProfileFragment.isEnabled = false
                            binding.emailFieldInMentorProfileFragment.isEnabled = false
                        }
                        "Mentor" -> {
                            binding.passwordInputLayoutInMentorProfileFragment.visibility =
                                View.VISIBLE
                            binding.updateButtonInMentorProfileFragment.visibility = View.VISIBLE
                            binding.nameFieldInMentorProfileFragment.isEnabled = false
                            binding.mentorTypeInputInMentorProfileFragment.isEnabled = false
                            binding.emailFieldInMentorProfileFragment.isEnabled = false
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
                    oldPassword = mentor.password.toString()

                    binding.updateButtonInMentorProfileFragment.setOnClickListener{
                        val nameOfMentor = binding.nameFieldInMentorProfileFragment.text.toString()
                        var phoneNumberOfMentor = binding.phoneNumberInMentorProfileFragment.text.toString()
                        val emailOfMentor = binding.emailFieldInMentorProfileFragment.text.toString()
                        var passwordOfMentor = binding.passwordFieldInMentorProfileFragment.text.toString()

                        if(nameOfMentor.isEmpty()){
                            binding.nameFieldInMentorProfileFragment.error = "Name field cannot be empty"
                            binding.nameFieldInMentorProfileFragment.requestFocus()
                            return@setOnClickListener
                        }
                        if(emailOfMentor.isEmpty()){
                            binding.emailFieldInMentorProfileFragment.error = "Email field cannot be empty"
                            binding.emailFieldInMentorProfileFragment.requestFocus()
                            return@setOnClickListener
                        }
                        if(!checkDomain(emailOfMentor)){
                            binding.emailFieldInMentorProfileFragment.error = "Email should be a valid nitc email"
                            binding.emailFieldInMentorProfileFragment.requestFocus()
                            return@setOnClickListener
                        }
                        if(passwordOfMentor.length < 8){
                            binding.passwordFieldInMentorProfileFragment.error = "Password should contain at least 8 characters"
                            binding.passwordFieldInMentorProfileFragment.requestFocus()
                            return@setOnClickListener
                        }

                        phoneNumberOfMentor = phoneNumberOfMentor.trim()
                        if(phoneNumberOfMentor.length <10 || phoneNumberOfMentor.length >10){
                            phoneNumberOfMentor.trim()
                            binding.phoneNumberInMentorProfileFragment.error = "Phone number should be 10 digits only"
                            binding.phoneNumberInMentorProfileFragment.requestFocus()
                            return@setOnClickListener
                        }else if (!isDigitsOnly(phoneNumberOfMentor)){
                            binding.phoneNumberInMentorProfileFragment.setText("")
                            Toast.makeText(context,"Oops !! you entered phone number in wrong format", Toast.LENGTH_LONG).show()
                            binding.phoneNumberInMentorProfileFragment.requestFocus()
                            return@setOnClickListener
                        }

                        var mentorTypeSelected = binding.mentorTypeInputInMentorProfileFragment.text.toString().trim()

                        val userName = emailOfMentor.substring(0,emailOfMentor.indexOf("@"))
                        val mentor = Mentor(
                            nameOfMentor,
                            phoneNumberOfMentor,
                            emailOfMentor,
                            mentorTypeSelected,
                            passwordOfMentor,
                            userName
                        )
                        var updateCoroutineScope = CoroutineScope(Dispatchers.Main)
                        val loadingDialog = Dialog(requireContext())
                        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
                        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        updateCoroutineScope.launch {
                            loadingDialog.create()
                            loadingDialog.show()
                            val updatedMentor =
                                AddMentorAccess(requireContext()).updateMentor(mentor,oldPassword)
                            loadingDialog.cancel()
                            updateCoroutineScope.cancel()
                                if (updatedMentor) {
                                    Toast.makeText(
                                        context,
                                        "Successfully updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(context,"Some error occurred. Try again", Toast.LENGTH_SHORT).show()
                                }

                        }

                    }


                }
            }
        }else{
            Toast.makeText(context,"Couldn't get details",Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }
    private fun checkDomain(emailInput: String): Boolean {
        val domain : String = emailInput.substring(emailInput.indexOf("@")+1,emailInput.length)
        if(domain != "nitc.ac.in")return false
        return  true
    }
    private fun isDigitsOnly(str: String): Boolean {
        return str.matches(Regex("[0-9]+"))
    }

}