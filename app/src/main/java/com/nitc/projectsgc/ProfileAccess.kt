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
                                                    reference.child("students")
                                                        .addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                Log.d(
                                                                    "sharedP",
                                                                    "current student got"
                                                                )
                                                                if(snapshot.hasChild(username)) {
                                                                    sharedViewModel.userType = userType
                                                                    sharedViewModel.currentStudent =
                                                                        snapshot.child(username).getValue(Student::class.java)!!
                                                                    sharedViewModel.currentUserID = username
                                                                    profileLive.postValue(true)
                                                                    Log.d("sharedP", "return statement")
//                                                                callback(true)
                                                                    continuation.resume(true)
                                                                    return
                                                                }else{
                                                                    auth.currentUser!!.delete().addOnCompleteListener {deleteTask->
                                                                        if(deleteTask.isSuccessful){
                                                                            Toast.makeText(context,"Your account has been removed by admin",Toast.LENGTH_SHORT).show()
                                                                            continuation.resume(false)
                                                                        }else{
                                                                            continuation.resume(false)
                                                                        }
                                                                    }
                                                                }
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
                                                    Log.d("username","mentor username = ${username.toString()}")
                                                    if (mentorType != null) {
                                                        reference.child("types")
                                                            .addListenerForSingleValueEvent(object :
                                                                ValueEventListener {
                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    if(snapshot.hasChild(mentorType)) {
                                                                        var mentorPath = "$mentorType/$username"
                                                                        if(snapshot.hasChild(mentorPath)) {
                                                                            sharedViewModel.currentMentor = snapshot.child(mentorPath).getValue(Mentor::class.java)!!
                                                                            sharedViewModel.userType =
                                                                                userType
                                                                            profileLive.postValue(true)
                                                                            sharedViewModel.currentUserID = username
                                                                            continuation.resume(true)
                                                                            return
                                                                        }else {
                                                                            auth.currentUser!!.delete().addOnCompleteListener {deleteTask->
                                                                                if(deleteTask.isSuccessful){
                                                                                    Toast.makeText(context,"Your account has been removed by admin",Toast.LENGTH_SHORT).show()
                                                                                    continuation.resume(false)
                                                                                }else{
                                                                                    continuation.resume(false)
                                                                                }
                                                                            }
                                                                            continuation.resume(false)
                                                                            return
                                                                        }
                                                                    }
                                                                    else {
                                                                        auth.currentUser!!.delete().addOnCompleteListener {deleteTask->
                                                                            if(deleteTask.isSuccessful){
                                                                                Toast.makeText(context,"Your account has been removed by admin",Toast.LENGTH_SHORT).show()
                                                                                continuation.resume(false)
                                                                            }else{
                                                                                continuation.resume(false)

                                                                            }
                                                                        }
                                                                        continuation.resume(false)
                                                                        return
                                                                    }

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
                                .addOnFailureListener{exc->
                                    Toast.makeText(context,"Exception : $exc",Toast.LENGTH_LONG).show()
                                    profileLive.postValue(false)
                                    continuation.resume(false)
                                }
                        } else continuation.resume(false)
                    }else continuation.resume(false)
                }else{
                    continuation.resume(false)
                }
            }else{
                continuation.resume(false)
            }
        }
    }

}