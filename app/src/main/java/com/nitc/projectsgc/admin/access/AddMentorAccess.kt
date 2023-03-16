package com.nitc.projectsgc.admin.access

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.Mentors

class AddMentorAccess(
    var context : Context
) {
    fun addMentor(
        mentors: Mentors
    ): MutableLiveData<Boolean> {
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("mentors").child(mentors.type)
        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        val addMentorSuccess = MutableLiveData<Boolean>()
        reference.child(mentors.userName).setValue(mentors).addOnCompleteListener{task ->
            if (task.isSuccessful){
                Log.d("accessAddMentor","here in addMentor access")
                auth.createUserWithEmailAndPassword(
                    mentors.emailId,
                    mentors.password
                ).addOnCompleteListener{authTask->
                    if(authTask.isSuccessful){
                        addMentorSuccess.postValue(true)
                    }else{
                        reference.child(mentors.userName).removeValue()
                        addMentorSuccess.postValue(false)
                    }
                }
            }
            else{
                addMentorSuccess.postValue(false)
                Log.d("accessAddMentor","not success")
            }

        }
        return addMentorSuccess
    }
}