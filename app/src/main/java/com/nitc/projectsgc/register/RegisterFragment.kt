package com.nitc.projectsgc.register

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student
import com.nitc.projectsgc.databinding.FragmentRegisterBinding
import com.nitc.projectsgc.register.access.RegisterAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Calendar

class RegisterFragment : Fragment(), AdapterView.OnItemSelectedListener {
    lateinit var registerBinding: FragmentRegisterBinding
    lateinit var studentGender:Spinner
    private val sharedViewModel:SharedViewModel by activityViewModels()
    lateinit var selectedGenderTextView: String

//    creating database reference object
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
        var dateOfBirth = ""
        val calendar = Calendar.getInstance()

        registerBinding.dateOfBirthInRegisterFragment.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ){view,year,monthOfYear,dayOfMonth->
            var monthNumber = monthOfYear + 1
            Log.d("monthNumber","month number before = "+monthNumber)
            if(monthNumber/10 == 0) monthNumber = "0${monthNumber.toString()}".toInt()
            Log.d("monthNumber","month numbe = "+monthNumber.toString())
            if(monthNumber < 10) dateOfBirth = "${registerBinding.dateOfBirthInRegisterFragment.dayOfMonth.toString()}/0${monthNumber.toString()}/${registerBinding.dateOfBirthInRegisterFragment.year.toString()}"
            else dateOfBirth = "${registerBinding.dateOfBirthInRegisterFragment.dayOfMonth.toString()}/${monthNumber.toString()}/${registerBinding.dateOfBirthInRegisterFragment.year.toString()}"
            Log.d("monthNumber","date of birth = "+dateOfBirth)

        }
        registerBinding.signUpButtonInRegisterFragment.setOnClickListener{
            val nameInput = registerBinding.nameFieldInRegisterFragment.text.toString()
            val emailInput = registerBinding.emailFieldInRegisterFragment.text.toString()
            val passwordInput = registerBinding.passwordFieldInRegisterFragment.text.toString()
            var phoneNumber = registerBinding.phoneNumberInRegisterFragment.text.toString()


            if(nameInput.isEmpty()){
                registerBinding.nameFieldInRegisterFragment.error = "Name field cannot be empty"
                registerBinding.nameFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }
            var names = nameInput.toString().trim().split(" ")
            var nameValid = true
            for (name in names){
                if(!isAlphabetic(name)){
                    nameValid = false
                    break
                }
            }
            if(!nameValid){
                registerBinding.nameFieldInRegisterFragment.error = "Name field shouldn't contain numbers"
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

            if(!verifyRollNO(rollNo)){
                registerBinding.emailFieldInRegisterFragment.error = "You have entered the roll no. in email incorrectly"
                registerBinding.emailFieldInRegisterFragment.requestFocus()
                return@setOnClickListener
            }

            val loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
            loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            Log.d("today","this called")
            val registerCoroutineScope = CoroutineScope(Dispatchers.Main)
            registerCoroutineScope.launch {
                val student = Student(rollNo,nameInput,dateOfBirth,emailInput,selectedGenderTextView,passwordInput,phoneNumber)
                loadingDialog.create()
                loadingDialog.show()
                val registerSuccess = RegisterAccess(requireContext()).register(student)
                loadingDialog.cancel()
                registerCoroutineScope.cancel()
                if(registerSuccess){
                    if(sharedViewModel.userType == "Admin") findNavController().navigate(R.id.adminDashboardFragment)
                    else findNavController().navigate(R.id.loginFragment)
                }
            }




//            findNavController().navigate(R.id.bookingFragment)

        }

        registerBinding.signInButtonInRegisterFragment.setOnClickListener{
            findNavController().navigate(R.id.loginFragment)
        }
//        registerBinding.f
    val backCallback = object : OnBackPressedCallback(true /* enabled by default */) {
        override fun handleOnBackPressed() {
            // Call a method in your Fragment to handle the navigation
            requireActivity().finish()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backCallback)
        // Inflate the layout for this fragment
        return registerBinding.root
    }

    private fun verifyRollNO(rollNo: String): Boolean {
        if(rollNo.length != 9)return false
        if(rollNo[0]!='p' && rollNo[0]!='m' && rollNo[0]!='b')return false
        val streamId = rollNo.substring(7)
        if(streamId.isDigitsOnly())return false
        if(streamId != streamId.lowercase())return false
        return true
    }

    private fun checkDomain(emailInput: String): Boolean {
        val domain : String = emailInput.substring(emailInput.indexOf("@")+1,emailInput.length)
        if(domain != "nitc.ac.in") return false
        return  true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements and set up event listeners here

    }
    fun isDigitsOnly(str: String): Boolean {
        return str.matches(Regex("[0-9]+"))
    }
    fun isAlphabetic(name: String): Boolean {
        return name.matches("[a-zA-Z]+".toRegex())
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
