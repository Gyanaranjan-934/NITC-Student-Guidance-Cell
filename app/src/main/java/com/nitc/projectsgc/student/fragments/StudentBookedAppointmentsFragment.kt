package com.nitc.projectsgc.student.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentBookedAppointmentsBinding
import com.nitc.projectsgc.student.access.AppointmentsAccess
import com.nitc.projectsgc.student.adapters.BookedAppointmentsAdapter
import java.text.SimpleDateFormat
import java.util.*

class StudentBookedAppointmentsFragment: Fragment() {

    lateinit var binding:FragmentStudentBookedAppointmentsBinding

    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentBookedAppointmentsBinding.inflate(inflater,container,false)

        binding.bookedAppointmentsRecyclerViewStudentDashboard.layoutManager = LinearLayoutManager(context)

        getBookedAppointments()


        return binding.root
    }
    fun getBookedAppointments() {
        Log.d("today","this called")
        var dateToday = SimpleDateFormat("dd-MM-yyyy").format(Date()).toString()
        var appointmentAccess = context?.let { AppointmentsAccess(it,this,sharedViewModel) }
        Log.d("today",dateToday)
        var bookedLive = appointmentAccess?.getBookedAppointments()
        if (bookedLive != null) {
            bookedLive.observe(viewLifecycleOwner){appointments->
                if(appointments != null && appointments.size != 0){
                    var bookedAppointmentsAdapter = context?.let {
                        BookedAppointmentsAdapter(
                            it,
                            this,
                            sharedViewModel,
                            appointments,
                            false
                        )
                    }
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.adapter = bookedAppointmentsAdapter
//                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.VISIBLE
                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
                }else{
                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.VISIBLE
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
//                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
//                    binding.noCompletedTVInStudentDashboardFragment.visibility = View.GONE
                }
            }
        }
    }


}