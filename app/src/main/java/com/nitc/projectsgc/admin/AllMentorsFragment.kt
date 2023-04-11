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
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.admin.adapters.MentorsAdapter
import com.nitc.projectsgc.databinding.FragmentAllMentorsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AllMentorsFragment:Fragment() {

    lateinit var binding:FragmentAllMentorsBinding

    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllMentorsBinding.inflate(inflater,container,false)

        binding.mentorRecyclerViewInAllMentorsFragment.layoutManager = LinearLayoutManager(context)

//        binding.swipeLayoutInAllMentorsFragment.setOnRefreshListener {
//            getMentors()
//            binding.swipeLayoutInAllMentorsFragment.isRefreshing = false
//        }
        getMentors()

        binding.addMentorButtonInAllMentorsFragment.setOnClickListener{
            findNavController().navigate(R.id.addMentorFragment)
        }
        return binding.root
    }
    private fun getMentors() {
        var coroutineScope = CoroutineScope(Dispatchers.Main)
        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        coroutineScope.launch {
            loadingDialog.create()
            loadingDialog.show()
            var mentors = context?.let { MentorsAccess(it).getMentors() }
            loadingDialog.cancel()
            if (mentors == null) {
                binding.noMentorsTVInAllMentorsFragment.visibility = View.GONE
                binding.mentorRecyclerViewInAllMentorsFragment.visibility = View.GONE
                Toast.makeText(context,"Some error occurred. Try again later", Toast.LENGTH_SHORT).show()
            }
            else if(mentors.isEmpty()){
                binding.noMentorsTVInAllMentorsFragment.visibility = View.VISIBLE
                binding.mentorRecyclerViewInAllMentorsFragment.visibility = View.GONE
            } else {
                var mentorsAdapter = context?.let {
                    MentorsAdapter(it, true, mentors = mentors, this@AllMentorsFragment, sharedViewModel)
                }
                binding.mentorRecyclerViewInAllMentorsFragment.adapter = mentorsAdapter
                binding.mentorRecyclerViewInAllMentorsFragment.visibility = View.VISIBLE
                binding.noMentorsTVInAllMentorsFragment.visibility = View.GONE

            }
            coroutineScope.cancel()
        }
    }

}