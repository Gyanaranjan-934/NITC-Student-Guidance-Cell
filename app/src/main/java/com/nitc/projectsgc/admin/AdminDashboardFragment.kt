package com.nitc.projectsgc.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.R
import com.nitc.projectsgc.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {
    lateinit var adminDashboardBinding : FragmentAdminDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        adminDashboardBinding =  FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return adminDashboardBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adminDashboardBinding.addMentorButtonInAdminDashboard.setOnClickListener{
            findNavController().navigate(R.id.addMentorFragment)
        }
    }


}