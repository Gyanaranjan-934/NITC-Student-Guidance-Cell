package com.nitc.projectsgc.alerts.news.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.News
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.news.access.NewsAccess
import com.nitc.projectsgc.databinding.NewsCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AllNewsAdapter(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel,
    var news:ArrayList<News>
): RecyclerView.Adapter<AllNewsAdapter.AllNewsViewHolder>() {
    class AllNewsViewHolder(val binding:NewsCardBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsViewHolder {
        val binding = NewsCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllNewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return news.size
    }

    override fun onBindViewHolder(holder: AllNewsViewHolder, position: Int) {
        holder.binding.mentorNameInNewsCard.text = news[position].mentorName.toString()
        holder.binding.newsTextInNewsCard.text = news[position].news.toString()
        holder.binding.dateTextInNewsCard.text = news[position].publishDate.toString()
        if(sharedViewModel.userType == "Mentor") {
            if(sharedViewModel.currentMentor.userName == news[position].mentorID) holder.binding.deleteButtonInNewsCard.visibility = View.VISIBLE
            else holder.binding.deleteButtonInNewsCard.visibility = View.GONE
        }
        else holder.binding.deleteButtonInNewsCard.visibility = View.GONE
        holder.binding.deleteButtonInNewsCard.setOnClickListener {
            var coroutineScope = CoroutineScope(Dispatchers.Main)
            coroutineScope.launch {
                deleteNews(position)
                coroutineScope.cancel()
            }
        }
    }

    private suspend fun deleteNews(position: Int) {
        var deleted = NewsAccess(context, sharedViewModel, parentFragment).deleteNews(news[position].newsID.toString())
        if(deleted){
            Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }else Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
    }
}