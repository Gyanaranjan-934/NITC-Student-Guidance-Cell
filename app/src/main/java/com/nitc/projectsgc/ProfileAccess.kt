package com.nitc.projectsgc

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileAccess(var context: Context,var sharedViewModel: SharedViewModel,var parentFragment: Fragment) {

    fun getProfile():LiveData<Boolean>{

        var profileLive = MutableLiveData<Boolean>(false)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference
        var auth = FirebaseAuth.getInstance()
        var sharedPreferences = parentFragment.activity?.getSharedPreferences("sgcLogin",Context.MODE_PRIVATE)
        if(sharedPreferences != null) {
            var loggedIn = sharedPreferences.getBoolean("loggedIn", false)
            if (loggedIn) {
                var userType = sharedPreferences.getString("userType", null)
                if (userType != null) {
                    var email = sharedPreferences.getString("email",null)
                    var password = sharedPreferences.getString("password",null)
                    var username = sharedPreferences.getString("username",null)
                    if(email != null && password != null){
                           auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {authTask->
                               if(authTask.isSuccessful){
                                   when(userType){
                                       "Student"->{
                                           if (username != null) {
                                               reference.child("students").child(username).addValueEventListener(object:ValueEventListener{
                                                   override fun onDataChange(snapshot: DataSnapshot) {
                                                       sharedViewModel.userType = userType
                                                        sharedViewModel.currentStudent = snapshot.getValue(Student::class.java)!!
                                                       profileLive.postValue(true)
                                                   }

                                                   override fun onCancelled(error: DatabaseError) {
                                                       Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
                                                   }

                                               })
                                           }
                                       }
                                       "Mentor"->{
                                           if (username != null) {
                                               var mentorType = sharedPreferences.getString("mentorType",null)
                                               if(mentorType != null) {
                                                   reference.child("types/$mentorType")
                                                       .child(username)
                                                       .addValueEventListener(object :
                                                           ValueEventListener {
                                                           override fun onDataChange(snapshot: DataSnapshot) {
                                                               sharedViewModel.userType = userType
                                                               sharedViewModel.currentMentor =
                                                                   snapshot.getValue(Mentor::class.java)!!
                                                               profileLive.postValue(true)
                                                           }

                                                           override fun onCancelled(error: DatabaseError) {
                                                               Toast.makeText(
                                                                   context,
                                                                   "Error : $error",
                                                                   Toast.LENGTH_LONG
                                                               ).show()
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
            }
        }
        return profileLive
    }

}