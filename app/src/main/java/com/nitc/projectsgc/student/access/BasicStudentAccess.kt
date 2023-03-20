package com.nitc.projectsgc.student.access

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student

class BasicStudentAccess(
    var context: Context,
    var sharedViewModel: SharedViewModel
) {

    fun getStudent(studentID:String):LiveData<Student>{
        var studentsLive = MutableLiveData<Student>(null)
        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("students")
        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var student = snapshot.child(studentID).getValue(Student::class.java)!!
                studentsLive.postValue(student)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }

        })
        return studentsLive
    }

}