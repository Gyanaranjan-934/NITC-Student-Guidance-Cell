package com.nitc.projectsgc.register.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nitc.projectsgc.Student
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RegisterAccess(
    var context: Context
) {
    suspend fun register(
        student:Student
    ):Boolean {
        return suspendCoroutine { continuation ->
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference: DatabaseReference = database.reference.child("students")
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            reference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild(student.rollNo)) {
                        Toast.makeText(context,"Already registered with given mail ID",Toast.LENGTH_SHORT).show()
                        continuation.resume(false)
                    }else{
                        reference.child(student.rollNo).setValue(student).addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                Log.d("accessRegister", "here in register access")
                                auth.createUserWithEmailAndPassword(
                                    student.emailId,
                                    student.password
                                ).addOnCompleteListener { authTask ->
                                    if (authTask.isSuccessful) {
                                        auth.currentUser?.sendEmailVerification()
                                            ?.addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Please verify your email",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                continuation.resume(true)
                                            }
                                            ?.addOnFailureListener {
                                                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT)
                                                    .show()
                                                reference.child(student.rollNo).removeValue()
                                                continuation.resume(false)
                                            }
                                    } else {
                                        reference.child(student.rollNo).removeValue()
                                        continuation.resume(false)
                                    }
                                }
                            } else {
                                continuation.resume(false)
                                Log.d("accessRegister", "not success")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("databaseError","error : $error")
                }
            })

        }
    }
}