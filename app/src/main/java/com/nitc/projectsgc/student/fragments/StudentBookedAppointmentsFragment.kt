package com.nitc.projectsgc.student.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentBookedAppointmentsBinding
import com.nitc.projectsgc.student.access.AppointmentsAccess
import com.nitc.projectsgc.student.adapters.BookedAppointmentsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

//        binding.swipeLayoutInStudentBookedAppointmentsFragment.setOnRefreshListener {
//            getBookedAppointments()
//        }
        getBookedAppointments()


        return binding.root
    }
    fun getBookedAppointments() {
        var bookedAppointmentsLive = MutableLiveData<ArrayList<Appointment>>()
        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Log.d("today","this called")
        val bookedCoroutineScope = CoroutineScope(Dispatchers.Main)
        bookedCoroutineScope.launch {
            var appointmentAccess = context?.let {
                AppointmentsAccess(
                    it,
                    this@StudentBookedAppointmentsFragment,
                    sharedViewModel
                )
            }
            loadingDialog.create()
            loadingDialog.show()
            bookedAppointmentsLive.postValue(appointmentAccess?.getBookedAppointments())
            loadingDialog.cancel()
                bookedAppointmentsLive.observe(viewLifecycleOwner) { bookedAppointments ->
                    if (bookedAppointments != null && bookedAppointments.size != 0) {
//                        (binding.bookedAppointmentsRecyclerViewStudentDashboard.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                        var bookedAppointmentsAdapter = context?.let {
                            BookedAppointmentsAdapter(
                                it,
                                this@StudentBookedAppointmentsFragment,
                                sharedViewModel,
                                bookedAppointments,
                                false
                            )
                        }
                        binding.bookedAppointmentsRecyclerViewStudentDashboard.isNestedScrollingEnabled = false
                        binding.bookedAppointmentsRecyclerViewStudentDashboard.adapter =
                            bookedAppointmentsAdapter
//                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
                        binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility =
                            View.VISIBLE
                        binding.noBookingsTVInStudentDashboardFragment.visibility = View.GONE
                    }else {
                        binding.noBookingsTVInStudentDashboardFragment.visibility = View.VISIBLE
                        binding.bookedAppointmentsRecyclerViewStudentDashboard.visibility =
                            View.GONE
//                    binding.completedAppointmentsRecyclerViewInStudentDashboard.visibility = View.GONE
//                    binding.noCompletedTVInStudentDashboardFragment.visibility = View.GONE
                    }
                }

            bookedCoroutineScope.cancel()
        }
                }



}