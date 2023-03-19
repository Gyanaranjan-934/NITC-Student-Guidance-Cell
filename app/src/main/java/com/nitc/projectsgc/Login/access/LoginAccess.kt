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
    ): LiveData<String> {
        var loginLive = MutableLiveData<String>("NA")
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){ task->
                if(task.isSuccessful){
                    var userID = auth.currentUser!!.uid.toString()
                    loginLive.postValue(userID)
                }else{
                    loginLive.postValue("NA")
                }
            }
        return loginLive

    }

}