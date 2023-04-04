package com.nitc.projectsgc.mentors

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.Login.access.LoginAccess
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentMentorDashboardBinding
import com.nitc.projectsgc.mentors.access.MentorAppointmentsAccess
import com.nitc.projectsgc.mentors.adapters.MentorAppointmentsAdapter
import java.text.SimpleDateFormat
import java.util.*


class MentorDashboardFragment : Fragment() {
    private val sharedViewModel : SharedViewModel  by activityViewModels()
    lateinit var binding: FragmentMentorDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var selectedDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
        binding = FragmentMentorDashboardBinding.inflate(inflater,container,false)
        binding.selectDateInMentorDashboardFragment.text = selectedDate
        getAppointments(selectedDate)
        binding.selectDateInMentorDashboardFragment.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    var monthToSet = month + 1
                    if(monthToSet < 10) selectedDate = "$day-0${monthToSet}-$year"
                    else selectedDate = "$day-${monthToSet}-$year"
                    binding.selectDateInMentorDashboardFragment.setText(selectedDate)
                    getAppointments(selectedDate)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            }
            if (datePickerDialog != null) {
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            }
            datePickerDialog?.show()
        }

        binding.logoutButtonInMentorDashboardFragment.setOnClickListener {
            var logoutSuccess = context?.let { it1 -> LoginAccess(it1,this,sharedViewModel).logout() }
            if(logoutSuccess == true){
                findNavController().navigate(R.id.loginFragment)
            }else{
                Toast.makeText(context,"Some error occurred. Try again", Toast.LENGTH_SHORT).show()
            }
//            }

        }
        binding.notificationsButtonInMentorDashboardFragment.setOnClickListener {
            findNavController().navigate(R.id.allEventsFragment)
        }

        var backCallBack = object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backCallBack)

        return binding.root
    }

    private fun getAppointments(selectedDate:String) {
        var appointmentsLive = context?.let { MentorAppointmentsAccess(it,sharedViewModel).getAppointments(selectedDate) }
        if (appointmentsLive != null) {
            appointmentsLive.observe(viewLifecycleOwner){appointments->
                if(appointments != null) {
                    binding.appointmentRecyclerViewInMentorDashboard.layoutManager = LinearLayoutManager(context)
                    binding.appointmentRecyclerViewInMentorDashboard.adapter =
                        context?.let { MentorAppointmentsAdapter(it, appointments,this,sharedViewModel) }
                }
            }
        }
    }
}