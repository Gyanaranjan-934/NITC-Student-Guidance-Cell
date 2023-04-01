package com.nitc.projectsgc

data class Event(
    var id:String = "",
    var heading:String,
    var venue:String,
    var eventDate:String,
    var eventTime:String,
    var link:String = "",
    var publishDate:String,
    var mentorName:String
)
