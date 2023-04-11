package com.nitc.projectsgc.admin.access

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.Student
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class StudentsAccess(var context: Context,var parentFragment: Fragment) {


    suspend fun getStudent(rollNo: String): Student? {
        return suspendCoroutine {continuation->
            var studentLive = MutableLiveData<Student?>(null)
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            var reference: DatabaseReference = database.reference.child("students")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var student = snapshot.child(rollNo).getValue(Student::class.java)
                    if (student == null) continuation.resume(null)
                    else continuation.resume(student)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                    continuation.resume(null)
                }

            })
        }
    }
    suspend fun getStudents(): ArrayList<Student>? {
        return suspendCoroutine { continuation ->
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            var reference: DatabaseReference = database.reference.child("students")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var studentList = arrayListOf<Student>()
                    for (student in snapshot.children) {
                        var thisStudent = student.getValue(Student::class.java)
                        if (thisStudent != null) {
                            studentList.add(thisStudent)
                        }
                    }
                    continuation.resume(studentList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                    continuation.resume(null)
                }

            })
        }
    }

    suspend fun deleteStudent(rollNo: String,email:String):Boolean{
        return suspendCoroutine {continuation->
            var deleteLive = MutableLiveData<Boolean>(false)
            var database: FirebaseDatabase = FirebaseDatabase.getInstance()
            var typeReference = database.reference.child("types")
            var studentReference: DatabaseReference = database.reference.child("students")
//        Log.d("child",reference.child(rollNo).toString())

            studentReference.child(rollNo)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(studentSnapshot: DataSnapshot) {
                        if (studentSnapshot.hasChild("appointments")) {
                            for (appointment in studentSnapshot.child("appointments").children) {
                                var studentAppointment =
                                    appointment.getValue(Appointment::class.java)!!
                                var mentorAppointmentPath =
                                    "${studentAppointment.mentorType}/${studentAppointment.mentorID}/${studentAppointment.date}/${studentAppointment.timeSlot}"
                                studentAppointment.status = "Student deleted"
                                studentAppointment.cancelled = true
                                typeReference.child(mentorAppointmentPath).setValue(studentAppointment)
                                    .addOnCompleteListener { mentorDeleted ->
                                        if (mentorDeleted.isSuccessful) {}
                                        else {
                                            continuation.resume(false)
                                        }
                                    }
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                        continuation.resume(false)
                    }

                })


            studentReference.child(rollNo).removeValue().addOnSuccessListener {
                continuation.resume(true)
            }
                .addOnFailureListener { error ->
                    Toast.makeText(context, "Error : $error", Toast.LENGTH_LONG).show()
                    deleteLive.postValue(false)
                }

        }
    }
    fun getAppointments(rollNo:String):LiveData<ArrayList<Appointment>>{
        var appointmentsLive = MutableLiveData<ArrayList<Appointment>>(null)
        var appointments = arrayListOf<Appointment>()
        var database = FirebaseDatabase.getInstance()
        var reference = database.reference.child("students")
        reference.child("$rollNo/appointments").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children){
                    var appointment = ds.getValue(Appointment::class.java)
                    if(appointment != null){
                        appointments.add(appointment)
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