package com.nitc.projectsgc.student.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentCompletedAppointmentsBinding
import com.nitc.projectsgc.student.access.AppointmentsAccess
import com.nitc.projectsgc.student.adapters.CompletedAppointmentsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class StudentCompletedAppointmentsFragment: Fragment() {

    lateinit var binding: FragmentStudentCompletedAppointmentsBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentCompletedAppointmentsBinding.inflate(inflater, container, false)
        binding.completedAppointmentsRecyclerViewInStudentDashboard.layoutManager = LinearLayoutManager(context)
        binding.swipeLayoutInStudentCompletedAppointmentsFragment.setOnRefreshListener {
            getCompletedAppointments()
            binding.swipeLayoutInStudentCompletedAppointmentsFragment.isRefreshing = false
        }


        getCompletedAppointments()


        return binding.root
    }

    private fun getCompletedAppointments() {

        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(
            requireActivity().layoutInflater.inflate(
                R.layout.loading_dialog,
                null
            )
        )
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val completedAppointmentsCoroutineScope = CoroutineScope(Dispatchers.Main)
        completedAppointmentsCoroutineScope.launch {
            loadingDialog.create()
            loadingDialog.show()
            var appointmentAccess = context?.let {
                AppointmentsAccess(
                    it,
                    this@StudentCompletedAppointmentsFragment,
                    sharedViewModel
                )
            }
            var appointments = appointmentAccess?.getCompletedAppointments()
            loadingDialog.cancel()
            completedAppointmentsCoroutineScope.cancel()
            if (appointments != null && appointments.size != 0) {
                var completedAppointmentsAdapter = context?.let {
                    CompletedAppointmentsAdapter(
                        it,
                        this@StudentCompletedAppointmentsFragment,
                        sharedViewModel,
                        appointments
                    )
                }
                binding.completedAppointmentsRecyclerViewInStudentDashboard.adapter =
                    completedAppointmentsAdapter
                binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility =
                    View.VISIBLE
//                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
                binding.noCompletedTVInStudentDashboardFragment.visibility = View.GONE
//                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
            } else {
                binding.noCompletedTVInStudentDashboardFragment.visibility = View.VISIBLE
//                    binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility = View.GONE
                binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility =
                    View.GONE
//                    binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
            }

        }

    }
}