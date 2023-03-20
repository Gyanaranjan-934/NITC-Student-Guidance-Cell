package com.nitc.projectsgc.Login

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.Login.access.LoginAccess
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    var userType = "Student"
    var mentorNumber = 0
    var mentorTypeSelected = "NA"
    private val sharedViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.adminLoginTypeButtonInLoginFragment.setOnClickListener {
            binding.adminLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.adminLoginTypeButtonInLoginFragment.setTextColor(Color.WHITE)
            binding.studentLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.studentLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            binding.mentorTypeButtonInLoginFragment.visibility = View.GONE
            binding.mentorLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            userType = "Admin"
        }
        binding.studentLoginTypeButtonInLoginFragment.setOnClickListener {
            binding.adminLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.adminLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            binding.studentLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.studentLoginTypeButtonInLoginFragment.setTextColor(Color.WHITE)
            binding.mentorTypeButtonInLoginFragment.visibility = View.GONE
            binding.mentorLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            userType = "Student"
        }
        binding.mentorLoginTypeButtonInLoginFragment.setOnClickListener {
            binding.adminLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.adminLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            binding.studentLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.studentLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            binding.mentorTypeButtonInLoginFragment.visibility = View.VISIBLE
            binding.mentorLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.mentorLoginTypeButtonInLoginFragment.setTextColor(Color.WHITE)
            userType = "Mentor"
        }

        binding.mentorTypeButtonInLoginFragment.setOnClickListener {
            val mentorTypesLive = context?.let { it1 -> MentorsAccess(it1).getMentorTypes() }
            mentorTypesLive?.observe(viewLifecycleOwner) { mentorTypes ->
                if (mentorTypes != null) {
                    val mentorTypeBuilder = AlertDialog.Builder(context)
                    mentorTypeBuilder.setTitle("Choose Mentor Type")
                    mentorTypeBuilder.setSingleChoiceItems(
                        mentorTypes.map { it }.toTypedArray(),
                        mentorNumber
                    ) { dialog, selectedIndex ->
                        mentorTypeSelected = mentorTypes[selectedIndex].toString()
                        binding.mentorTypeButtonInLoginFragment.text = mentorTypeSelected
                        mentorNumber = selectedIndex
                        mentorTypes.clear()
                        dialog.dismiss()
                    }
                    mentorTypeBuilder.setPositiveButton("Go") { dialog, which ->
                        mentorTypeSelected = mentorTypes[0].toString()
                        binding.mentorTypeButtonInLoginFragment.text = mentorTypeSelected
                        mentorTypes.clear()
                        dialog.dismiss()
                    }
                    mentorTypeBuilder.create().show()
                }
            }
        }
        binding.signInButton.setOnClickListener {
            var emailInput = requireView().findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
            val passwordInput = requireView().findViewById<EditText>(R.id.editTextTextPassword).text.toString()

            if(emailInput.isEmpty()){
                binding.editTextTextEmailAddress.error = "No email entered"
                binding.editTextTextEmailAddress.requestFocus()
                return@setOnClickListener
            }
            if(passwordInput.isEmpty()){
                binding.editTextTextPassword.error = "No password entered"
                binding.editTextTextPassword.requestFocus()
                return@setOnClickListener
            }

            if(userType == "Student"){
                val emailDomain = emailInput.substring(emailInput.indexOf("@")+1,emailInput.length)
                val rollNo = emailInput.substring(emailInput.indexOf("_")+1,emailInput.indexOf("@"))
                if(emailDomain != "nitc.ac.in"){
                    Toast.makeText(context,"User should login with NITC email id",Toast.LENGTH_SHORT).show()
                }else{
                    val loginLive = context?.let { it1 -> LoginAccess(it1,this,sharedViewModel).login(emailInput,passwordInput,userType,rollNo,"NA") }

                    loginLive!!.observe(viewLifecycleOwner){loginSuccess->
                        if(loginSuccess){
                            sharedViewModel.currentUserID = rollNo
                            sharedViewModel.userType = "Student"
                            Log.d("loginSuccess",loginSuccess.toString())
                            findNavController().navigate(R.id.studentDashBoardFragment)
                        }else{
                            Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
            else if(userType == "Admin"){
                Toast.makeText(requireContext(),"Email entered is $emailInput and password entered is $passwordInput",Toast.LENGTH_LONG).show()
                emailInput = emailInput.trim()
                if(emailInput=="admin@nitc.ac.in" && passwordInput=="admin@123"){
                    sharedViewModel.userType = "Admin"
                    findNavController().navigate(R.id.adminDashboardFragment)
                }
                else{
                    Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
                }
            }
            else if(userType == "Mentor"){
                val emailDomain = emailInput.substring(emailInput.indexOf("@")+1,emailInput.length)
                val userName = emailInput.substring(0,emailInput.indexOf("@"))
                if(emailDomain != "nitc.ac.in"){
                    Toast.makeText(context,"Mentor should login with NITC email id",Toast.LENGTH_SHORT).show()
                }else{
                    val loginLive = context?.let { it1 -> LoginAccess(it1,this,sharedViewModel).login(emailInput,passwordInput,userType,userName,mentorTypeSelected) }

                    loginLive!!.observe(viewLifecycleOwner){loginSuccess->
                        if(loginSuccess){
                            sharedViewModel.currentUserID = userName
                            sharedViewModel.userType = "Mentor"
                            findNavController().navigate(R.id.mentorDashboardFragment)
                        }else{
                            Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.signUpButton.setOnClickListener{
            findNavController().navigate(R.id.registerFragment)
        }
        val backCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Call a method in your Fragment to handle the navigation
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backCallback)
    }
}
