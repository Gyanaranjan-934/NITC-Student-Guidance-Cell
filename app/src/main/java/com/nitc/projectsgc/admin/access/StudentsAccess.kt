package com.nitc.projectsgc.admin.access

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.nitc.projectsgc.Students

class StudentsAccess(var context: Context) {


    fun getStudents(): LiveData<ArrayList<Students>> {
        var studentsLive = MutableLiveData<ArrayList<Students>>(null)
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
                studentsLive.postValue(studentsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()

            }

        })
        return studentsLive
    }

    fun deleteStudent(rollNo: String,email:String):LiveData<Boolean>{

        var deleteLive = MutableLiveData<Boolean>(false)
        var database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("students")
        reference.child(rollNo).removeValue().addOnSuccessListener {
            deleteLive.postValue(true)
        }
            .addOnFailureListener {error->
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
                deleteLive.postValue(false)
            }

        return deleteLive
    }


}