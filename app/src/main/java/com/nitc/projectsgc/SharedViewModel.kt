package com.nitc.projectsgc

import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel() {

    lateinit var updateMentor:Mentors
    lateinit var currentStudent: Students
    var currentUserID = "NA"
    var userType = "Student"
}