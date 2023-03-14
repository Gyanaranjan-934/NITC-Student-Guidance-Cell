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
import com.nitc.projectsgc.Students
import com.nitc.projectsgc.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment(), AdapterView.OnItemSelectedListener {
    lateinit var registerBinding: FragmentRegisterBinding
    lateinit var studentGender:Spinner
    lateinit var selectedGenderTextView: String

//    creating database reference object
    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var reference : DatabaseReference = database.reference.child("students")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        registerBinding = FragmentRegisterBinding.inflate(inflater,container,false)
        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        studentGender = registerBinding.genderSpinnerInRegisterFragment
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        studentGender.adapter = arrayAdapter
        studentGender.onItemSelectedListener = this

        registerBinding.signUpButtonInRegisterFragment.setOnClickListener{
            val nameInput = registerBinding.nameFieldInRegisterFragment.text.toString()
            val emailInput = registerBinding.emailFieldInRegisterFragment.text.toString()
            val passwordInput = registerBinding.passwordFieldInRegisterFragment.text.toString()
            var phoneNumber = registerBinding.phoneNumberInRegisterFragment.text.toString()
            val dateOfBirth = registerBinding.dateOfBirthInRegisterFragment.dayOfMonth.toString()+"/"+registerBinding.dateOfBirthInRegisterFragment.month.toString()+"/"+registerBinding.dateOfBirthInRegisterFragment.year.toString()

            if(nameInput.isEmpty()){
                registerBinding.nameFieldInRegisterFragment.error = "Name field cannot be empty"
                registerBinding.nameFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }
            if(emailInput.isEmpty()){
                registerBinding.emailFieldInRegisterFragment.error = "Email field cannot be empty"
                registerBinding.emailFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }
            if(!checkDomain(emailInput)){
                registerBinding.emailFieldInRegisterFragment.error = "Email should be a valid nitc email"
                registerBinding.emailFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }
            if(passwordInput.length < 8){
                registerBinding.passwordFieldInRegisterFragment.error = "Password should contain at least 8 characters"
                registerBinding.passwordFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }

            phoneNumber = phoneNumber.trim()
            if(phoneNumber.length <10 || phoneNumber.length >10){
                phoneNumber.trim()
                registerBinding.phoneNumberInRegisterFragment.error = "Phone number should be 10 digits only"
                registerBinding.phoneNumberInRegisterFragment.requestFocus()
                return@setOnClickListener
            }else if (!isDigitsOnly(phoneNumber)){
                registerBinding.phoneNumberInRegisterFragment.setText("")
                Toast.makeText(context,"Oops !! you entered phone number in wrong format", Toast.LENGTH_LONG).show()
                registerBinding.phoneNumberInRegisterFragment.requestFocus()
                return@setOnClickListener
            }

            val checked = registerBinding.tncBoxInRegisterFragment
            if(!checked.isChecked){
                Toast.makeText(context,"You have to accept the terms and conditions", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

//
//            Toast.makeText(context,"Email = $emailInput \n" +
//                    " password = $passwordInput \n"+
//                    "phone = $phoneNumber \n"+
//                    "name = $nameInput \n"+
//                    "D.O.B = $dateOfBirth\n"+
//                    "Gender = $selectedGenderTextView \n"
//                , Toast.LENGTH_LONG).show()

            val rollNo = emailInput.substring(emailInput.indexOf("_") + 1, emailInput.indexOf("@"))

            val student = Students(rollNo,nameInput,dateOfBirth,emailInput,selectedGenderTextView,passwordInput,phoneNumber)

            reference.child(rollNo).setValue(student).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    Toast.makeText(requireContext(),"The student has been added to the database",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.bookingFragment)
                }else{
                    Toast.makeText(requireContext(),task.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }

            findNavController().navigate(R.id.bookingFragment)

        }

        registerBinding.signInButtonInRegisterFragment.setOnClickListener{
            findNavController().navigate(R.id.loginFragment)
        }
        // Inflate the layout for this fragment
        return registerBinding.root
    }

    private fun checkDomain(emailInput: String): Boolean {
        val domain : String = emailInput.substring(emailInput.indexOf("@")+1,emailInput.length)
        if(domain != "nitc.ac.in")return false
        return  true
    }

    fun addStudentsToDatabase(){

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
