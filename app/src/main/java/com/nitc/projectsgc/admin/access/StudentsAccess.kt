package com.nitc.projectsgc.admin.access

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.Student

class StudentsAccess(var context: Context) {


    fun getStudent(rollNo: String):LiveData<Student>{
        var studentLive = MutableLiveData<Student>(null)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("students")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var student = snapshot.child(rollNo).getValue(Student::class.java)
                studentLive.postValue(student)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return studentLive
    }
    fun getStudents(): LiveData<ArrayList<Student>> {
        var studentLive = MutableLiveData<ArrayList<Student>>(null)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("students")
        reference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var studentList = arrayListOf<Student>()
                for(student in snapshot.children){
                    var thisStudent = student.getValue(Student::class.java)
                    if (thisStudent != null) {
                        studentList.add(thisStudent)
                    }
                }
                studentLive.postValue(studentList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()

            }

        })
        return studentLive
    }

    fun deleteStudent(rollNo: String,email:String):LiveData<Boolean>{

        var deleteLive = MutableLiveData<Boolean>(false)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("students")
        Log.d("child",reference.child(rollNo).toString())
        reference.child(rollNo).removeValue().addOnSuccessListener {
            deleteLive.postValue(true)
        }
            .addOnFailureListener {error->
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
                deleteLive.postValue(false)
            }

        return deleteLive
    }
    fun getAppointments(rollNo:String):LiveData<ArrayList<Appointment>>{
        var appointmentsLive = MutableLiveData<ArrayList<Appointment>>(null)
        var appointments = arrayListOf<Appointment>()
        var database = FirebaseDatabase.getInstance()
        var reference = database.reference.child("students")
        reference.child("appointments").addListenerForSingleValueEvent(object:ValueEventListener{
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