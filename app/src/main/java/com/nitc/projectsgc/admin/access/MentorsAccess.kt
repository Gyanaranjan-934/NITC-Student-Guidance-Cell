package com.nitc.projectsgc.admin.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Mentor

class MentorsAccess(var context: Context) {
    fun getMentors():LiveData<ArrayList<Mentor>>{
        var mentorLive = MutableLiveData<ArrayList<Mentor>>(null)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("types")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var typeList = arrayListOf<String>()
                var mentorList = arrayListOf<Mentor>()
                for (typeOfMentor in snapshot.children){
                    typeList.add(typeOfMentor.key.toString())
                }
                for (typeOfMentor in typeList){
                    for (mentor in snapshot.child(typeOfMentor).children){
                        Log.d("mentorCheck",mentor.toString())
                        var thisMentor = mentor.getValue(Mentor::class.java)
                        if (thisMentor != null) {
                            mentorList.add(thisMentor)
                        }
                    }
                }
                mentorLive.postValue(mentorList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return mentorLive
    }

    fun getMentor(mentorType:String,mentorID:String):LiveData<Mentor>{
        var mentorLive = MutableLiveData<Mentor>(null)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("types/${mentorType}/${mentorID}")

        reference.addValueEventListener(object:ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {

                mentorLive.postValue(snapshot.getValue(Mentor::class.java))

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_SHORT).show()
            }

        })
        return mentorLive
    }
    fun getMentorNames(mentorType:String): MutableLiveData<ArrayList<Mentor>?> {
        var mentorNamesLive = MutableLiveData<ArrayList<Mentor>?>()
        var mentors = arrayListOf<Mentor>()
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("types")

        reference.addValueEventListener(object:ValueEventListener{


            override fun onDataChange(snapshot: DataSnapshot) {

                var mentorsSnapshot = snapshot.child(mentorType).children

                for(mentor in mentorsSnapshot){
                    mentors.add(mentor.getValue(Mentor::class.java)!!)
                }
                mentorNamesLive.value = mentors

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_SHORT).show()
                mentorNamesLive.value = null
            }

        })
        return mentorNamesLive
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
    fun deleteMentor(userName: String,mentorType:String):LiveData<Boolean>{

        var deleteLive = MutableLiveData<Boolean>(false)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("types")
        Log.d("child",reference.child(userName).toString())
        reference.child("$mentorType/$userName").removeValue().addOnSuccessListener {
            deleteLive.postValue(true)
        }
            .addOnFailureListener {error->
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
                deleteLive.postValue(false)
            }

        return deleteLive
    }
}