package com.nitc.projectsgc.admin.access

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Mentors
import com.nitc.projectsgc.databinding.FragmentMentorUpdateBinding

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
                    typeList.add(typeOfMentor.key.toString())
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
    fun getMentorTypes():LiveData<ArrayList<String>>{
        var mentorTypeLive = MutableLiveData<ArrayList<String>>(null)
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("types")
        var mentorTypes = arrayListOf<String>()
        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(typeChild in snapshot.children){
                    mentorTypes.add(typeChild.key.toString())
                }
                mentorTypeLive.postValue(mentorTypes)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return mentorTypeLive
    }
    fun deletementor(userName: String,email:String):LiveData<Boolean>{

        var deleteLive = MutableLiveData<Boolean>(false)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("types")
        Log.d("child",reference.child(userName).toString())
        reference.child(userName).removeValue().addOnSuccessListener {
            deleteLive.postValue(true)
        }
            .addOnFailureListener {error->
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
                deleteLive.postValue(false)
            }

        return deleteLive
    }
}