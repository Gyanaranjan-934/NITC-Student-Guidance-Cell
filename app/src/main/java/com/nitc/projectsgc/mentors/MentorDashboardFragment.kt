package com.nitc.projectsgc.mentors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentMentorDashboardBinding


class MentorDashboardFragment : Fragment() {
    private val sharedViewModel : SharedViewModel  by activityViewModels()
    lateinit var mentorDashboardBinding: FragmentMentorDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mentorDashboardBinding = FragmentMentorDashboardBinding.inflate(inflater,container,false)

        var mentorLive = context?.let {  }

        return mentorDashboardBinding.root
    }
}