package com.nitc.projectsgc

import androidx.lifecycle.ViewModel

class SharedViewModel:ViewModel() {

    lateinit var currentMentor: Mentor
    lateinit var currentStudent: Student
    lateinit var profileForMentorID:String
    lateinit var profileForMentorType:String
    var currentUserID = "NA"
    var userType = "Student"
    var rescheduling:Boolean = false
    lateinit var viewAppointmentStudentID:String
    var reschedulingMentorName = "NA"
    lateinit var pastRecordStudentID:String
    lateinit var reschedulingAppointment:Appointment
    var isUpdatingEvent:Boolean = false
    lateinit var updatingOldEvent:Event
}