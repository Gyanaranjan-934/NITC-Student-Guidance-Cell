package com.nitc.projectsgc.admin

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.admin.adapters.StudentsAdapter
import com.nitc.projectsgc.databinding.FragmentAllStudentsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AllStudentsFragment:Fragment() {

    lateinit var binding:FragmentAllStudentsBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllStudentsBinding.inflate(inflater,container,false)

        binding.studentRecyclerViewInAllStudentsFragment.layoutManager = LinearLayoutManager(context)
        binding.swipeLayoutInAllStudentsFragment.setOnRefreshListener {
            getStudents()
            binding.swipeLayoutInAllStudentsFragment.isRefreshing = false
        }
        binding.addStudentButtonInAdminDashboard.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        getStudents()
        return binding.root
    }
    private fun getStudents() {
        var coroutineScope = CoroutineScope(Dispatchers.Main)
        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(
            requireActivity().layoutInflater.inflate(
                R.layout.loading_dialog,
                null
            )
        )
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        coroutineScope.launch {
            loadingDialog.create()
            loadingDialog.show()
            var students = StudentsAccess(requireContext(), this@AllStudentsFragment).getStudents()
            loadingDialog.cancel()
            if (students == null) {
                binding.noStudentsTVInAllStudentsFragment.visibility = View.GONE
                binding.studentRecyclerViewInAllStudentsFragment.visibility = View.GONE
                Toast.makeText(context, "Some error occurred. Try again later", Toast.LENGTH_SHORT)
                    .show()
            }else if(students.isEmpty()){
                binding.noStudentsTVInAllStudentsFragment.visibility = View.VISIBLE
                binding.studentRecyclerViewInAllStudentsFragment.visibility = View.GONE
            } else {
                var studentsAdapter = context?.let {
                    StudentsAdapter(
                        it,
                        students = students,
                        this@AllStudentsFragment,
                        sharedViewModel
                    )
                }
                binding.studentRecyclerViewInAllStudentsFragment.adapter = studentsAdapter
                binding.studentRecyclerViewInAllStudentsFragment.visibility = View.VISIBLE
                binding.noStudentsTVInAllStudentsFragment.visibility = View.GONE
            }
            coroutineScope.cancel()
        }
    }


}