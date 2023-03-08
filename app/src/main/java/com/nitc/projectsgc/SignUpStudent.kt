package com.nitc.projectsgc

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.View
import android.widget.*
import com.nitc.projectsgc.databinding.ActivitySignUpStudentBinding
import java.text.ParseException
import java.text.SimpleDateFormat

class SignUpStudent : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivitySignUpStudentBinding
    lateinit var studentGender: Spinner
    lateinit var selectedGenderTextView: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )

        studentGender = findViewById(R.id.gender_spinner)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        studentGender.adapter = arrayAdapter
        binding.signUpButton.setOnClickListener{
            var nameInput = binding.nameField.text.toString()
            var emailInput = binding.emailField.text.toString()
            var passwordInput = binding.passwordField.text.toString()
            var phoneNumber = binding.phoneNumber.text.toString()
            var dateOfBirth = binding.dateofBirth.dayOfMonth.toString()+"/"+binding.dateofBirth.month.toString()+"/"+binding.dateofBirth.year.toString()
//            selectedGenderTextView = findViewById(R.id.)
            studentGender.onItemSelectedListener = this
            selectedGenderTextView = studentGender.toString()


            if(nameInput.isEmpty()){
                binding.nameField.error = "Name field cannot be empty"
                binding.nameField.requestFocus()
                return@setOnClickListener
            }
            if(emailInput.isEmpty()){
                binding.emailField.error = "Email field cannot be empty"
                binding.emailField.requestFocus()
                return@setOnClickListener
            }
            if(passwordInput.length < 8){
                binding.passwordField.error = "Password should contain at least 8 characters"
                binding.passwordField.requestFocus()
                return@setOnClickListener
            }

            phoneNumber = phoneNumber.trim()
            if(phoneNumber.length <10 || phoneNumber.length >10){
                phoneNumber.trim()
                binding.phoneNumber.error = "Phone number should be 10 digits only"
                binding.phoneNumber.requestFocus()
                return@setOnClickListener
            }else if (!isDigitsOnly(phoneNumber)){
                binding.phoneNumber.setText("")
                Toast.makeText(this,"Oops !! you entered phone number in wrong format",Toast.LENGTH_LONG).show()
                binding.phoneNumber.requestFocus()
                return@setOnClickListener
            }

            val checked = binding.tncBox
            if(!checked.isChecked){
                Toast.makeText(this,"You have to accept the terms and conditions",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            Toast.makeText(this,"Email = $emailInput \n" +
                    " password = $passwordInput \n"+
                    "phone = $phoneNumber \n"+
                    "name = $nameInput \n"+
                    "D.O.B = $dateOfBirth\n"+
                    "Gender = $selectedGenderTextView \n"
                ,Toast.LENGTH_LONG).show()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun checkDOB(dateString: String): Boolean {
        val regex = """^\d{2}/\d{2}/\d{4}$""".toRegex()
        if (!regex.matches(dateString)) {
            return false
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.isLenient = false
        try {
            dateFormat.parse(dateString)
        } catch (e: ParseException) {
            return false
        }
        return true
    }

    fun isDigitsOnly(str: String): Boolean {
        return str.matches(Regex("[0-9]+"))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            selectedGenderTextView = parent.getItemAtPosition(position).toString()
//            return selectedGenderTextView
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}