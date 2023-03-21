package com.nitc.projectsgc.student

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.Login.access.LoginAccess
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentDashBoardBinding
import com.nitc.projectsgc.student.access.AppointmentsAccess
import com.nitc.projectsgc.student.access.BasicStudentAccess
import com.nitc.projectsgc.student.adapters.BookedAppointmentsAdapter
import com.nitc.projectsgc.student.adapters.CompletedAppointmentsAdapter
import java.text.SimpleDateFormat
import java.util.*

class StudentDashboardFragment: Fragment() {
    lateinit var binding:FragmentStudentDashBoardBinding
    var selectedType = "Booked"
    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentDashBoardBinding.inflate(inflater,container,false)
        binding.bookedAppointmentsRecyclerViewStudentDashboard.layoutManager = LinearLayoutManager(context)
        binding.completedAppointmentsRecyclerViewInStudentDashboard.layoutManager = LinearLayoutManager(context)
        sharedViewModel.rescheduling = false
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
//        var studentLive = context?.let { BasicStudentAccess(it,sharedViewModel).getStudent() }
//        studentLive!!.observe(viewLifecycleOwner){student->
//            if(student != null){
//                sharedViewModel.currentStudent = student
//                Log.d("student",student.name)
//            }
//        }

        binding.logoutButtonInStudentDashboardFragment.setOnClickListener {
            var logoutSuccess = context?.let { it1 -> LoginAccess(it1,this,sharedViewModel).logout() }
//            logoutLive!!.observe(viewLifecycleOwner){logoutSuccess->
                if(logoutSuccess == true){
                    findNavController().navigate(R.id.loginFragment)
                }else{
                    Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
                }
//            }

        }

        binding.bookAppointmentButtonInStudentDashboard.setOnClickListener {
            findNavController().navigate(R.id.bookingFragment)
        }


        val backCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Call a method in your Fragment to handle the navigation
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backCallback)

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
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
                    binding.noCompletedTVInStudentDashboardFragment.visibility = View.GONE
                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
                }else{
                    binding.noCompletedTVInStudentDashboardFragment.visibility = View.VISIBLE
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
                }
            }
        }
    }

    private fun getBookedAppointments() {
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
                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.VISIBLE
                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
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