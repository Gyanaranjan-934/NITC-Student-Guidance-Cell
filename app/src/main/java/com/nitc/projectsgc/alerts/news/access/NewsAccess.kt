package com.nitc.projectsgc.alerts.news.access

import android.content.Context
import android.renderscript.Sampler.Value
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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


    suspend fun getNews():ArrayList<News>?{
        return suspendCoroutine { continuation ->
            var database = FirebaseDatabase.getInstance()
            var reference = database.reference.child("news")
            var newsArray = arrayListOf<News>()
            reference.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for ( ds in snapshot.children){
                        var news = ds.getValue(News::class.java)
                        if(news != null) newsArray.add(news)
                        else {
                            continuation.resume(null)
                            return
                        }
                    }
                    continuation.resume(newsArray)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(null)
                }

            })
        }
    }


}