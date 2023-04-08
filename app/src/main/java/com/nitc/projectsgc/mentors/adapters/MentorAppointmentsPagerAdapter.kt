package com.nitc.projectsgc.mentors.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nitc.projectsgc.mentors.MentorAppointmentsFragment
import com.nitc.projectsgc.mentors.MentorProfileFragment

class MentorAppointmentsPagerAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->MentorAppointmentsFragment()
            1->MentorProfileFragment()
            else-> throw IllegalArgumentException("Invalid position $position")
        }
    }
}