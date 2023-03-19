package com.nitc.projectsgc.Login.access

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginAccess(
    var context: Context,
    var parentFragment: Fragment
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

}