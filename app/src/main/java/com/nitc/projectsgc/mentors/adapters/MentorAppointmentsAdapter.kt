package com.nitc.projectsgc.mentors.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
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
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.mentors.access.MentorAppointmentsAccess
import de.hdodenhof.circleimageview.CircleImageView

class MentorAppointmentsAdapter(
    var context : Context,
    var appointments : ArrayList<Appointment>,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
) : RecyclerView.Adapter<MentorAppointmentsAdapter.MentorAppointmentsViewHolder>() {
    class MentorAppointmentsViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        var nameOfTheStudent = itemView.findViewById<TextView>(R.id.nameInMentorAppointmentsCard)
        var genderPic = itemView.findViewById<CircleImageView>(R.id.imageInMentorAppointmentsCard)
        var dobText = itemView.findViewById<TextView>(R.id.dobInMentorAppointmentsCard)
        var phoneText = itemView.findViewById<TextView>(R.id.phoneInMentorAppointmentsCard)
        var rollText = itemView.findViewById<TextView>(R.id.rollNoInMentorAppointmentsCard)
        var viewPastRecord = itemView.findViewById<Button>(R.id.viewPastRecordButtonInMentorAppointmentsCard)
        var cancelButton = itemView.findViewById<Button>(R.id.cancelButtonInMentorAppointmentsCard)
        var statusCard = itemView.findViewById<CardView>(R.id.statusCardInMentorAppointmentsCard)
        var statusText = itemView.findViewById<TextView>(R.id.statusTextInMentorAppointmentsCard)
        var remarksLayout = itemView.findViewById<ConstraintLayout>(R.id.remarksLayoutInMentorAppointmentsCard)
        var reactLayout = itemView.findViewById<ConstraintLayout>(R.id.reactLayoutInMentorAppointmentsCard)
        var completeButton = itemView.findViewById<Button>(R.id.completedButtonInMentorAppointmentsCard)
        var submitRemarks = itemView.findViewById<Button>(R.id.submitRemarksButtonInMentorAppointmentsCard)
        var remarksInput = itemView.findViewById<EditText>(R.id.remarksInputInMentorAppointmentsCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorAppointmentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.mentor_appointments_card,parent,false)
        return MentorAppointmentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    override fun onBindViewHolder(holder: MentorAppointmentsViewHolder, position: Int) {


        holder.statusCard.visibility = View.VISIBLE
        holder.statusText.text = appointments[position].status
        if(appointments[position].cancelled) {
            holder.cancelButton.visibility = View.GONE
            holder.viewPastRecord.visibility = View.GONE
            holder.completeButton.visibility = View.GONE
            holder.remarksLayout.visibility = View.VISIBLE
        }
        if(appointments[position].completed){
            holder.reactLayout.visibility = View.GONE
            holder.remarksLayout.visibility = View.VISIBLE
            holder.remarksInput.setText(appointments[position].remarks)
            holder.remarksInput.isEnabled = false
            holder.completeButton.visibility = View.GONE
        }

        var stdId = appointments[position].studentID.toString()
        var database =  FirebaseDatabase.getInstance()
        var reference = database.reference.child("students")
        reference.child(stdId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
            }

        })
        var studentLive = StudentsAccess(context,parentFragment).getStudent(stdId)
        studentLive.observe(parentFragment.viewLifecycleOwner){student->
            if(student != null) {
                holder.nameOfTheStudent.text = student.name.toString()
                holder.dobText.text = student.dateOfBirth.toString()
                holder.phoneText.text = student.phoneNumber.toString()
                holder.rollText.text = student.rollNo.toString()
                if (student.gender == "Male") {
                    holder.genderPic.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            parentFragment.resources,
                            R.drawable.boy_face,
                            null
                        )
                    )
                } else {
                    holder.genderPic.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            parentFragment.resources,
                            R.drawable.girl_face,
                            null
                        )
                    )
                }
            }else{
                holder.nameOfTheStudent.text = "NULL"
                holder.dobText.text = "NULL"
                holder.phoneText.text ="NULL"
                holder.rollText.text = "NULL"
                holder.statusText.text = "Student deleted"
            }
        }
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
        if(appointments[position].expanded){
            holder.reactLayout.visibility = View.GONE
            holder.remarksLayout.visibility = View.VISIBLE
        }else{
            holder.reactLayout.visibility = View.VISIBLE
            holder.remarksLayout.visibility = View.GONE
        }
        holder.submitRemarks.setOnClickListener {
            var remarksInput = holder.remarksInput.text.toString()
            if(remarksInput.isEmpty()){
                holder.remarksInput.error = "Enter remarks"
                holder.remarksInput.requestFocus()
                return@setOnClickListener
            }
            var remarkAppointment = appointments[position]
            remarkAppointment.remarks = remarksInput
            remarkAppointment.status = "Completed"
            remarkAppointment.completed = true
            var remarksLive = MentorAppointmentsAccess(context, sharedViewModel).giveRemarks(remarkAppointment)
            if(remarksLive != null){
                remarksLive.observe(parentFragment.viewLifecycleOwner){remarked->
                    if(remarked){
                        appointments[position] = remarkAppointment
                        notifyItemChanged(position)
                    }
                }
            }
        }

        holder.completeButton.setOnClickListener {
            appointments[position].expanded = !appointments[position].expanded
            notifyItemChanged(position)
        }


    }
}