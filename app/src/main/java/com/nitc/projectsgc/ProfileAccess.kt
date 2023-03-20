package com.nitc.projectsgc

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileAccess(var context: Context,var sharedViewModel: SharedViewModel,var parentFragment: Fragment) {

    suspend fun getProfile():Boolean{


        return suspendCoroutine { continuation ->

            var profileLive = MutableLiveData<Boolean>(false)
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            var reference: DatabaseReference = database.reference
            var auth = FirebaseAuth.getInstance()
            var sharedPreferences =
                parentFragment.activity?.getSharedPreferences("sgcLogin", Context.MODE_PRIVATE)
            if (sharedPreferences != null) {
                Log.d("sharedP", "not null")
                var loggedIn = sharedPreferences.getBoolean("loggedIn", false)
                if (loggedIn) {
                    Log.d("sharedP", "logged in")
                    var userType = sharedPreferences.getString("userType", null)
                    if (userType != null) {
                        Log.d("sharedP", "user type not null")
                        var email = sharedPreferences.getString("email", null)
                        var password = sharedPreferences.getString("password", null)
                        var username = sharedPreferences.getString("username", null)
                        if (email != null && password != null) {
                            Log.d("sharedP", "email and password not null")
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { authTask ->
                                    if (authTask.isSuccessful) {
                                        Log.d("sharedP", "auth successful")
                                        when (userType) {
                                            "Student" -> {
                                                if (username != null) {
                                                    Log.d("sharedP", "username not null")
                                                    reference.child("students").child(username)
                                                        .addValueEventListener(object :
                                                            ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                Log.d(
                                                                    "sharedP",
                                                                    "current student got"
                                                                )
                                                                sharedViewModel.userType = userType
                                                                sharedViewModel.currentStudent =
                                                                    snapshot.getValue(Student::class.java)!!
                                                                profileLive.postValue(true)
                                                                Log.d("sharedP", "return statement")
//                                                                callback(true)
                                                                continuation.resume(true)
                                                                return
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Error : $error",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                                profileLive.postValue(false)
//                                                                callback(false)
                                                                continuation.resume(false)
                                                                return
                                                            }

                                                        })
                                                }
                                            }
                                            "Mentor" -> {
                                                if (username != null) {
                                                    var mentorType = sharedPreferences.getString(
                                                        "mentorType",
                                                        null
                                                    )
                                                    if (mentorType != null) {
                                                        reference.child("types/$mentorType")
                                                            .child(username)
                                                            .addValueEventListener(object :
                                                                ValueEventListener {
                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    sharedViewModel.userType =
                                                                        userType
                                                                    sharedViewModel.currentMentor =
                                                                        snapshot.getValue(Mentor::class.java)!!
                                                                    profileLive.postValue(true)
//                                                                    callback(true)
                                                                    continuation.resume(true)
                                                                    return
                                                                }

                                                                override fun onCancelled(error: DatabaseError) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Error : $error",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    profileLive.postValue(false)
//                                                                    callback(false)
                                                                    continuation.resume(false)
                                                                    return
                                                                }

                                                            })
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                    }
                }else{
                    continuation.resume(false)
                }
            }else{
                continuation.resume(false)
            }
        }
    }

}