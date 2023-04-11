package com.nitc.projectsgc.alerts.events.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import java.util.ArrayList

class AllEventsFragment:Fragment() {

    private val sharedViewModel:SharedViewModel by activityViewModels()
    lateinit var binding:FragmentAllEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllEventsBinding.inflate(inflater,container,false)



        val loadingDialog = Dialog(requireContext())
        binding.recyclerViewInAllEventsFragment.layoutManager = LinearLayoutManager(context)
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Log.d("userType","in all events, usertype = "+sharedViewModel.userType)
        var coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            loadingDialog.show()
            getEvents()
            coroutineScope.cancel()
            loadingDialog.cancel()
        }


        if(sharedViewModel.userType == "Mentor"){
            binding.addEventButtonInAllEventsFragment.visibility = View.VISIBLE
            Log.d("viewVisible","view is visible")
        }else binding.addEventButtonInAllEventsFragment.visibility = View.GONE


        binding.addEventButtonInAllEventsFragment.setOnClickListener {
            findNavController().navigate(R.id.addEventFragment)
        }

        var selectedTypeOfEvent = "All"
        var selectedIndexGlobal = 0
        binding.selectTypeButtonInAllEventsFragment.setOnClickListener {
            val newTypes = resources.getStringArray(com.nitc.projectsgc.R.array.event_types)
            val availableTypesOfEvents: ArrayList<String> = ArrayList(newTypes.filterNotNull())
            availableTypesOfEvents.add(0,"All")
            val eventTypeDialog = AlertDialog.Builder(context)
            eventTypeDialog.setTitle("Select the type of Event")
            eventTypeDialog.setSingleChoiceItems(
                availableTypesOfEvents.toTypedArray(),selectedIndexGlobal
            ) { dialog,selectedIndex->
                selectedTypeOfEvent = availableTypesOfEvents[selectedIndex]
                selectedIndexGlobal = selectedIndex
                binding.selectTypeButtonInAllEventsFragment.text = selectedTypeOfEvent
                availableTypesOfEvents.clear()
                val loadingDialog = Dialog(requireContext())
                loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
                loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                var typeEventCoroutineScope = CoroutineScope(Dispatchers.Main)
                typeEventCoroutineScope.launch {
                    loadingDialog.create()
                    loadingDialog.show()
                    if(selectedTypeOfEvent != "All"){
                        getSpecificEvents(selectedTypeOfEvent)
                    }else{
                        getEvents()
                    }
                    loadingDialog.cancel()
                    typeEventCoroutineScope.cancel()
                }
                dialog.dismiss()
            }
            eventTypeDialog.setPositiveButton("Ok"){dialog,which->
                selectedTypeOfEvent = availableTypesOfEvents[selectedIndexGlobal]
                binding.selectTypeButtonInAllEventsFragment.text = selectedTypeOfEvent
                availableTypesOfEvents.clear()
                loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
                loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                var typeEventCoroutineScope = CoroutineScope(Dispatchers.Main)
                typeEventCoroutineScope.launch {
                    loadingDialog.create()
                    loadingDialog.show()
                    getEvents()
                    loadingDialog.cancel()
                    typeEventCoroutineScope.cancel()
                }
                dialog.dismiss()
            }
            eventTypeDialog.create().show()
        }
//        binding.allEventsSwipeLayout.setOnRefreshListener {
//            var swipeCoroutineScope = CoroutineScope(Dispatchers.Main)
//            swipeCoroutineScope.launch {
//                loadingDialog.show()
//                if(selectedTypeOfEvent == "NA" || selectedTypeOfEvent == "All"){
//                    getEvents()
//                }else{
//                    getSpecificEvents(selectedTypeOfEvent)
//                }
//                swipeCoroutineScope.cancel()
//                loadingDialog.cancel()
//                binding.allEventsSwipeLayout.isRefreshing = false
//            }
//        }
        return binding.root
    }
    private suspend fun getSpecificEvents(eventType:String) {

        Log.d("userType",sharedViewModel.userType.toString())

            val events = EventsAccess(
                requireContext(),
                sharedViewModel,
                this@AllEventsFragment
            ).getTypeEvents(sharedViewModel.userType == "Student", eventType)

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

    }

    private suspend fun getEvents() {
        Log.d("userType",sharedViewModel.userType.toString())
        val events = EventsAccess(requireContext(),sharedViewModel,this@AllEventsFragment).getEvents(sharedViewModel.userType == "Student")
        if(events == null || events.isEmpty()){
            binding.noEventsTVInAllEventsFragment.visibility = View.VISIBLE
            binding.recyclerViewInAllEventsFragment.visibility = View.GONE
            return
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
            return
        }
    }

}