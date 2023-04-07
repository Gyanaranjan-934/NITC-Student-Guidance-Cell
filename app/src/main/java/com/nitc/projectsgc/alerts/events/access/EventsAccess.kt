package com.nitc.projectsgc.alerts.events.access

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.projectsgc.Event
import com.nitc.projectsgc.SharedViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EventsAccess(
    var context: Context,
    var sharedViewModel: SharedViewModel,
    var parentFragment: Fragment
) {

    suspend fun addEvent(event: Event):Boolean{
        return suspendCoroutine { continuation ->
            val database = FirebaseDatabase.getInstance()
            val reference = database.reference.child("events")
            val eventID = reference.push().key.toString()
            event.id = eventID
            reference.child(event.type).child(eventID).setValue(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(true)
                } else continuation.resume(false)
            }
        }
    }

    suspend fun updateEvent(eventID:String,event: Event):Boolean{
        return suspendCoroutine { continuation ->
            val database = FirebaseDatabase.getInstance()
            val reference = database.reference.child("events")
            reference.child(eventID).setValue(event).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(true)
                } else continuation.resume(false)
            }
        }
    }
    suspend fun getTypeEvents(isStudent:Boolean,eventType:String):ArrayList<Event>?{
        return suspendCoroutine {continuation->
            val eventsLive = MutableLiveData<ArrayList<Event>>()
            val events = arrayListOf<Event>()
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val database = FirebaseDatabase.getInstance()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val reference = database.reference.child("events").child(eventType)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                            val event = ds.getValue(Event::class.java)
                            if (event != null) {
                                var publishDate =
                                    SimpleDateFormat("dd-MM-yyyy").parse(event.eventDate)
                                if (isStudent) {
                                    if (publishDate.after(today) || publishDate == today) {
                                        events.add(event)
                                    }
                                } else events.add(event)
                            }else continuation.resume(null)


                    }
                    val sortedEvents =
                        events.sortedBy { LocalDate.parse(it.publishDate, formatter) }
                            .toCollection(ArrayList<Event>())
                    continuation.resume(sortedEvents)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(null)
                }

            })
        }
    }


    suspend fun getEvents(isStudent:Boolean):ArrayList<Event>?{
        return suspendCoroutine {continuation->
            val eventsLive = MutableLiveData<ArrayList<Event>>()
            val events = arrayListOf<Event>()
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val database = FirebaseDatabase.getInstance()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val reference = database.reference.child("events")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        for (eventName in ds.children){
                            val event = eventName.getValue(Event::class.java)
                            if (event != null) {
                                var publishDate =
                                    SimpleDateFormat("dd-MM-yyyy").parse(event.eventDate)
                                if (isStudent) {
                                    if (publishDate.after(today) || publishDate == today) {
                                        events.add(event)
                                    }
                                } else events.add(event)
                            }else continuation.resume(null)
                        }

                    }
                    val sortedEvents =
                        events.sortedBy { LocalDate.parse(it.publishDate, formatter) }
                            .toCollection(ArrayList<Event>())
                    continuation.resume(sortedEvents)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(null)
                }

            })
        }
    }


}