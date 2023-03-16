package com.nitc.projectsgc.signin

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SignUpActivity
import com.nitc.projectsgc.booking.BookingFragment
import com.nitc.projectsgc.databinding.FragmentLoginBinding
import com.nitc.projectsgc.register.RegisterFragment

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference : DatabaseReference = database.reference


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
//            reference.addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
////                    perform data retrieving in this method
//                    var userName : String = snapshot.child ("students").child("name").value as String
//                    Toast.makeText(requireContext(),"Name is right and name is $userName",Toast.LENGTH_LONG).show()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
////                    action taken if data not retrieved
//                }
//
//            })
            findNavController().navigate(R.id.bookingFragment)
        }

        binding.signUpButton.setOnClickListener{
            val fragManager = requireActivity().supportFragmentManager
            val transaction = fragManager.beginTransaction()
            transaction.replace(
                R.id.navHostFragment,
                RegisterFragment()
            )
            transaction.addToBackStack(null) // if u want this fragment to stay in stack specify it
            transaction.commit()

        }
    }
}
