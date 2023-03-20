package com.nitc.projectsgc.Login.access

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.SharedViewModel

class LoginAccess(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
) {


    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference : DatabaseReference = database.reference.child("students")
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