package com.nitc.projectsgc

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.databinding.FragmentStudentDashBoardBinding
import com.nitc.projectsgc.student.access.AppointmentsAccess
import com.nitc.projectsgc.student.adapters.BookedAppointmentsAdapter
import java.text.SimpleDateFormat
import java.util.*

class StudentDashBoardFragment : Fragment() {
    lateinit var binding: FragmentStudentDashBoardBinding
    var selectedType = "Booked"
    private val sharedViewModel:SharedViewModel by activityViewModels()
    var appointmentAccess = context?.let { AppointmentsAccess(it,this,sharedViewModel) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentStudentDashBoardBinding.inflate(inflater, container, false)

        binding.bookedAppointmentsRecyclerViewStudentDashboard.layoutManager = LinearLayoutManager(context)
        binding.completedAppointmentsRecyclerViewInStudentDashboard.layoutManager = LinearLayoutManager(context)
        getBookedAppointments()
        binding.bookedAppointmentTypeButtonInStudentDashboardFragment.setOnClickListener {
            binding.bookedAppointmentTypeImageInStudentDashboardFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.bookedAppointmentTypeButtonInStudentDashboardFragment.setTextColor(Color.WHITE)
            binding.completedAppointmentTypeImageInStudentDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.completedAppointmentTypeButtonInStudentDashboardFragment.setTextColor(Color.BLACK)
            selectedType = "Booked"
            getBookedAppointments()
        }
        binding.completedAppointmentTypeButtonInStudentDashboardFragment.setOnClickListener {
            binding.bookedAppointmentTypeImageInStudentDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.bookedAppointmentTypeButtonInStudentDashboardFragment.setTextColor(Color.BLACK)
            binding.completedAppointmentTypeImageInStudentDashboardFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.completedAppointmentTypeButtonInStudentDashboardFragment.setTextColor(Color.WHITE)
            selectedType = "Completed"
            getCompletedAppointments()
        }

        return binding.root

    }

    private fun getCompletedAppointments() {

    }

    private fun getBookedAppointments() {
        var bookedLive = appointmentAccess?.getBookedAppointments(SimpleDateFormat("dd-MM-yyyy").format(
            Date()
        ))
        if (bookedLive != null) {
            bookedLive.observe(viewLifecycleOwner){appointments->
                if(appointments != null && appointments.size != 0){
                    var bookedAppointmentsAdapter = context?.let {
                        BookedAppointmentsAdapter(
                            it,
                            this,
                            sharedViewModel,
                            appointments
                        )
                    }
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.adapter = bookedAppointmentsAdapter
                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.VISIBLE
                }else{
                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.VISIBLE
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
                    binding.noCompletedTVInStudentDashboardFragment.visibility = View.GONE
                }
            }
        }
    }


}