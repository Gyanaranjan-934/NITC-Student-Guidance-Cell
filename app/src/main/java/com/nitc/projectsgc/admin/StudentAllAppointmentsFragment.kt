package com.nitc.projectsgc.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.databinding.FragmentPastRecordBinding
import com.nitc.projectsgc.student.adapters.BookedAppointmentsAdapter

class StudentAllAppointmentsFragment: Fragment() {
    lateinit var binding:FragmentPastRecordBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPastRecordBinding.inflate(inflater,container,false)
        binding.headingTVInPastRecordFragment.text = "All Appointments"
        var studentLive = context?.let { StudentsAccess(it,this).getStudent(sharedViewModel.viewAppointmentStudentID) }
        if(studentLive != null){
            studentLive.observe(viewLifecycleOwner){student->
                if(student != null) {

                    binding.dobInStudentCard.text = student.dateOfBirth
                    binding.nameInStudentCard.text = student.name
                    binding.phoneInStudentCard.text = student.phoneNumber
                    binding.rollNoInStudentCard.text = student.rollNo
                }else{
                    binding.dobInStudentCard.text = "NULL"
                    binding.nameInStudentCard.text = "NULL"
                    binding.phoneInStudentCard.text = "NULL"
                    binding.rollNoInStudentCard.text = "NULL"
                }
            }
        }
        var appointmentsLive = context?.let { StudentsAccess(it,this).getAppointments(sharedViewModel.viewAppointmentStudentID) }
        if(appointmentsLive != null) {
            appointmentsLive.observe(viewLifecycleOwner){appointments->
                if(appointments != null){
                    binding.pastRecordRecyclerViewInPastRecordFragment.layoutManager = LinearLayoutManager(context)
                    binding.pastRecordRecyclerViewInPastRecordFragment.adapter = context?.let {
                        BookedAppointmentsAdapter(
                            it,
                            this,
                            sharedViewModel,
                            appointments,
                            true
                        )
                    }
                }
            }

        }

        return binding.root
    }
}