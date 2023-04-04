package com.nitc.projectsgc.alerts

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.events.fragments.AllEventsFragment
import com.nitc.projectsgc.alerts.news.fragments.AllNewsFragment

class AllAlertsAdapter(fragmentManager: FragmentManager, lifeCycle: Lifecycle):
    FragmentStateAdapter(fragmentManager,lifeCycle)  {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> AllEventsFragment()
            1-> AllNewsFragment()
            else->throw IllegalArgumentException("Invalid position $position")
        }
    }
}