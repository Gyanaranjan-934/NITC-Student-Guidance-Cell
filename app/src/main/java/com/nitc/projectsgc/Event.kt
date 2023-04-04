package com.nitc.projectsgc

data class Event(
    var id:String = "",
    var heading:String? = null,
    var venue:String? = null,
    var eventDate:String? = null,
    var eventTime:String? = null,
    var link:String = "",
    var publishDate:String? = null,
    var mentorName:String? = null
)
