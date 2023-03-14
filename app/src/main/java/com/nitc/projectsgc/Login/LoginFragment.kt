package com.nitc.projectsgc.Login

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.R
import com.nitc.projectsgc.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    var userType = "Student"

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
            binding.mentorLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            userType = "Admin"
        }
        binding.studentLoginTypeButtonInLoginFragment.setOnClickListener {
            binding.adminLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.adminLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            binding.studentLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.studentLoginTypeButtonInLoginFragment.setTextColor(Color.WHITE)
            binding.mentorLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            userType = "Student"
        }
        binding.mentorLoginTypeButtonInLoginFragment.setOnClickListener {
            binding.adminLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.adminLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            binding.studentLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.studentLoginTypeButtonInLoginFragment.setTextColor(Color.BLACK)
            binding.mentorLoginTypeImageInLoginFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.mentorLoginTypeButtonInLoginFragment.setTextColor(Color.WHITE)
            userType = "Mentor"
        }

        binding.signInButton.setOnClickListener {
            val emailInput = requireView().findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString()
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

            Toast.makeText(requireContext(),"Email entered is $emailInput and password entered is $passwordInput",Toast.LENGTH_LONG).show()

//            val fragManager = requireActivity().supportFragmentManager
//            val transaction = fragManager.beginTransaction()
//            transaction.replace(
//                R.id.frameMain,
//                BookingFragment()
//            )
//            transaction.addToBackStack(null) // if u want this fragment to stay in stack specify it
//            transaction.commit()
        }

        binding.signUpButton.setOnClickListener{
//            val fragManager = requireActivity().supportFragmentManager
//            val transaction = fragManager.beginTransaction()
//            transaction.replace(
//                R.id.frameMain,
//                RegisterFragment()
//            )
//            transaction.addToBackStack(null) // if u want this fragment to stay in stack specify it
//            transaction.commit()

            findNavController().navigate(R.id.registerFragment)

        }
    }
}
