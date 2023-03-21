package com.nitc.projectsgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {
    lateinit var forgotPasswordBinding: FragmentForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        forgotPasswordBinding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        
        forgotPasswordBinding.forgotPasswordButtonInForgotPasswordFragment.setOnClickListener {
            var emailInput = forgotPasswordBinding.emailInputInForgotPasswordFragment.text.toString()
            var domain = emailInput.substring(emailInput.indexOf("@")+1,emailInput.length)
            if(domain != "nitc.ac.in"){
                forgotPasswordBinding.emailInputInForgotPasswordFragment.error = "Invalid email"
                Toast.makeText(context, "Please give your valid nitc email", Toast.LENGTH_SHORT).show()
                forgotPasswordBinding.emailInputInForgotPasswordFragment.requestFocus()
            }
            var userId = emailInput.substring(emailInput.indexOf("_")+1,emailInput.indexOf("@"))
            var auth = FirebaseAuth.getInstance()
            var reference = FirebaseDatabase.getInstance().reference.child("students")
            auth.sendPasswordResetEmail(emailInput)
                .addOnSuccessListener {
                    Toast.makeText(context, "Please check your email and click the link to reset the password", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.loginFragment)
                }
                .addOnFailureListener {
                    Toast.makeText(context,"You are not registered , please sign up to proceed further", Toast.LENGTH_SHORT).show()
                }
        }

        
        return forgotPasswordBinding.root
    }
    
}