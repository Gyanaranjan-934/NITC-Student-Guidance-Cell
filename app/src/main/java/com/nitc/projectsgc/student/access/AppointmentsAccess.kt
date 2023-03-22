package com.nitc.projectsgc.student.access

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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppointmentsAccess(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
) {

    fun getBookedAppointments():LiveData<ArrayList<Appointment>>{
        Log.d("appointment","class called")
        var bookedLive = MutableLiveData<ArrayList<Appointment>>(null)
        var appointments = arrayListOf<Appointment>()
        var database = FirebaseDatabase.getInstance()
        var studentReference = database.reference.child("students")
        Log.d("appointment",sharedViewModel.currentUserID.toString())
        studentReference.child(sharedViewModel.currentUserID).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.child("appointments").children){
                    Log.d("appointment",ds.toString())
                    var appointment = ds.getValue(Appointment::class.java)
                    if (appointment != null && appointment.completed == false) {
                        appointments.add(appointment)
                    }
                }
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val sortedAppointments = appointments.sortedBy { LocalDate.parse(it.date, formatter) }.toCollection(ArrayList<Appointment>())
                bookedLive.postValue(sortedAppointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })

        return bookedLive
    }

    fun getCompletedAppointments():LiveData<ArrayList<Appointment>>{
        var completedLive = MutableLiveData<ArrayList<Appointment>>(null)
        var appointments = arrayListOf<Appointment>()
        var database = FirebaseDatabase.getInstance()
        var studentReference = database.reference.child("students")
        studentReference.child(sharedViewModel.currentUserID).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.child("appointments").children){
                    var appointment = ds.getValue(Appointment::class.java)
                    if(appointment != null){
                        if(appointment.completed) appointments.add(appointment)
                    }
                }
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val sortedAppointments = appointments.sortedBy { LocalDate.parse(it.date, formatter) }.toCollection(ArrayList<Appointment>())
                completedLive.postValue(sortedAppointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return completedLive
    }


    fun cancelAppointment(appointment: Appointment):LiveData<Boolean>{
        var deletedLive = MutableLiveData<Boolean>()
        var database = FirebaseDatabase.getInstance()
        var studentReference = database.reference.child("students")
        var typeReference = database.reference.child("types")
        studentReference.child(sharedViewModel.currentUserID+"/appointments").child(appointment.id.toString()).setValue(appointment).addOnCompleteListener { studentTask->
            if(studentTask.isSuccessful){
                typeReference.child(appointment.mentorType.toString()+"/${appointment.mentorID}/appointments/${appointment.date}/${appointment.timeSlot}").setValue(appointment).addOnCompleteListener {mentorTask->
                    if(mentorTask.isSuccessful) deletedLive.postValue(true)
                    else deletedLive.postValue(false)
                }
            }else{
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
                deletedLive.postValue(false)
            }
        }
        return deletedLive
    }


}