package com.nitc.projectsgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nitc.projectsgc.databinding.FragmentStudentDashBoardBinding

class StudentDashBoardFragment : Fragment() {
    lateinit var studentDashBoardBinding: FragmentStudentDashBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        studentDashBoardBinding =  FragmentStudentDashBoardBinding.inflate(inflater, container, false)




        return studentDashBoardBinding.root

    }


}