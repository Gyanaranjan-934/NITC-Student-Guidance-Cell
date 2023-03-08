package com.nitc.projectsgc.signin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SignUpActivity
import com.nitc.projectsgc.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        }

        binding.signUpButton.setOnClickListener{

        }
    }
}
