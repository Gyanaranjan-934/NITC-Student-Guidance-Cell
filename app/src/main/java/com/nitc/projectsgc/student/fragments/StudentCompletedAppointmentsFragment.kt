package com.nitc.projectsgc.student.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentCompletedAppointmentsBinding
import com.nitc.projectsgc.student.access.AppointmentsAccess
import com.nitc.projectsgc.student.adapters.CompletedAppointmentsAdapter

class StudentCompletedAppointmentsFragment: Fragment() {

    lateinit var binding:FragmentStudentCompletedAppointmentsBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentCompletedAppointmentsBinding.inflate(inflater,container,false)

        getCompletedAppointments()


        return binding.root
    }
    private fun getCompletedAppointments() {
        var appointmentAccess = context?.let { AppointmentsAccess(it,this,sharedViewModel) }
        var completedLive = appointmentAccess?.getCompletedAppointments()
        if (completedLive != null) {
            completedLive.observe(viewLifecycleOwner){appointments->
                if(appointments != null && appointments.size != 0){
                    var completedAppointmentsAdapter = context?.let {
                        CompletedAppointmentsAdapter(
                            it,
                            this,
                            sharedViewModel,
                            appointments
                        )
                    }
                    binding.completedAppointmentsRecyclerViewInStudentDashboard.adapter = completedAppointmentsAdapter
                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.VISIBLE
//                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
                    binding.noCompletedTVInStudentDashboardFragment.visibility = View.GONE
//                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
                }else{
                    binding.noCompletedTVInStudentDashboardFragment.visibility = View.VISIBLE
//                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
//                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
                }
            }
        }
    }

}