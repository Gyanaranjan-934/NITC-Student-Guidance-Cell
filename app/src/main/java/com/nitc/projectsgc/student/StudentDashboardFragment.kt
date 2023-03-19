package com.nitc.projectsgc.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.databinding.FragmentStudentDashBoardBinding
import com.nitc.projectsgc.student.access.BasicStudentAccess

class StudentDashboardFragment: Fragment() {
    private val sharedViewModel:SharedViewModel by activityViewModels()

    lateinit var binding:FragmentStudentDashBoardBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentDashBoardBinding.inflate(inflater,container,false)

        var studentLive = context?.let { BasicStudentAccess(it,sharedViewModel).getStudent() }
        studentLive!!.observe(viewLifecycleOwner){student->
            if(student != null){
                sharedViewModel.currentStudent = student
            }
        }


        return binding.root
    }

}