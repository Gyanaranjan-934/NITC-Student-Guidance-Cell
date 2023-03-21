package com.nitc.projectsgc.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentPastRecordBinding
import com.nitc.projectsgc.mentors.access.MentorAppointmentsAccess
import com.nitc.projectsgc.mentors.adapters.MentorAppointmentsAdapter
import com.nitc.projectsgc.mentors.adapters.PastRecordAdapter
import com.nitc.projectsgc.student.adapters.BookedAppointmentsAdapter

class PastRecordFragment:Fragment() {

    private val sharedViewModel:SharedViewModel by activityViewModels()
    lateinit var binding:FragmentPastRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPastRecordBinding.inflate(inflater,container,false)

        var pastRecordLive = context?.let { MentorAppointmentsAccess(it, sharedViewModel = sharedViewModel).getStudentRecord(sharedViewModel.pastRecordStudentID) }
        binding.pastRecordRecyclerViewInPastRecordFragment.layoutManager = LinearLayoutManager(context)
        if(pastRecordLive != null){
            pastRecordLive.observe(viewLifecycleOwner){pastRecord->
                if(pastRecord != null){
                    binding.pastRecordRecyclerViewInPastRecordFragment.adapter =
                        context?.let { PastRecordAdapter(it,this,pastRecord, sharedViewModel = sharedViewModel) }
                }
            }
        }

        return binding.root
    }

}