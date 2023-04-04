package com.nitc.projectsgc.alerts.events.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.events.access.EventsAccess
import com.nitc.projectsgc.alerts.events.adapters.AllEventsAdapter
import com.nitc.projectsgc.databinding.FragmentAllEventsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AllEventsFragment:Fragment() {

    lateinit var binding:FragmentAllEventsBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllEventsBinding.inflate(inflater,container,false)

        binding.recyclerViewInAllEventsFragment.layoutManager = LinearLayoutManager(context)

        if(sharedViewModel.userType == "Mentor"){
            binding.addEventButtonInAllEventsFragment.visibility = View.VISIBLE
        }else binding.addEventButtonInAllEventsFragment.visibility = View.GONE
        var coroutineScope = CoroutineScope(Dispatchers.Main)
        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        coroutineScope.launch {
            loadingDialog.show()
            val events = EventsAccess(requireContext(),sharedViewModel,this@AllEventsFragment).getEvents(sharedViewModel.userType == "Student")
            if(events == null || events.isEmpty()){
                binding.noEventsTVInAllEventsFragment.visibility = View.VISIBLE
                binding.recyclerViewInAllEventsFragment.visibility = View.GONE
            }else{
                binding.noEventsTVInAllEventsFragment.visibility = View.GONE
                binding.recyclerViewInAllEventsFragment.visibility = View.VISIBLE
                binding.recyclerViewInAllEventsFragment.adapter = AllEventsAdapter(
                    requireContext(),
                    this@AllEventsFragment,
                    sharedViewModel,
                    sharedViewModel.userType == "Student",
                    events
                )
            }
            loadingDialog.cancel()
            coroutineScope.cancel()
        }
        binding.addEventButtonInAllEventsFragment.setOnClickListener {
            findNavController().navigate(R.id.addEventFragment)
        }


        return binding.root
    }

}