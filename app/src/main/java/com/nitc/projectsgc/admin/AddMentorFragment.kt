package com.nitc.projectsgc.admin

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.R
import com.nitc.projectsgc.admin.access.AddMentorAccess
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.databinding.FragmentAddMentorBinding

class AddMentorFragment : Fragment() {
//    var database : FirebaseDatabase =
    lateinit var addMentorBinding: FragmentAddMentorBinding
    var mentorNumber = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addMentorBinding = FragmentAddMentorBinding.inflate(inflater, container, false)
        return addMentorBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mentorTypeSelected = "NA"
//        addMentorBinding.mentorTypeButtonInAddMentorFragment.setOnClickListener {
//            val mentorTypesLive = context?.let { it1 -> MentorsAccess(it1).getMentorTypes() }
//            mentorTypesLive?.observe(viewLifecycleOwner) { mentorTypes ->
//                if (mentorTypes != null) {
//                    val mentorTypeBuilder = AlertDialog.Builder(context)
//                    mentorTypeBuilder.setTitle("Choose Mentor Type")
//                    mentorTypeBuilder.setSingleChoiceItems(
//                        mentorTypes.map { it }.toTypedArray(),
//                        mentorNumber
//                    ) { dialog, selectedIndex ->
//                        mentorTypeSelected = mentorTypes[selectedIndex].toString()
//                        addMentorBinding.mentorTypeButtonInAddMentorFragment.text = mentorTypeSelected
//                        mentorNumber = selectedIndex
//                        mentorTypes.clear()
//                        dialog.dismiss()
//                    }
//                    mentorTypeBuilder.setPositiveButton("Go") { dialog, which ->
//                        mentorTypeSelected = mentorTypes[0].toString()
//                        addMentorBinding.mentorTypeButtonInAddMentorFragment.text = mentorTypeSelected
//                        mentorTypes.clear()
//                        dialog.dismiss()
//                    }
//                    mentorTypeBuilder.create().show()
//                }
//
//            }
//        }
        addMentorBinding.addButtonInAddMentorFragment.setOnClickListener{
            val nameOfMentor = addMentorBinding.nameFieldInAddMentorFragment.text.toString()
            var phoneNumberOfMentor = addMentorBinding.phoneNumberInAddMentorFragment.text.toString()
            val emailOfMentor = addMentorBinding.emailFieldInAddMentorFragment.text.toString()
            var passwordOfMentor = addMentorBinding.passwordFieldInAddMentorFragment.text.toString()

            if(nameOfMentor.isEmpty()){
                addMentorBinding.nameFieldInAddMentorFragment.error = "Name field cannot be empty"
                addMentorBinding.nameFieldInAddMentorFragment.requestFocus()
                return@setOnClickListener
            }
            if(emailOfMentor.isEmpty()){
                addMentorBinding.emailFieldInAddMentorFragment.error = "Email field cannot be empty"
                addMentorBinding.emailFieldInAddMentorFragment.requestFocus()
                return@setOnClickListener
            }
            if(!checkDomain(emailOfMentor)){
                addMentorBinding.emailFieldInAddMentorFragment.error = "Email should be a valid nitc email"
                addMentorBinding.emailFieldInAddMentorFragment.requestFocus()
                return@setOnClickListener
            }
            if(passwordOfMentor.length < 8){
                addMentorBinding.passwordFieldInAddMentorFragment.error = "Password should contain at least 8 characters"
                addMentorBinding.passwordFieldInAddMentorFragment.requestFocus()
                return@setOnClickListener
            }

            phoneNumberOfMentor = phoneNumberOfMentor.trim()
            if(phoneNumberOfMentor.length <10 || phoneNumberOfMentor.length >10){
                phoneNumberOfMentor.trim()
                addMentorBinding.phoneNumberInAddMentorFragment.error = "Phone number should be 10 digits only"
                addMentorBinding.phoneNumberInAddMentorFragment.requestFocus()
                return@setOnClickListener
            }else if (!isDigitsOnly(phoneNumberOfMentor)){
                addMentorBinding.phoneNumberInAddMentorFragment.setText("")
                Toast.makeText(context,"Oops !! you entered phone number in wrong format", Toast.LENGTH_LONG).show()
                addMentorBinding.phoneNumberInAddMentorFragment.requestFocus()
                return@setOnClickListener
            }

            mentorTypeSelected = addMentorBinding.mentorTypeInputInAddMentorFragment.text.toString().trim()
            if(mentorTypeSelected.isEmpty()){
                addMentorBinding.mentorTypeInputInAddMentorFragment.error = "Add Mentor type"
                addMentorBinding.mentorTypeInputInAddMentorFragment.requestFocus()
                return@setOnClickListener
            }
            val userName = emailOfMentor.substring(0,emailOfMentor.indexOf("@"))
            val mentor = Mentor(nameOfMentor,phoneNumberOfMentor,emailOfMentor,mentorTypeSelected,passwordOfMentor,userName)
            val addMentorSuccess = context?.let { it1 -> AddMentorAccess(it1).addMentor(mentor) }

            addMentorSuccess!!.observe(viewLifecycleOwner){success->
                if(success){
                    findNavController().navigate(R.id.adminDashboardFragment)
                    Toast.makeText(context,"adding success of $nameOfMentor",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
                }
            }

        }

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