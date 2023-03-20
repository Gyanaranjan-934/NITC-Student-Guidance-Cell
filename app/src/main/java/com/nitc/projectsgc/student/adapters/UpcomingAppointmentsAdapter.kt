package com.nitc.projectsgc.student.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.UpcomingAppointmentCardBinding

class UpcomingAppointmentsAdapter(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel,
    var appointments:Array<Appointment>
): RecyclerView.Adapter<UpcomingAppointmentsAdapter.UpcomingAppointmentsViewHolder>() {

    class UpcomingAppointmentsViewHolder(val binding: UpcomingAppointmentCardBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingAppointmentsViewHolder {
        var binding = UpcomingAppointmentCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UpcomingAppointmentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    override fun onBindViewHolder(holder: UpcomingAppointmentsViewHolder, position: Int) {

    }
}