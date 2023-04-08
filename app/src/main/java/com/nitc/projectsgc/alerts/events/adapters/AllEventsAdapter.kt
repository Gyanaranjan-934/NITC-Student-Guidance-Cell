package com.nitc.projectsgc.alerts.events.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.Event
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.events.access.EventsAccess
import com.nitc.projectsgc.databinding.EventCardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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
        holder.binding.dateOfPublishInEventCard.text = events[position].publishDate.toString()
        holder.binding.headingInEventCard.text = events[position].heading.toString()
        holder.binding.venueOfEventInEventCard.text = events[position].venue.toString()
        holder.binding.timeOfEventInEventCard.text = events[position].eventTime.toString()
        holder.binding.dateOfEventInEventCard.text = events[position].eventDate.toString()
        if(events[position].link == " "){
            holder.binding.linkLayoutInEventCard.visibility = View.GONE
        }else{
            holder.binding.linkLayoutInEventCard.visibility = View.VISIBLE
            holder.binding.linkTextInEventCard.text = events[position].link.toString()
        }
        if(events[position].mentorID == sharedViewModel.currentMentor.userName){
            holder.binding.deleteButtonInEventCard.visibility = View.VISIBLE
            holder.binding.updateButtonInEventCard.visibility = View.VISIBLE
        }else{
            holder.binding.deleteButtonInEventCard.visibility = View.GONE
            holder.binding.updateButtonInEventCard.visibility = View.GONE
        }
        if(events[position].eventTime == " ") holder.binding.timeLayoutInEventCard.visibility =View.GONE
        else holder.binding.timeLayoutInEventCard.visibility = View.VISIBLE
        if(isStudent){
            holder.binding.updateButtonInEventCard.visibility = View.GONE
            holder.binding.deleteButtonInEventCard.visibility = View.GONE
        }
        holder.binding.updateButtonInEventCard.setOnClickListener {
            sharedViewModel.isUpdatingEvent = true
            sharedViewModel.updatingOldEvent = events[position]
            parentFragment.findNavController().navigate(R.id.addEventFragment)
        }

        holder.binding.deleteButtonInEventCard.setOnClickListener {
            val deleteCoroutineScope = CoroutineScope(Dispatchers.Main)
            val loadingDialog = Dialog(context)
            loadingDialog.setContentView(parentFragment.requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
            loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            deleteCoroutineScope.launch {
                loadingDialog.create()
                loadingDialog.show()
                val deleted = EventsAccess(context, sharedViewModel, parentFragment).deleteEvent(events[position])
                if(deleted){
                    events.removeAt(position)
                    loadingDialog.cancel()
                    deleteCoroutineScope.cancel()
                    notifyDataSetChanged()
                }else{
                    Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
                    loadingDialog.cancel()
                    deleteCoroutineScope.cancel()
                }

            }
        }
    }

}