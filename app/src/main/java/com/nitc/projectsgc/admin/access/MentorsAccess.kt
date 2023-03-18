package com.nitc.projectsgc.admin.access

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Mentors

class MentorsAccess(var context: Context) {
    fun getMentors():LiveData<ArrayList<Mentors>>{
        var mentorsLive = MutableLiveData<ArrayList<Mentors>>(null)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("types")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var typeList = arrayListOf<String>()
                var mentorList = arrayListOf<Mentors>()
                for (typeOfMentor in snapshot.children){
                    typeList.add(typeOfMentor.toString())
                }
                for (typeOfMentor in typeList){
                    for (mentor in snapshot.child(typeOfMentor).children){
                        var thisMentor = mentor.getValue(Mentors::class.java)
                        if (thisMentor != null) {
                            mentorList.add(thisMentor)
                        }
                    }
                }
                mentorsLive.postValue(mentorList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return mentorsLive
    }
}