package com.nitc.projectsgc.admin.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.booking.access.BookingAccess

class StudentsAdapter(
    var context: Context,
    var students:ArrayList<Student>,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
): RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder>() {
    class StudentsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var nameText = itemView.findViewById<TextView>(R.id.nameInStudentCard)
        var dobText = itemView.findViewById<TextView>(R.id.dobInStudentCard)
        var phoneText = itemView.findViewById<TextView>(R.id.phoneInStudentCard)
        var rollText = itemView.findViewById<TextView>(R.id.rollNoInStudentCard)
        var viewAppointmentsButton = itemView.findViewById<Button>(R.id.viewAppointmentsButtonInStudentCard)
        var deleteButton = itemView.findViewById<Button>(R.id.deleteButtonInStudentCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.student_card,parent,false)
        return StudentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return students.size
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        holder.nameText.text = students[position].name.toString()
        holder.dobText.text = students[position].dateOfBirth.toString()
        holder.phoneText.text = students[position].phoneNumber.toString()
        holder.rollText.text = students[position].rollNo.toString()

        holder.viewAppointmentsButton.setOnClickListener {
            var appointmentsLive = StudentsAccess(context).getAppointments(students[position].rollNo)
            if(appointmentsLive != null){
                appointmentsLive.observe(parentFragment.viewLifecycleOwner){appointments->
                    if(appointments != null){
                        if(!appointments.isEmpty()) {

                            sharedViewModel.viewAppointmentStudentID = students[position].rollNo
                            parentFragment.findNavController()
                                .navigate(R.id.studentAllAppointmentsFragment)
                        }
                    }
                }
            }
        }

        holder.deleteButton.setOnClickListener {
            var confirmDeleteBuilder = AlertDialog.Builder(context)
            confirmDeleteBuilder.setTitle("Are you sure ?")
                .setMessage("You want to delete this student?")
                .setPositiveButton("Yes"){dialog,which->
                    var deletedLive = StudentsAccess(context).deleteStudent(students[position].rollNo.toString(),students[position].emailId.toString())
                    deletedLive.observe(parentFragment.viewLifecycleOwner){deleted->
                        if(deleted){
                            Toast.makeText(context,"Student deleted",Toast.LENGTH_SHORT).show()
                            students.removeAt(position)
                            notifyItemChanged(position)
                        }
                    }
                    dialog.dismiss()

                }
                .setNegativeButton("No"){dialog,which->
                    dialog.dismiss()
                }
                .create().show()
        }


    }


}