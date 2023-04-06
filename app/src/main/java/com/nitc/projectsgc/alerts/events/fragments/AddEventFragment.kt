package com.nitc.projectsgc.alerts.events.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.Event
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.events.access.EventsAccess
import com.nitc.projectsgc.databinding.FragmentAddEventBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment: Fragment() {

    lateinit var binding:FragmentAddEventBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()
    var selectedDate:String = " "
    var selectedTime = " "
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddEventBinding.inflate(inflater,container,false)

        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if(sharedViewModel.isUpdatingEvent){
            binding.dateButtonInAddEventFragment.text = sharedViewModel.updatingOldEvent.eventDate
            binding.venueInputInAddEventFragment.setText(sharedViewModel.updatingOldEvent.venue)
            binding.headingInputInAddEventFragment.setText(sharedViewModel.updatingOldEvent.heading)
            if(sharedViewModel.updatingOldEvent.link != " ") binding.linkInputInAddEventFragment.setText(sharedViewModel.updatingOldEvent.link)
            if(sharedViewModel.updatingOldEvent.eventTime != " ") {
                binding.timeButtonInAddEventFragment.visibility = View.VISIBLE
                binding.timeButtonInAddEventFragment.text = sharedViewModel.updatingOldEvent.eventTime
                selectedTime = sharedViewModel.updatingOldEvent.eventTime.toString()
            }
            binding.addEventButtonInAddEventFragment.text = "Update"
            selectedDate = sharedViewModel.updatingOldEvent.eventDate.toString()
        }
        val calendar = Calendar.getInstance()
        binding.dateButtonInAddEventFragment.setOnClickListener {

            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    var monthToSet = month + 1
                    if(monthToSet < 10) selectedDate = "$day-0${monthToSet}-$year"
                    else selectedDate = "$day-${monthToSet}-$year"
                    binding.dateButtonInAddEventFragment.text = selectedDate
                    binding.timeButtonInAddEventFragment.visibility = View.VISIBLE
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            }
            if (datePickerDialog != null) {
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            }
            datePickerDialog?.show()
        }

        binding.timeButtonInAddEventFragment.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(requireContext(),object:TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    selectedTime = "$hourOfDay : $minute"
                    binding.timeButtonInAddEventFragment.text = selectedTime
                }

            },hour,minute,true).show()
        }

        binding.addEventButtonInAddEventFragment.setOnClickListener {

                val headingInput = binding.headingInputInAddEventFragment.text.toString().trim()
                if (headingInput.isEmpty()) {
                    Toast.makeText(context, "Enter heading of event first", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                val venueInput = binding.venueInputInAddEventFragment.text.toString().trim()
                if (venueInput.isEmpty()) {
                    Toast.makeText(context, "Enter venue for the event", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                var linkInput = binding.linkInputInAddEventFragment.text.toString().trim()
                if (linkInput.isEmpty()) linkInput = " "
                if (sharedViewModel.isUpdatingEvent) {
                    var updatingCoroutineScope = CoroutineScope(Dispatchers.Main)
                    updatingCoroutineScope.launch {
                        loadingDialog.create()
                        loadingDialog.show()
                        val updated =
                            EventsAccess(requireContext(), sharedViewModel, this@AddEventFragment).updateEvent(
                                sharedViewModel.updatingOldEvent.id,
                                Event(
                                    heading = headingInput,
                                    venue = venueInput,
                                    eventTime = selectedTime,
                                    eventDate = selectedDate,
                                    link = linkInput,
                                    mentorName = sharedViewModel.currentMentor.name,
                                    publishDate = sharedViewModel.updatingOldEvent.publishDate
                                )
                            )
                        loadingDialog.cancel()
                        if(updated){
                            updatingCoroutineScope.cancel()
                            Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show()
                            sharedViewModel.isUpdatingEvent = false
                            findNavController().navigate(R.id.allAlertsFragment)
                        }else{
                            Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    var addingCoroutineScope = CoroutineScope(Dispatchers.Main)
                    addingCoroutineScope.launch {
                        loadingDialog.create()
                        loadingDialog.show()
                        val added =
                            EventsAccess(requireContext(), sharedViewModel, this@AddEventFragment).addEvent(
                                Event(
                                    heading = headingInput,
                                    venue = venueInput,
                                    eventTime = selectedTime,
                                    eventDate = selectedDate,
                                    link = linkInput,
                                    mentorName = sharedViewModel.currentMentor.name,
                                    publishDate = SimpleDateFormat("dd-MM-yyyy").format(Date())
                                )
                            )
                        loadingDialog.cancel()
                            if (added) {
                                Toast.makeText(context, "Added event", Toast.LENGTH_SHORT).show()
                                addingCoroutineScope.cancel()
                                findNavController().navigate(R.id.allAlertsFragment)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Some error occurred. Try again later",
                                    Toast.LENGTH_SHORT
                                ).show()
                                addingCoroutineScope.cancel()
                            }

                    }
            }
        }

        return binding.root
    }
}