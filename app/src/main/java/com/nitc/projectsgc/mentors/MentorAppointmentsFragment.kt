package com.nitc.projectsgc.mentors

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.nitc.projectsgc.databinding.FragmentMentorAppointmentsBinding
import com.nitc.projectsgc.mentors.access.MentorAppointmentsAccess
import com.nitc.projectsgc.mentors.adapters.MentorAppointmentsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MentorAppointmentsFragment: Fragment() {

    lateinit var binding:FragmentMentorAppointmentsBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMentorAppointmentsBinding.inflate(inflater,container,false)

        var selectedDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        binding.selectDateInMentorAppointmentsFragment.text = selectedDate
        var coroutineScope = CoroutineScope(Dispatchers.Main)
        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.create()
        coroutineScope.launch {
            loadingDialog.show()
            getAppointments(selectedDate)
            loadingDialog.cancel()
            coroutineScope.cancel()
        }
        binding.mentorDashboardSwipeLayout.setOnRefreshListener {
            var swipeCoroutineScope = CoroutineScope(Dispatchers.Main)
            swipeCoroutineScope.launch {
                loadingDialog.show()
                getAppointments(selectedDate)
                loadingDialog.cancel()
                swipeCoroutineScope.cancel()
                binding.mentorDashboardSwipeLayout.isRefreshing = false
            }
        }

        binding.selectDateInMentorAppointmentsFragment.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    var monthToSet = month + 1
                    if(monthToSet < 10) selectedDate = "$day-0${monthToSet}-$year"
                    else selectedDate = "$day-${monthToSet}-$year"
                    binding.selectDateInMentorAppointmentsFragment.setText(selectedDate)
                    var selectDateCoroutineScope = CoroutineScope(Dispatchers.Main)
                    selectDateCoroutineScope.launch {
                        loadingDialog.show()
                        getAppointments(selectedDate)
                        loadingDialog.cancel()
                        selectDateCoroutineScope.cancel()
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            }
//            if (datePickerDialog != null) {
//                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
//            }
            datePickerDialog?.show()
        }


        return binding.root
    }

    private suspend fun getAppointments(selectedDate:String) {
        var appointments = context?.let { MentorAppointmentsAccess(it,sharedViewModel).getAppointments(selectedDate) }
        if(appointments != null) {
            Log.d("appointments",appointments.toString())
            if(appointments.isEmpty()){
                binding.appointmentRecyclerViewInMentorDashboard.visibility = View.GONE
                binding.noAppointmentsTVInMentorAppointmentsFragment.visibility = View.VISIBLE
            }else {
                binding.appointmentRecyclerViewInMentorDashboard.layoutManager =
                    LinearLayoutManager(context)
                binding.appointmentRecyclerViewInMentorDashboard.adapter =
                    context?.let {
                        MentorAppointmentsAdapter(
                            it,
                            appointments,
                            this,
                            sharedViewModel
                        )
                    }
                binding.noAppointmentsTVInMentorAppointmentsFragment.visibility = View.GONE
                binding.appointmentRecyclerViewInMentorDashboard.visibility = View.VISIBLE
            }
            return
        }else{
            Toast.makeText(context,"Some error occurred. Try again", Toast.LENGTH_SHORT).show()
            return
        }
    }

}