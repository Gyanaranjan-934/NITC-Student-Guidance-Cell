package com.nitc.projectsgc.mentors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
        binding = FragmentMentorDashboardBinding.inflate(inflater,container,false)
        binding.appointmentRecyclerViewInMentorDashboard.layoutManager = LinearLayoutManager(context)

        getAppointments()

        return binding.root
    }

    private fun getAppointments() {
        var appointmentsLive = context?.let { MentorAppointmentsAccess(it,sharedViewModel,this).getAppointments(SimpleDateFormat("dd-MM-yyyy").format(
            Date()
        )) }
        if (appointmentsLive != null) {
            appointmentsLive.observe(viewLifecycleOwner){appointments->
                binding.appointmentRecyclerViewInMentorDashboard.adapter =
                    context?.let { MentorAppointmentsAdapter(it,appointments) }
            }
        }
    }
}