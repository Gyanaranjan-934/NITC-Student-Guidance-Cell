package com.nitc.projectsgc

data class Appointment(
    var date:String? = null,
    var timeSlot:String? = null,
    var mentorType:String? = null,
    var rescheduled:Boolean = false,
    var mentorID:String? = null,
    var studentID:String? = null,
    var mentorName:String? = null,
    var completed:Boolean = false,
    var status:String? = null,
    var remarks:String? = null,
    var cancelled:Boolean = false,
    var expanded:Boolean = false,
    var id:String? = null,
    var problemDescription:String? = null
) {
}
