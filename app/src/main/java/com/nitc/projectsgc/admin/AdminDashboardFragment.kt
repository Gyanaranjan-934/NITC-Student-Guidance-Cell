package com.nitc.projectsgc.admin

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.adapters.MentorsAdapter
import com.nitc.projectsgc.admin.adapters.StudentsAdapter
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {
    lateinit var binding : FragmentAdminDashboardBinding
    var userType = "Student"
    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentAdminDashboardBinding.inflate(inflater, container, false)


        binding.studentRecyclerViewInAdminDashboardFragment.layoutManager = LinearLayoutManager(context)
        binding.mentorRecyclerViewInAdminDashboardFragment.layoutManager = LinearLayoutManager(context)
        getStudents()
        binding.mentorLoginTypeButtonInAdminDashboardFragment.setOnClickListener {
            binding.mentorLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.mentorLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.WHITE)
            binding.studentLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.studentLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            binding.studentLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.studentLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            userType = "Mentor"
            getMentors()
        }
        binding.studentLoginTypeButtonInAdminDashboardFragment.setOnClickListener {
            binding.mentorLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            binding.studentLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_blue_bg)
            binding.studentLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.WHITE)
            binding.mentorLoginTypeImageInAdminDashboardFragment.setBackgroundResource(R.drawable.login_type_card_transparent_bg)
            binding.mentorLoginTypeButtonInAdminDashboardFragment.setTextColor(Color.BLACK)
            userType = "Student"
            getStudents()
        }


        binding.addStudentButtonInAdminDashboard.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }




        binding.addMentorButtonInAdminDashboard.setOnClickListener{
            findNavController().navigate(R.id.addMentorFragment)
        }

        val backCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Call a method in your Fragment to handle the navigation
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,backCallback)
        return binding.root
    }

    private fun getMentors() {
        binding.mentorLayoutInAdminDashboardFragment.visibility = View.VISIBLE
        binding.studentLayoutInAdminDashboardFragment.visibility = View.GONE
        var mentorsLive = context?.let { MentorsAccess(it).getMentors() }
        if (mentorsLive != null){
            mentorsLive.observe(viewLifecycleOwner){mentors->
                if (mentors==null){
                    binding.noMentorsTVInAdminDashboardFragment.visibility = View.VISIBLE
                    binding.mentorRecyclerViewInAdminDashboardFragment.visibility = View.GONE
                }else{
                    var mentorsAdapter = context?.let {
                        MentorsAdapter(it,true,mentors = mentors,this,sharedViewModel)
                    }
                    binding.mentorRecyclerViewInAdminDashboardFragment.adapter = mentorsAdapter
                    binding.mentorRecyclerViewInAdminDashboardFragment.visibility = View.VISIBLE
                    binding.noMentorsTVInAdminDashboardFragment.visibility = View.GONE
                }
            }
        }else{
            binding.noMentorsTVInAdminDashboardFragment.visibility = View.GONE
            binding.mentorRecyclerViewInAdminDashboardFragment.visibility = View.GONE
            Toast.makeText(context,"Some error occurred. Try again later",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getStudents() {
        binding.mentorLayoutInAdminDashboardFragment.visibility = View.GONE
        binding.studentLayoutInAdminDashboardFragment.visibility = View.VISIBLE
        var studentsLive = context?.let { StudentsAccess(it).getStudents() }
        if(studentsLive != null){
            studentsLive.observe(viewLifecycleOwner){students->
                if(students == null){
                    binding.noStudentsTVInAdminDashboardFragment.visibility = View.VISIBLE
                    binding.studentRecyclerViewInAdminDashboardFragment.visibility = View.GONE
                }else{
                    var studentsAdapter = context?.let {
                        StudentsAdapter(
                            it,
                            true,
                            students = students,
                            this
                        )
                    }
                    binding.studentRecyclerViewInAdminDashboardFragment.adapter = studentsAdapter
                    binding.studentRecyclerViewInAdminDashboardFragment.visibility = View.VISIBLE
                    binding.noStudentsTVInAdminDashboardFragment.visibility = View.GONE
                }
            }
        }else{
            binding.noStudentsTVInAdminDashboardFragment.visibility = View.GONE
            binding.studentRecyclerViewInAdminDashboardFragment.visibility = View.GONE
            Toast.makeText(context,"Some error occurred. Try again later",Toast.LENGTH_SHORT).show()
        }
    }

}