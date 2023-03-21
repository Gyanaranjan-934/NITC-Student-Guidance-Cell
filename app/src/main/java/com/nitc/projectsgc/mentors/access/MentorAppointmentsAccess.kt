package com.nitc.projectsgc.mentors.access

import android.content.Context
import android.util.Log
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
    var sharedViewModel: SharedViewModel
) {


    fun getAppointments(today:String):LiveData<ArrayList<Appointment>>{
        var appointmentLive = MutableLiveData<ArrayList<Appointment>>(null)
        var database = FirebaseDatabase.getInstance()
        var refString = "types/${sharedViewModel.currentMentor.type}/${sharedViewModel.currentUserID}/appointments/22-03-2023"
        Log.d("refString",refString)
        var reference = database.reference.child(refString)
        reference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var appointments = arrayListOf<Appointment>()
                for(timeSlot in snapshot.children){
                    Log.d("refString",timeSlot.key.toString())
                    appointments.add(timeSlot.getValue(Appointment::class.java)!!)
                }
                appointmentLive.postValue(appointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return appointmentLive
    }


    fun cancelAppointment(appointment: Appointment):LiveData<Boolean>{
        var cancelLive = MutableLiveData<Boolean>(false)
        var database = FirebaseDatabase.getInstance()
        var refString = "types/${sharedViewModel.currentMentor.type}/${sharedViewModel.currentUserID}/appointments/${appointment.date}/${appointment.timeSlot}"
        Log.d("refString",refString)
        var reference = database.reference.child(refString)
        reference.setValue(appointment).addOnCompleteListener {cancelTask->
            if(cancelTask.isSuccessful){
                cancelLive.postValue(true)
            }
        }
        return cancelLive
    }


    fun getStudentRecord(studentID:String):LiveData<ArrayList<Appointment>>{
        var appointmentsLive = MutableLiveData<ArrayList<Appointment>>(null)
        var appointments = arrayListOf<Appointment>()
        var database = FirebaseDatabase.getInstance()
        var refString = "students/${studentID}/appointments"
        var mentorReference = database.reference.child(refString)
        mentorReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    var appointment = ds.getValue(Appointment::class.java)
                    if (appointment != null) {
                        if(appointment.mentorID != sharedViewModel.currentUserID && appointment.completed == true){
                            appointments.add(appointment)
                        }
                    }
                }
                appointmentsLive.postValue(appointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return appointmentsLive
    }


}