package com.nitc.projectsgc.admin.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nitc.projectsgc.Mentor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddMentorAccess(
    var context : Context
) {
    suspend fun addMentor(
        mentor: Mentor
    ): Boolean {
        return suspendCoroutine { continuation ->

            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference: DatabaseReference = database.reference.child("types").child(mentor.type)
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChild(mentor.userName)) {
                        reference.child(mentor.userName)
                            .setValue(mentor).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("accessAddMentor", "here in addMentor access")

                                } else {
                                    continuation.resume(false)
                                    Log.d("accessAddMentor", "not success")
                                }
                            }
                    }
                    else {
                        Toast.makeText(
                            context,
                            "Another mentor found with same email.",
                            Toast.LENGTH_SHORT
                        ).show()
                        continuation.resume(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(false)
                }
            })
            auth.createUserWithEmailAndPassword(
                mentor.email,
                mentor.password
            ).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    continuation.resume(true)
                } else {
                    reference.child(mentor.userName).removeValue()
                    continuation.resume(false)
                }
            }

        }
    }
    suspend fun updateMentor(
        mentor: Mentor,
        oldPassword:String
    ): Boolean {
        return suspendCoroutine { continuation ->
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val reference: DatabaseReference = database.reference.child("types").child(mentor.type)
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            reference.child(mentor.userName).setValue(mentor).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("accessAddMentor", "here in addMentor access")
                    if(mentor.password != oldPassword) {
//                        auth.signInWithEmailAndPassword(mentor.email, oldPassword)
//                            .addOnCompleteListener { loggedIn ->
//                                if (loggedIn.isSuccessful) {
//                                    auth.currentUser?.delete()?.addOnCompleteListener { deleted ->
//                                        if (!deleted.isSuccessful) continuation.resume(false)
//                                        else {
//                                            auth.createUserWithEmailAndPassword(
//                                                mentor.email,
//                                                mentor.password
//                                            ).addOnCompleteListener { authTask ->
//                                                if (authTask.isSuccessful) {
//                                                    continuation.resume(true)
//                                                } else {
//                                                    continuation.resume(false)
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
                        val currentUser = FirebaseAuth.getInstance().currentUser

// Update the password in the Authentication Database
                        currentUser?.updatePassword(mentor.password)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Show success message to the user
                                Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                continuation.resume(true)
                            } else {
                                // Password update failed, show error message to the user
                                Toast.makeText(context, "Password update failed", Toast.LENGTH_SHORT).show()
                                continuation.resume(false)
                            }
                        }
                    }else{
                        continuation.resume(true)
                    }
                } else {
                    continuation.resume(false)
                    Log.d("accessAddMentor", "not success")
                }

            }
        }
    }
}