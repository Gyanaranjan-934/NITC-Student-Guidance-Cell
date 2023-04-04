package com.nitc.projectsgc.alerts.news.access

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.News
import com.nitc.projectsgc.SharedViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NewsAccess(
    var context:Context,
    var sharedViewModel: SharedViewModel,
    var parentFragment:Fragment
) {

    suspend fun addNews(news: News):Boolean{
        return suspendCoroutine {continuation ->

            var database = FirebaseDatabase.getInstance()
            var reference = database.reference.child("news")
            var newsID = reference.push().key.toString()
            news.newsID = newsID
            reference.setValue(news).addOnCompleteListener { task->
                if(task.isSuccessful){
                    continuation.resume(true)
                }else{
                    continuation.resume(false)
                }
            }
        }
    }

}