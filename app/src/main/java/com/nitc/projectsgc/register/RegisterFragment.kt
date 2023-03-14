package com.nitc.projectsgc.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.R
import com.nitc.projectsgc.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(), AdapterView.OnItemSelectedListener {
    lateinit var binding: FragmentRegisterBinding
    lateinit var studentGender:Spinner
    lateinit var selectedGenderTextView: String
    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference : DatabaseReference = database.reference.child("students")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        studentGender = binding.genderSpinnerInRegisterFragment
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        studentGender.adapter = arrayAdapter
        studentGender.onItemSelectedListener = this

        binding.signUpButtonInRegisterFragment.setOnClickListener{
            val nameInput = binding.nameFieldInRegisterFragment.text.toString()
            val emailInput = binding.emailFieldInRegisterFragment.text.toString()
            val passwordInput = binding.passwordFieldInRegisterFragment.text.toString()
            var phoneNumber = binding.phoneNumberInRegisterFragment.text.toString()
            val dateOfBirth = binding.dateOfBirthInRegisterFragment.dayOfMonth.toString()+"/"+binding.dateOfBirthInRegisterFragment.month.toString()+"/"+binding.dateOfBirthInRegisterFragment.year.toString()

            if(nameInput.isEmpty()){
                binding.nameFieldInRegisterFragment.error = "Name field cannot be empty"
                binding.nameFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }
            if(emailInput.isEmpty()){
                binding.emailFieldInRegisterFragment.error = "Email field cannot be empty"
                binding.emailFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }
            if(passwordInput.length < 8){
                binding.passwordFieldInRegisterFragment.error = "Password should contain at least 8 characters"
                binding.passwordFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }

            phoneNumber = phoneNumber.trim()
            if(phoneNumber.length <10 || phoneNumber.length >10){
                phoneNumber.trim()
                binding.phoneNumberInRegisterFragment.error = "Phone number should be 10 digits only"
                binding.phoneNumberInRegisterFragment.requestFocus()
                return@setOnClickListener
            }else if (!isDigitsOnly(phoneNumber)){
                binding.phoneNumberInRegisterFragment.setText("")
                Toast.makeText(context,"Oops !! you entered phone number in wrong format", Toast.LENGTH_LONG).show()
                binding.phoneNumberInRegisterFragment.requestFocus()
                return@setOnClickListener
            }

            val checked = binding.tncBoxInRegisterFragment
            if(!checked.isChecked){
                Toast.makeText(context,"You have to accept the terms and conditions", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            Toast.makeText(context,"Email = $emailInput \n" +
                    " password = $passwordInput \n"+
                    "phone = $phoneNumber \n"+
                    "name = $nameInput \n"+
                    "D.O.B = $dateOfBirth\n"+
                    "Gender = $selectedGenderTextView \n"
                , Toast.LENGTH_LONG).show()
            reference.child("name").setValue(nameInput)
            reference.child("dateOfBirth").setValue(dateOfBirth)
            reference.child("emailId").setValue(emailInput)
            reference.child("Student_id").setValue(emailInput.split("_")[1].subSequence(0,8).toString())
            reference.child("phoneNumber").setValue(phoneNumber)
            reference.child("gender").setValue(selectedGenderTextView)
            reference.child("password").setValue(passwordInput)


//            val fragManager = requireActivity().supportFragmentManager
//            val transaction = fragManager.beginTransaction()
//            transaction.replace(
//                R.id.frameMain,
//                BookingFragment()
//            )
//            transaction.addToBackStack(null) // if u want this fragment to stay in stack specify it
//            transaction.commit()

        }

        binding.signInButtonInRegisterFragment.setOnClickListener{
//            val fragManager = requireActivity().supportFragmentManager
//            val transaction = fragManager.beginTransaction()
//            transaction.replace(
//                R.id.frameMain,
//                SignInFragment()
//            )
//            transaction.addToBackStack(null) // if u want this fragment to stay in stack specify it
//            transaction.commit()

            findNavController().navigate(R.id.loginFragment)



        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements and set up event listeners here

    }
    fun isDigitsOnly(str: String): Boolean {
        return str.matches(Regex("[0-9]+"))
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            selectedGenderTextView = parent.getItemAtPosition(position).toString()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
