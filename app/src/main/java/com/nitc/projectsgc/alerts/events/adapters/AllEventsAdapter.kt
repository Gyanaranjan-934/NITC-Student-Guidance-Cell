package com.nitc.projectsgc.alerts.events.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.Event
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.EventCardBinding

class AllEventsAdapter(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel,
    var isStudent:Boolean = false,
    var events:ArrayList<Event>
    ):RecyclerView.Adapter<AllEventsAdapter.AllEventsViewHolder>() {
    class AllEventsViewHolder(val binding:EventCardBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllEventsViewHolder {
        val binding = EventCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllEventsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: AllEventsViewHolder, position: Int) {
        holder.binding.mentorNameInEventCard.text = events[position].mentorName.toString()
        holder.binding.dateOfPublishInEventCard.text = events[position].mentorName.toString()
        holder.binding.headingInEventCard.text = events[position].mentorName.toString()
        holder.binding.venueOfEventInEventCard.text = events[position].mentorName.toString()
        holder.binding.timeOfEventInEventCard.text = events[position].mentorName.toString()
        holder.binding.dateOfEventInEventCard.text = events[position].mentorName.toString()
        if(events[position].link == " "){
            holder.binding.linkLayoutInEventCard.visibility = View.GONE
        }
        if(isStudent){
            holder.binding.updateButtonInEventCard.visibility = View.GONE
            holder.binding.deleteButtonInEventCard.visibility = View.GONE
        }
    }

}