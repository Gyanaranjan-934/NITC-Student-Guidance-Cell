package com.nitc.projectsgc.booking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nitc.projectsgc.R
import com.nitc.projectsgc.databinding.FragmentBookingBinding
import com.nitc.projectsgc.databinding.FragmentRegisterBinding

class BookingFragment : Fragment() {
    lateinit var binding : FragmentBookingBinding
    lateinit var mentorType : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookingBinding.inflate(inflater,container,false)
        binding.mentorTypeDropdownInBookingFragment.setOnClickListener{

        }
        return binding.root
    }

}