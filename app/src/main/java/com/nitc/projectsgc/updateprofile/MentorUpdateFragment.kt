package com.nitc.projectsgc.updateprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.Mentors
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.AddMentorAccess
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.databinding.FragmentMentorUpdateBinding

class MentorUpdateFragment : Fragment() {
    lateinit var mentorUpdateBinding: FragmentMentorUpdateBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()
    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference : DatabaseReference = database.reference.child("types")
    var auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mentorUpdateBinding = FragmentMentorUpdateBinding.inflate(inflater,container,false)

        mentorUpdateBinding.nameFieldInUpdateMentorFragment.setText(sharedViewModel.updateMentor.name)
        mentorUpdateBinding.emailFieldInUpdateMentorFragment.setText(sharedViewModel.updateMentor.email)
        mentorUpdateBinding.mentorTypeButtonInUpdateMentorFragment.text = sharedViewModel.updateMentor.type
        mentorUpdateBinding.passwordFieldInUpdateMentorFragment.setText(sharedViewModel.updateMentor.password)
        mentorUpdateBinding.phoneNumberInUpdateMentorFragment.setText(sharedViewModel.updateMentor.phone)


        mentorUpdateBinding.updateButtonInUpdateMentorFragment.setOnClickListener {
            var newEmail = mentorUpdateBinding.emailFieldInUpdateMentorFragment.text.toString()
            var newName = mentorUpdateBinding.nameFieldInUpdateMentorFragment.text.toString()
            var newPhone = mentorUpdateBinding.phoneNumberInUpdateMentorFragment.text.toString()
            var newPassword = mentorUpdateBinding.passwordFieldInUpdateMentorFragment.text.toString()
            var newType = mentorUpdateBinding.mentorTypeButtonInUpdateMentorFragment.text.toString()

            val oldEmail = sharedViewModel.updateMentor.email
            var oldUserName = oldEmail.substring(0,oldEmail.indexOf("@"))
            if(newEmail != sharedViewModel.updateMentor.email){
//              new email verification will be applied
                var newUserName = newEmail.substring(0,newEmail.indexOf("@"))
                reference.child(newType).child(oldUserName).removeValue()
                val mentor = Mentors(newName,newPhone,newEmail,newType,newPassword,newUserName)
                reference.child(newType).child(newUserName).setValue(mentor).addOnCompleteListener{task->
                    auth.createUserWithEmailAndPassword(
                        mentor.email,
                        mentor.password
                    ).addOnCompleteListener{authTask->
                        if(authTask.isSuccessful){
                            Toast.makeText(context,"Update Successful",Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.adminDashboardFragment)
                        }else{
                            Toast.makeText(context,"Update Unsuccessful",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                val mentor = Mentors(newName,newPhone,newEmail,newType,newPassword,oldUserName)
                reference.child(newType).child(oldUserName).setValue(mentor)
                findNavController().navigate(R.id.adminDashboardFragment)
            }
        }

        return mentorUpdateBinding.root
    }

}