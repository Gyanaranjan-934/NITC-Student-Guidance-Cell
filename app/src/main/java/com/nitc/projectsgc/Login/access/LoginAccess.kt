package com.nitc.projectsgc.Login.access

import android.content.Context
import android.provider.ContactsContract.Profile
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.ProfileAccess
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginAccess(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
) {


    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference : DatabaseReference = database.reference
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(
        email:String,
        password:String,
        userType:String,
        username:String,
        mentorType:String
    ): LiveData<Boolean> {
        var loginLive = MutableLiveData<Boolean>()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){ task->
                if(task.isSuccessful){
                    var verification = auth.currentUser?.isEmailVerified
                    if(verification==true){
                        loginLive.postValue(true)
                        var sharedPreferences = parentFragment.activity?.getSharedPreferences("sgcLogin",Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        if (editor != null) {
                            editor.putBoolean("loggedIn",true)
                            editor.putString("password",password)
                            editor.putString("userType",userType)
                            editor.putString("mentorType",mentorType)
                            editor.putString("email",email)
                            editor.putString("username",username)
                            editor.apply()
                            sharedViewModel.currentUserID = username
                            sharedViewModel.userType = userType
                            when(userType){
                                "Student"->{
                                    reference.child("students/$username").addListenerForSingleValueEvent(object:ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            sharedViewModel.currentStudent = snapshot.getValue(Student::class.java)!!
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(context,"Some error : $error",Toast.LENGTH_LONG).show()
                                        }

                                    })
                                }
                                "Mentor"->{
                                    reference.child("types/$mentorType/$username").addListenerForSingleValueEvent(object:ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            sharedViewModel.currentMentor = snapshot.getValue(Mentor::class.java)!!

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(context,"Some error : $error",Toast.LENGTH_LONG).show()
                                        }

                                    })
                                }
                            }
                        }
                    }else{
                        Toast.makeText(context, "Please verify your email", Toast.LENGTH_SHORT).show()
                    }


                }else{
                    loginLive.postValue(false)
                }
            }
        return loginLive

    }

    fun logout():Boolean{
//        auth.signOut()
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
            logoutLive = true
            sharedViewModel.userType = "NA"
            sharedViewModel.currentUserID = "NA"
        }
        Log.d("logout",logoutLive.toString())
        return logoutLive
    }

}