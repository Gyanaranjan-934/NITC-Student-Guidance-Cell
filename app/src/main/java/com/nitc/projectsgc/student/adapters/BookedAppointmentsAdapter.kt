package com.nitc.projectsgc.student.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.UpcomingAppointmentCardBinding

class BookedAppointmentsAdapter(
    var context: Context,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel,
    var appointments:ArrayList<Appointment>
): RecyclerView.Adapter<BookedAppointmentsAdapter.UpcomingAppointmentsViewHolder>() {

    class UpcomingAppointmentsViewHolder(val binding: UpcomingAppointmentCardBinding):RecyclerView.ViewHolder(binding.root) {

        var database = FirebaseDatabase.getInstance()
        var reference = database.reference.child("types")
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

        var typeReference = holder.reference.child(appointments[position].mentorType.toString())
        typeReference.child(appointments[position].mentorID.toString()).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var mentor = snapshot.getValue(Mentor::class.java)
                if (mentor != null) {
                    holder.binding.mentorNameInUpcomingAppointmentCard.text = mentor.name.toString()
                    holder.binding.phoneInMentorCard.text = mentor.phone.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error : $error",Toast.LENGTH_LONG).show()
            }
        })
        holder.binding.dateOfAppointmentInUpcomingAppointmentCard.text = appointments[position].date.toString()
        holder.binding.timeOfAppointmentInUpcomingAppointmentCard.text = appointments[position].timeSlot.toString()
        holder.binding.rescheduleButtonInUpcomingCard.setOnClickListener {

        }
        holder.binding.cancelAppointmentInUpcomingAppointmentCard.setOnClickListener {

        }
    }
}