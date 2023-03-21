package com.nitc.projectsgc.student.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.databinding.BookedAppointmentCardBinding
import com.nitc.projectsgc.student.access.AppointmentsAccess

class CompletedAppointmentsAdapter(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel,
    var appointments:ArrayList<Appointment>
): RecyclerView.Adapter<CompletedAppointmentsAdapter.CompletedAppointmentsViewHolder>() {

    class CompletedAppointmentsViewHolder(val binding: BookedAppointmentCardBinding):RecyclerView.ViewHolder(binding.root) {

        var database = FirebaseDatabase.getInstance()
        var reference = database.reference.child("types")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedAppointmentsViewHolder {
        var binding = BookedAppointmentCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CompletedAppointmentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    override fun onBindViewHolder(holder: CompletedAppointmentsViewHolder, position: Int) {
            holder.binding.cancelAppointmentInBookedAppointmentCard.visibility = View.GONE
            holder.binding.rescheduleButtonInUpcomingCard.visibility = View.GONE
            holder.binding.remarksCardInBookedAppointmentsCard.visibility = View.VISIBLE
            holder.binding.remarksTextInBookedAppointmentsCard.text = appointments[position].remarks

        if(appointments[position].completed || appointments[position].cancelled){
            holder.binding.cancelAppointmentInBookedAppointmentCard.visibility = View.GONE
            holder.binding.rescheduleButtonInUpcomingCard.visibility = View.GONE
        }
        holder.binding.imageInBookedAppointmentCard.setOnClickListener {
            sharedViewModel.profileForMentorID = appointments[position].mentorID.toString()
            sharedViewModel.profileForMentorType = appointments[position].mentorType.toString()
            parentFragment.findNavController().navigate(R.id.mentorProfileFragment)
        }
        holder.binding.typeInBookedAppointmentCard.text = appointments[position].mentorType.toString()
        holder.binding.statusTextInBookedAppointmentsCard.text = appointments[position].status
        var mentorName = "NA"
        var mentorLive = MentorsAccess(context).getMentor(appointments[position].mentorType.toString(),appointments[position].mentorID.toString())
        mentorLive.observe(parentFragment.viewLifecycleOwner) { mentor ->
            if (mentor != null) {
                holder.binding.mentorNameInBookedAppointmentCard.text = mentor.name.toString()
                mentorName = mentor.name.toString()
            }
        }
        holder.binding.dateOfAppointmentInBookedAppointmentCard.text = appointments[position].date.toString()
        holder.binding.timeOfAppointmentInBookedAppointmentCard.text = appointments[position].timeSlot.toString()
        holder.binding.rescheduleButtonInUpcomingCard.setOnClickListener {
            parentFragment.findNavController().navigate(R.id.bookingFragment)
            sharedViewModel.reschedulingAppointment = appointments[position]
            sharedViewModel.rescheduling = true
            sharedViewModel.reschedulingMentorName = mentorName
        }
        holder.binding.cancelAppointmentInBookedAppointmentCard.setOnClickListener {
            appointments[position].status = "Cancelled by student"
            appointments[position].cancelled = true
            var cancelLive = AppointmentsAccess(context,parentFragment, sharedViewModel).cancelAppointment(appointment = appointments[position])
            cancelLive.observe(parentFragment.viewLifecycleOwner){cancelled->
                if(cancelled != null){
                    if(cancelled){
                        Toast.makeText(context,"Cancelled",Toast.LENGTH_SHORT).show()
                        holder.binding.statusTextInBookedAppointmentsCard.text = appointments[position].status
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}