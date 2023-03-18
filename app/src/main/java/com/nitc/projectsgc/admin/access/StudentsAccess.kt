package com.nitc.projectsgc.admin.access

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Students

class StudentsAccess(var context: Context) {


    fun getStudents(): LiveData<Array<Students>> {
        var studentsLive = MutableLiveData<Array<Students>>(null)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("students")
        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var studentsList = arrayOf<Students>()
                var studentIndex = 0
                for(student in snapshot.children){
                    var thisStudent = student.getValue(Students::class.java)
                    if (thisStudent != null) {
                        studentsList[studentIndex] = thisStudent
                        studentIndex++
                    }
                }
                studentsLive.postValue(studentsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()

            }

        })
        return studentsLive
    }


}