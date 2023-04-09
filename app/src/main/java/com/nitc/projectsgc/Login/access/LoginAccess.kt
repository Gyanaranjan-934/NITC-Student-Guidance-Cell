package com.nitc.projectsgc.Login.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.*
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LoginAccess(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
) {



    suspend fun login(
        email:String,
        password:String,
        userType:String,
        username:String,
        mentorType:String
    ): Boolean {
        return suspendCoroutine { continuation ->
        var database: FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference: DatabaseReference = database.reference
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                var verification = auth.currentUser?.isEmailVerified
                if (userType == "Mentor") verification = true
                if (verification == true) {
                    var sharedPreferences = parentFragment.activity?.getSharedPreferences(
                        "sgcLogin",
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences?.edit()
                    sharedViewModel.currentUserID = username
                    sharedViewModel.userType = userType
                    when (userType) {
                        "Student" -> {
                            reference.child("students")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.hasChild(username)) {
                                            sharedViewModel.currentStudent =
                                                snapshot.child(username)
                                                    .getValue(Student::class.java)!!
                                            var saved = saveUsername(
                                                password, userType, mentorType, email, username
                                            )
                                            continuation.resume(saved)
                                        } else {
                                            auth.currentUser!!.delete()
                                                .addOnCompleteListener { deleteTask ->
                                                    if (deleteTask.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Your account has been removed by admin",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        if (editor != null) {
                                                            editor.remove("loggedIn")
                                                            editor.remove("password")
                                                            editor.remove("userType")
                                                            editor.remove("mentorType")
                                                            editor.remove("email")
                                                            editor.remove("username")
                                                            editor.apply()
                                                        }
                                                        continuation.resume(false)
                                                    } else {
                                                        continuation.resume(false)
                                                    }
                                                }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            context,
                                            "Some error : $error",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        continuation.resume(false)
                                    }

                                })
                        }
                        "Mentor" -> {
                            reference.child("types")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.hasChild(mentorType)) {
                                            var mentorPath = "$mentorType/$username"
                                            if (snapshot.hasChild(mentorPath)) {
                                                sharedViewModel.currentMentor =
                                                    snapshot.child(mentorPath)
                                                        .getValue(Mentor::class.java)!!
                                                var saved = saveUsername(
                                                    password, userType, mentorType, email, username
                                                )
                                                continuation.resume(saved)
                                            } else {
                                                auth.currentUser!!.delete()
                                                    .addOnCompleteListener { deleteTask ->
                                                        if (deleteTask.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Your account has been removed by admin",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            if (editor != null) {
                                                                editor.remove("loggedIn")
                                                                editor.remove("password")
                                                                editor.remove("userType")
                                                                editor.remove("mentorType")
                                                                editor.remove("email")
                                                                editor.remove("username")
                                                                editor.apply()
                                                            }
                                                            continuation.resume(false)
                                                        } else {
                                                            continuation.resume(false)
                                                        }
                                                    }
                                                continuation.resume(false)
                                            }
                                        } else {
                                            auth.currentUser!!.delete()
                                                .addOnCompleteListener { deleteTask ->
                                                    if (deleteTask.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Your account has been removed by admin",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        if (editor != null) {
                                                            editor.remove("loggedIn")
                                                            editor.remove("password")
                                                            editor.remove("userType")
                                                            editor.remove("mentorType")
                                                            editor.remove("email")
                                                            editor.remove("username")
                                                            editor.apply()
                                                        }
                                                        continuation.resume(false)
                                                    } else {
                                                        Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
                                                        continuation.resume(false)
                                                    }
                                                }
                                            continuation.resume(false)
                                        }

                                    }


                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            context,
                                            "Some error : $error",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        continuation.resume(false)
                                    }

                                })
                        }
                    }
                    if (editor != null) {
                        editor.putBoolean("loggedIn", true)
                        editor.putString("password", password)
                        editor.putString("userType", userType)
                        editor.putString("mentorType", mentorType)
                        editor.putString("email", email)
                        editor.putString("username", username)
                        editor.apply()
                    }
                } else {
                    Toast.makeText(context, "Please verify your email", Toast.LENGTH_SHORT).show()
                }


            } else {
                val errorCode = (task.exception as FirebaseAuthException).errorCode
                if (errorCode == "ERROR_USER_NOT_FOUND") {
                    // Show error message to the user
                    Toast.makeText(context, "Email address not found", Toast.LENGTH_SHORT).show()
                    continuation.resume(false)
                } else {
                    Toast.makeText(context,"Wrong credentials entered. Try again",Toast.LENGTH_SHORT).show()
                    continuation.resume(false)
                }

            }
        }
    }
    }

    fun saveUsername(
        password: String,
        userType: String,
        mentorType: String,
        email:String,
        username: String
    ):Boolean{
        var saved = false
        var sharedPreferences = parentFragment.activity?.getSharedPreferences(
        "sgcLogin",
        Context.MODE_PRIVATE
    )
        val editor = sharedPreferences?.edit()
        if (editor != null) {
            editor.putBoolean("loggedIn", true)
            editor.putString("password", password)
            editor.putString("userType", userType)
            editor.putString("mentorType", mentorType)
            editor.putString("email", email)
            editor.putString("username", username)
            editor.apply()
            saved = true
        }
        return saved
    }

    fun logout():Boolean{
//        auth.signOut()

        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        var logoutLive = false
        var sharedPreferences = parentFragment.activity?.getSharedPreferences("sgcLogin",Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        if (editor != null) {
            editor.remove("password")
            editor.remove("mentorType")
            editor.remove("userType")
            editor.remove("username")
            editor.remove("email")
            editor.putBoolean("loggedIn",false)
            editor.apply()
            sharedViewModel.rescheduling = false
            sharedViewModel.mentorTypeForProfile = "NA"
            sharedViewModel.viewAppointmentStudentID = "NA"
            sharedViewModel.pastRecordStudentID = "NA"
            logoutLive = true
            sharedViewModel.userType = "NA"
            sharedViewModel.currentUserID = "NA"
        }
        Log.d("logout",logoutLive.toString())
        return logoutLive
    }

}