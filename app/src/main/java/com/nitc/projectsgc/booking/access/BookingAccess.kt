package com.nitc.projectsgc.booking.access

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.SharedViewModel

class BookingAccess(var context: Context,var sharedViewModel: SharedViewModel) {

    fun bookAppointment(appointment: Appointment):LiveData<Boolean>{
        var bookingLive = MutableLiveData<Boolean>(false)
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference
        var mentorReference = reference.child("types/${appointment.mentorType}/${appointment.mentorID}/appointments/${appointment.date}/${appointment.timeSlot}")
        var appointmentID = mentorReference.push().key.toString()
        appointment.id = appointmentID
        mentorReference.setValue(appointment).addOnCompleteListener { mentorTask->
            if(mentorTask.isSuccessful){
                reference.child("students/${appointment.studentID}/appointments/$appointmentID").setValue(appointment).addOnCompleteListener { studentTask->
                    if(studentTask.isSuccessful){
                        bookingLive.postValue(true)
                    }else{
                        mentorReference.child(appointment.timeSlot.toString()).removeValue()
                    }
                }
            }
        }
        return bookingLive
    }
    fun rescheduleAppointment(appointment: Appointment):LiveData<Boolean>{
        var oldAppointment = sharedViewModel.reschedulingAppointment
        oldAppointment.status = "Rescheduled"
        var bookingLive = MutableLiveData<Boolean>(false)
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference
        var mentorNewReference = reference.child("types/${appointment.mentorType}/${appointment.mentorID}/appointments/${appointment.date}/${appointment.timeSlot}")
        var appointmentID = mentorNewReference.push().key.toString()
        appointment.id = appointmentID
        var mentorOldReference = reference.child("types/${oldAppointment.mentorType}/${oldAppointment.mentorID}/appointments/${oldAppointment.date}/${oldAppointment.timeSlot}")
        mentorOldReference.setValue(oldAppointment).addOnCompleteListener { oldMentorTask ->
            if (oldMentorTask.isSuccessful) {
                reference.child("students/${oldAppointment.studentID}/appointments/${oldAppointment.id}").removeValue().addOnCompleteListener { oldStudentTask ->
                    if(oldStudentTask.isSuccessful) {
                        mentorNewReference.setValue(appointment)
                            .addOnCompleteListener { mentorTask ->
                                if (mentorTask.isSuccessful) {
                                    reference.child("students/${appointment.studentID}/appointments/$appointmentID")
                                        .setValue(appointment)
                                        .addOnCompleteListener { studentTask ->
                                            if (studentTask.isSuccessful) {
                                                bookingLive.postValue(true)
                                            } else {
                                                mentorNewReference.child(appointment.timeSlot.toString())
                                                    .removeValue()
                                            }
                                        }
                                }
                            }
                    }
                }
            }
        }
        return bookingLive
    }

}