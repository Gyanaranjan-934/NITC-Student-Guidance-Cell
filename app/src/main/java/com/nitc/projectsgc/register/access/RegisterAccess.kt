package com.nitc.projectsgc.register.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.Student

class RegisterAccess(
    var context: Context
) {
    fun register(
        student:Student
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
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(context, "Please verify your email", Toast.LENGTH_SHORT).show()
                                registerSuccess.postValue(true)
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                            }
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