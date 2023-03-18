package com.nitc.projectsgc.admin.access

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.nitc.projectsgc.Students

class StudentsAccess(var context: Context) {


    fun getStudents(): LiveData<Array<Students>> {
        var studentsLive = MutableLiveData<Array<Students>>()
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("students")
        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var studentsList = arrayListOf<Students>()
                for(student in snapshot.children){
                    var thisStudent = student.getValue(Students::class.java)
                    if (thisStudent != null) {
                        studentsList.add(thisStudent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


}