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
import kotlin.math.log

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
                    if(userType=="Mentor") verification=true
                    if(verification==true){
                        var sharedPreferences = parentFragment.activity?.getSharedPreferences("sgcLogin",Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()

                            sharedViewModel.currentUserID = username
                            sharedViewModel.userType = userType
                            when(userType){
                                "Student"->{
                                    reference.child("students").addListenerForSingleValueEvent(object:ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.hasChild(username)) {
                                                sharedViewModel.currentStudent =
                                                    snapshot.child(username)
                                                        .getValue(Student::class.java)!!
                                                loginLive.postValue(true)
                                            }else{
                                                auth.currentUser!!.delete().addOnCompleteListener {deleteTask->
                                                    if(deleteTask.isSuccessful){
                                                        Toast.makeText(context,"Your account has been removed by admin",Toast.LENGTH_SHORT).show()
                                                        loginLive.postValue(false)
                                                    }else{
                                                        loginLive.postValue(false)
                                                    }
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(context,"Some error : $error",Toast.LENGTH_LONG).show()
                                            loginLive.postValue(false)
                                        }

                                    })
                                }
                                "Mentor"->{
                                    reference.child("types").addListenerForSingleValueEvent(object:ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if(snapshot.hasChild(mentorType)) {
                                                var mentorPath = "$mentorType/$username"
                                                if(snapshot.hasChild(mentorPath)) {
                                                sharedViewModel.currentMentor = snapshot.child(mentorPath).getValue(Mentor::class.java)!!
                                                loginLive.postValue(true)
                                            }else {
                                                    auth.currentUser!!.delete().addOnCompleteListener {deleteTask->
                                                        if(deleteTask.isSuccessful){
                                                            Toast.makeText(context,"Your account has been removed by admin",Toast.LENGTH_SHORT).show()
                                                            loginLive.postValue(false)
                                                        }else{
                                                            loginLive.postValue(false)
                                                        }
                                                    }
                                                    loginLive.postValue(false)
                                                }
                                                }
                                            else {
                                                auth.currentUser!!.delete().addOnCompleteListener {deleteTask->
                                                    if(deleteTask.isSuccessful){
                                                        Toast.makeText(context,"Your account has been removed by admin",Toast.LENGTH_SHORT).show()
                                                        loginLive.postValue(false)
                                                    }else{
                                                        loginLive.postValue(false)
                                                    }
                                                }
                                                loginLive.postValue(false)
                                            }

                                        }



                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(context,"Some error : $error",Toast.LENGTH_LONG).show()
                                            loginLive.postValue(false)
                                        }

                                    })
                                }
                            }
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
            sharedViewModel.rescheduling = false
            sharedViewModel.profileForMentorType = "NA"
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