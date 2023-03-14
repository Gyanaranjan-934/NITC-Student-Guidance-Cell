package com.nitc.projectsgc.Login.access

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginAccess(
    var context: Context
) {


    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference : DatabaseReference = database.reference.child("students")
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(
        email:String,
        password:String
    ): LiveData<Boolean> {

        var loginLive = MutableLiveData<Boolean>()
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(){task->
                if(task.isSuccessful){
                    loginLive.postValue(true)
                }else{
                    loginLive.postValue(false)
                }
            }
        return loginLive

    }

}