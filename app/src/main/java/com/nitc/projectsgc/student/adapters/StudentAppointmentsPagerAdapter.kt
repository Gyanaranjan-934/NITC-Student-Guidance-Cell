package com.nitc.projectsgc.student.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nitc.projectsgc.student.fragments.StudentBookedAppointmentsFragment
import com.nitc.projectsgc.student.fragments.StudentCompletedAppointmentsFragment
import com.nitc.projectsgc.student.fragments.StudentProfileFragment

class StudentAppointmentsPagerAdapter(fragmentManager: FragmentManager, lifeCycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifeCycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> StudentBookedAppointmentsFragment()
            1-> StudentCompletedAppointmentsFragment()
            2-> StudentProfileFragment()
            else->throw IllegalArgumentException("Invalid position $position")
        }
    }

}