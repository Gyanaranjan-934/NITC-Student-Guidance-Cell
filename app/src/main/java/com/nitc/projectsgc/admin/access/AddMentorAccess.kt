package com.nitc.projectsgc.admin.access

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.Mentor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddMentorAccess(
    var context : Context
) {
    fun addMentor(
        mentor: Mentor
    ): MutableLiveData<Boolean> {
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("types").child(mentor.type)
        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        val addMentorSuccess = MutableLiveData<Boolean>()
        reference.child(mentor.userName).setValue(mentor).addOnCompleteListener{ task ->
            if (task.isSuccessful){
                Log.d("accessAddMentor","here in addMentor access")
                auth.createUserWithEmailAndPassword(
                    mentor.email,
                    mentor.password
                ).addOnCompleteListener{authTask->
                    if(authTask.isSuccessful){
                        addMentorSuccess.postValue(true)
                    }else{
                        reference.child(mentor.userName).removeValue()
                        addMentorSuccess.postValue(false)
                    }
                }
            }
            else{
                addMentorSuccess.postValue(false)
                Log.d("accessAddMentor","not success")
            }

        }
        return addMentorSuccess
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
                        auth.signInWithEmailAndPassword(mentor.email, oldPassword)
                            .addOnCompleteListener { loggedIn ->
                                if (loggedIn.isSuccessful) {
                                    auth.currentUser?.delete()?.addOnCompleteListener { deleted ->
                                        if (!deleted.isSuccessful) continuation.resume(false)
                                        else {
                                            auth.createUserWithEmailAndPassword(
                                                mentor.email,
                                                mentor.password
                                            ).addOnCompleteListener { authTask ->
                                                if (authTask.isSuccessful) {
                                                    continuation.resume(true)
                                                } else {
                                                    continuation.resume(false)
                                                }
                                            }
                                        }
                                    }
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