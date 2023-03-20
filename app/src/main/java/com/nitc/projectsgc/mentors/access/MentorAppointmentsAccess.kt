package com.nitc.projectsgc.mentors.access

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.SharedViewModel

class MentorAppointmentsAccess(
    var context: Context,
    var sharedViewModel: SharedViewModel,
    var parentFragment: Fragment
) {


    fun getAppointments(today:String):LiveData<ArrayList<Appointment>>{
        var appointmentLive = MutableLiveData<ArrayList<Appointment>>(null)
        var database = FirebaseDatabase.getInstance()
        var reference = database.reference.child("types/${sharedViewModel.currentMentor.type}/${sharedViewModel.currentUserID}/appointments/$today")
        reference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var appointments = arrayListOf<Appointment>()
                for(timeSlot in snapshot.children){
                    for(thisAppointment in snapshot.child(timeSlot.key.toString()).children){
                        appointments.add(thisAppointment.getValue(Appointment::class.java)!!)
                    }
                }
                appointmentLive.postValue(appointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return appointmentLive
    }

}