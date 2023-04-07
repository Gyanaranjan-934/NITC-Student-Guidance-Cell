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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MentorAppointmentsAccess(
    var context: Context,
    var sharedViewModel: SharedViewModel
) {


    suspend fun getAppointments(today:String):ArrayList<Appointment>?{
        return suspendCoroutine { continuation ->
            var appointmentLive = MutableLiveData<ArrayList<Appointment>>(null)
            var database = FirebaseDatabase.getInstance()
            var refString =
                "types/${sharedViewModel.currentMentor.type}/${sharedViewModel.currentUserID}/appointments/$today"
            Log.d("refString", refString)
            var reference = database.reference.child(refString)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var appointments = arrayListOf<Appointment>()
                    for (timeSlot in snapshot.children) {
                        Log.d("refString", timeSlot.key.toString())
                        appointments.add(timeSlot.getValue(Appointment::class.java)!!)
                    }
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    Log.d("appointmentsSize",appointments.size.toString())
                    val sortedAppointments =
                        appointments.sortedBy { LocalDate.parse(it.date, formatter) }
                            .toCollection(ArrayList<Appointment>())
                    Log.d("appointmentsSize",appointments.size.toString())
                    continuation.resume(sortedAppointments)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                    continuation.resume(null)
                }

            })
        }
    }


    fun cancelAppointment(appointment: Appointment):LiveData<Boolean>{
        var cancelLive = MutableLiveData<Boolean>(false)
        var database = FirebaseDatabase.getInstance()
        var refString = "types/${sharedViewModel.currentMentor.type}/${sharedViewModel.currentUserID}/appointments/${appointment.date}/${appointment.timeSlot}"
        Log.d("refString",refString)
        var reference = database.reference.child(refString)
        reference.setValue(appointment).addOnCompleteListener {cancelTask->
            if(cancelTask.isSuccessful){
                var studentRefString = "students/${appointment.studentID}/appointments/${appointment.id}"
                reference.child(studentRefString).setValue(appointment).addOnCompleteListener {studentTask->
                    if(studentTask.isSuccessful) cancelLive.postValue(true)
                    else Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
                }
            } else Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
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
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                val sortedAppointments = appointments.sortedBy { LocalDate.parse(it.date, formatter) }.toCollection(ArrayList<Appointment>())
                appointmentsLive.postValue(sortedAppointments)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return appointmentsLive
    }


    fun giveRemarks(appointment: Appointment):LiveData<Boolean>{
        var remarksLive = MutableLiveData<Boolean>(false)
        var database = FirebaseDatabase.getInstance()
        var refString = "students/${appointment.studentID}/appointments"
        var studentReference = database.reference.child(refString)
        studentReference.child(appointment.id.toString()).setValue(appointment).addOnCompleteListener {task->
            if(task.isSuccessful){
                var mentorRefString = "types/${sharedViewModel.currentMentor.type}/${sharedViewModel.currentUserID}/appointments/${appointment.date}/${appointment.timeSlot}"
                var mentorReference = database.reference.child(mentorRefString)
                mentorReference.setValue(appointment).addOnCompleteListener { mentorTask->
                    if(mentorTask.isSuccessful){
                        Toast.makeText(context,"Submitted",Toast.LENGTH_SHORT).show()
                        remarksLive.postValue(true)
                    }else{
                        Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
            }
        }
        return remarksLive

    }


}