package com.nitc.projectsgc.admin.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.Mentor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MentorsAccess(var context: Context) {
    suspend fun getMentors():ArrayList<Mentor>?{
        return suspendCoroutine { continuation ->
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            var reference: DatabaseReference = database.reference.child("types")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var typeList = arrayListOf<String>()
                    var mentorList = arrayListOf<Mentor>()
                    for (typeOfMentor in snapshot.children) {
                        typeList.add(typeOfMentor.key.toString())
                    }
                    for (typeOfMentor in typeList) {
                        for (mentor in snapshot.child(typeOfMentor).children) {
                            Log.d("mentorCheck", mentor.toString())
                            var thisMentor = mentor.getValue(Mentor::class.java)
                            if (thisMentor != null) {
                                mentorList.add(thisMentor)
                            }
                        }
                    }
                    continuation.resume(mentorList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                    continuation.resume(null)
                }

            })
        }
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
    suspend fun deleteMentor(userName: String,mentorType:String):Boolean{
        return suspendCoroutine {continuation->
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            var typeReference: DatabaseReference = database.reference.child("types")
            var mentorPath = "$mentorType/$userName"
            Log.d("deleteMentor", mentorPath)
            typeReference.child(mentorPath)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.child("appointments").children) {
                            for (timeSlot in ds.children) {
                                var appointment = timeSlot.getValue(Appointment::class.java)!!
                                var studentReference =
                                    "students/${appointment.studentID}/appointments/${appointment.id}"
                                Log.d("deleteMentor", studentReference)
                                database.reference.child(studentReference).removeValue()
                                    .addOnSuccessListener {

                                    }.addOnFailureListener {
                                    continuation.resume(false)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                        continuation.resume(false)
                    }

                })
            typeReference.child(mentorPath).removeValue().addOnSuccessListener { deletedMentor ->
                continuation.resume(true)
            }
                .addOnFailureListener { error ->
                    Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                    continuation.resume(false)
                }
        }
    }
}