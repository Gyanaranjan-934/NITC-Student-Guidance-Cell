package com.nitc.projectsgc.admin.adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student
import com.nitc.projectsgc.admin.access.StudentsAccess
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class StudentsAdapter(
    var context: Context,
    var students:ArrayList<Student>,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
): RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder>() {
    class StudentsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var nameText = itemView.findViewById<TextView>(R.id.nameInStudentCard)
        var personImage = itemView.findViewById<CircleImageView>(R.id.imageInStudentCard)
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

        val loadingDialog = Dialog(context)
        loadingDialog.setContentView(parentFragment.requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if(students[position].gender == "Male"){
            holder.personImage.setImageDrawable(ResourcesCompat.getDrawable(parentFragment.resources,R.drawable.boy_face,null))
        }
        else{
            holder.personImage.setImageDrawable(ResourcesCompat.getDrawable(parentFragment.resources,R.drawable.girl_face,null))
        }
        holder.viewAppointmentsButton.setOnClickListener {
            var appointmentsLive = StudentsAccess(context,parentFragment).getAppointments(students[position].rollNo)
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
                    dialog.dismiss()
                    var deleteCoroutineScope = CoroutineScope(Dispatchers.Main)
                    deleteCoroutineScope.launch {
                        loadingDialog.create()
                        loadingDialog.show()
                        var deleted = StudentsAccess(
                            context,
                            parentFragment
                        ).deleteStudent(
                            students[position].rollNo.toString(),
                            students[position].emailId.toString()
                        )
                        loadingDialog.cancel()
                        deleteCoroutineScope.cancel()
                        if (deleted) {
                            Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show()
                            students.removeAt(position)
                            notifyDataSetChanged()
                        }else{
                            Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                .setNegativeButton("No"){dialog,which->
                    dialog.dismiss()
                }
                .create().show()
        }


    }


}