package com.nitc.projectsgc.mentors.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student
import com.nitc.projectsgc.mentors.access.MentorAppointmentsAccess

class MentorAppointmentsAdapter(
    var context : Context,
    var appointments : ArrayList<Appointment>,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
) : RecyclerView.Adapter<MentorAppointmentsAdapter.MentorAppointmentsViewHolder>() {
    class MentorAppointmentsViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var nameOfTheStudent = itemView.findViewById<TextView>(R.id.nameInStudentCard)
        var dobText = itemView.findViewById<TextView>(R.id.dobInStudentCard)
        var phoneText = itemView.findViewById<TextView>(R.id.phoneInStudentCard)
        var rollText = itemView.findViewById<TextView>(R.id.rollNoInStudentCard)
        var viewPastRecord = itemView.findViewById<Button>(R.id.viewPastRecordButtonInStudentCard)
        var cancelButton = itemView.findViewById<Button>(R.id.cancelButtonInStudentCard)
        var viewAppointmentButton = itemView.findViewById<Button>(R.id.viewAppointmentsButtonInStudentCard)
        var deleteStudentButton = itemView.findViewById<Button>(R.id.deleteButtonInStudentCard)
        var statusCard = itemView.findViewById<CardView>(R.id.statusCardInStudentCard)
        var statusText = itemView.findViewById<TextView>(R.id.statusTextInStudentCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorAppointmentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.student_card,parent,false)
        return MentorAppointmentsAdapter.MentorAppointmentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    override fun onBindViewHolder(holder: MentorAppointmentsViewHolder, position: Int) {

        holder.statusCard.visibility = View.VISIBLE
        holder.statusText.text = appointments[position].status
        if(appointments[position].cancelled ||appointments[position].completed) {
            holder.cancelButton.visibility = View.GONE
            holder.viewPastRecord.visibility = View.GONE
        }
        else {
            holder.cancelButton.visibility = View.VISIBLE
            holder.viewPastRecord.visibility = View.VISIBLE
        }
        var stdId = appointments[position].studentID.toString()
        var database =  FirebaseDatabase.getInstance()
        var reference = database.reference.child("students")
        reference.child(stdId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var student = snapshot.getValue(Student::class.java)
                holder.nameOfTheStudent.text = student?.name.toString()
                holder.dobText.text = student?.dateOfBirth.toString()
                holder.phoneText.text = student?.phoneNumber.toString()
                holder.rollText.text = student?.rollNo.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
            }

        })
        holder.cancelButton.setOnClickListener {
            var cancelAppointment = appointments[position]
            cancelAppointment.cancelled = true
            cancelAppointment.status = "Cancelled by mentor"
            var cancelLive = MentorAppointmentsAccess(context, sharedViewModel = sharedViewModel).cancelAppointment(appointment = cancelAppointment)
            if(cancelLive != null){
                cancelLive.observe(parentFragment.viewLifecycleOwner){cancelSuccess->
                    if(cancelSuccess){
                        Toast.makeText(context,"Cancelled",Toast.LENGTH_SHORT).show()
                        appointments[position] = cancelAppointment
                        notifyItemChanged(position)
                    }
                }
            }
        }

        holder.viewPastRecord.setOnClickListener {
            var appointmentsLive = MentorAppointmentsAccess(context, sharedViewModel).getStudentRecord(studentID = appointments[position].studentID.toString())
            if(appointmentsLive != null){
                appointmentsLive.observe(parentFragment.viewLifecycleOwner){pastAppointments->
                    if(pastAppointments == null || pastAppointments.isEmpty()){
                        Toast.makeText(context,"No past record found",Toast.LENGTH_SHORT).show()
                    }else{
                        sharedViewModel.pastRecordStudentID = appointments[position].studentID.toString()
                        parentFragment.findNavController().navigate(R.id.pastRecordFragment)

                    }
                }
            }
        }
        holder.deleteStudentButton.visibility = View.GONE
        holder.viewAppointmentButton.visibility = View.GONE


    }
}