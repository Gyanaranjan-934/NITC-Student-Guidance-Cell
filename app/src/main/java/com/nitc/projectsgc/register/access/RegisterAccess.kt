package com.nitc.projectsgc.register.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.R
import com.nitc.projectsgc.Students

class RegisterAccess(
    var context: Context
) {
    fun register(
        student:Students
    ):LiveData<Boolean>{
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("students")
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val registerSuccess = MutableLiveData<Boolean>()

        reference.child(student.rollNo).setValue(student).addOnCompleteListener{ task ->
            if(task.isSuccessful){

                Log.d("accessRegister","here in register access")
                auth.createUserWithEmailAndPassword(
                    student.emailId,
                    student.password
                ).addOnCompleteListener{authTask->
                    if(authTask.isSuccessful){
                        registerSuccess.postValue(true)
                    }else{
                        reference.child(student.rollNo).removeValue()
                        registerSuccess.postValue(false)
                    }
                }
            }else{
                registerSuccess.postValue(false)
                Log.d("accessRegister","not success")
            }
        }
        return registerSuccess
    }

}