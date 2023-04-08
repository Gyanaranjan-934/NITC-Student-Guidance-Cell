package com.nitc.projectsgc.mentors.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.BookedAppointmentCardBinding

class PastRecordAdapter(
    var context: Context,
    var parentFragment: Fragment,
    var pastRecord:ArrayList<Appointment>,
    var sharedViewModel: SharedViewModel
    ): RecyclerView.Adapter<PastRecordAdapter.PastRecordViewHolder>() {
    class PastRecordViewHolder(val binding:BookedAppointmentCardBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastRecordViewHolder {
        val binding = BookedAppointmentCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PastRecordViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pastRecord.size
    }

    override fun onBindViewHolder(holder: PastRecordViewHolder, position: Int) {

        holder.binding.mentorNameInBookedAppointmentCard.text = pastRecord[position].mentorName
        holder.binding.typeInBookedAppointmentCard.text = pastRecord[position].mentorType
        holder.binding.dateOfAppointmentInBookedAppointmentCard.text = pastRecord[position].date
        holder.binding.timeLayoutInBookedAppointmentCard.visibility = View.GONE
        holder.binding.statusCardInBookedAppointmentsCard.visibility = View.GONE
        holder.binding.rescheduleButtonInUpcomingCard.visibility = View.GONE
        holder.binding.cancelAppointmentInBookedAppointmentCard.visibility = View.GONE
        holder.binding.remarksCardInBookedAppointmentsCard.visibility = View.VISIBLE
        holder.binding.remarksTextInBookedAppointmentsCard.text = pastRecord[position].remarks

        holder.binding.imageInBookedAppointmentCard.setOnClickListener {
                sharedViewModel.profileForMentorID = pastRecord[position].mentorID.toString()
                sharedViewModel.profileForMentorType = pastRecord[position].mentorType.toString()
                parentFragment.findNavController().navigate(R.id.mentorProfileFragment)

        }
    }
}