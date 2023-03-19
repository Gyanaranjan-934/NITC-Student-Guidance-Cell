package com.nitc.projectsgc.booking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.nitc.projectsgc.Appointment
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.booking.access.BookingAccess
import com.nitc.projectsgc.databinding.FragmentBookingBinding
import java.util.*
import com.nitc.projectsgc.R

class BookingFragment : Fragment() {
    lateinit var binding : FragmentBookingBinding
    lateinit var mentorType : String
    var mentorNameSelected = "NA"
    lateinit var mentorSelected:Mentor
    private val sharedViewModel:SharedViewModel by activityViewModels()
    var selectedDate = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        var reference : DatabaseReference = database.reference.child("types")

        var mentorTypeSelected = "NA"
        binding = FragmentBookingBinding.inflate(inflater,container,false)
        binding.mentorTypeButtonInBookingFragment.setOnClickListener{
            var mentorTypesLive = context?.let { it1 -> MentorsAccess(it1).getMentorTypes() }
            if (mentorTypesLive != null) {
                mentorTypesLive.observe(viewLifecycleOwner) { mentorTypes ->
                    if(mentorTypes != null) {
                        val mentorTypeBuilder = AlertDialog.Builder(context)
                        mentorTypeBuilder.setTitle("Choose Mentor Type")
                        mentorTypeBuilder.setSingleChoiceItems(
                            mentorTypes.map { it }.toTypedArray(),
                            0
                        ) { dialog, selectedIndex ->
                            mentorTypeSelected = mentorTypes[selectedIndex].toString()
                            dialog.dismiss()
                        }
                        mentorTypeBuilder.setPositiveButton("Go") { dialog, which ->
                            mentorTypeSelected = mentorTypes[0].toString()
                            dialog.dismiss()
                        }
                        mentorTypeBuilder.create().show()
                    }
                }
            }
        }
        binding.mentorNameButtonInBookingFragment.setOnClickListener{
            if(mentorTypeSelected != "NA") {
                var mentorsLive =
                    context?.let { it1 -> MentorsAccess(it1).getMentorNames(mentorTypeSelected) }
                if (mentorsLive != null) {
                    mentorsLive.observe(viewLifecycleOwner) { mentors ->
                        if (mentors != null) {
                            val mentorNameBuilder = AlertDialog.Builder(context)
                            mentorNameBuilder.setTitle("Choose Mentor Name")
                            mentorNameBuilder.setSingleChoiceItems(
                                mentors.map { it.name }.toTypedArray(),
                                0
                            ) { dialog, selectedIndex ->
                                mentorSelected = mentors[selectedIndex]
                                binding.mentorNameButtonInBookingFragment.text = mentorSelected.name
                                mentorNameSelected = mentorSelected.name
                                dialog.dismiss()
                            }
                            mentorNameBuilder.setPositiveButton("Go") { dialog, which ->
                                mentorSelected = mentors[0]
                                binding.mentorNameButtonInBookingFragment.text = mentorSelected.name
                                mentorNameSelected = mentorSelected.name
                                dialog.dismiss()
                            }
                            mentorNameBuilder.create().show()
                        }
                    }
                }
            }
            else{
                Toast.makeText(context,"Some error occurred",Toast.LENGTH_SHORT).show()
            }

        }
        binding.bookingDateButtonInBookingFragment.setOnClickListener{
            val calendar = Calendar.getInstance()

            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(it1, { _, year, month, day ->
                    selectedDate = "$day-${month + 1}-$year"
                    binding.bookingDateButtonInBookingFragment.text = selectedDate
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            }
            if (datePickerDialog != null) {
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            }
            datePickerDialog?.show()
        }
        var mentorTimeSlots = arrayListOf<String>()
        var totalTimeSlots = arrayListOf<String>("9-10","10-11","11-12","1-2","2-3","3-4","4-5")
        var availableTimeSlots = arrayListOf<String>()
        var selectedTimeSlot = "NA"
        var foundDate = MutableLiveData<Boolean>(false)
        binding.bookingTimeSlotButtonInBookingFragment.setOnClickListener {
            if (selectedDate != "NA") {
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(mentorTypeSelected+"/"+mentorSelected.userName+"appointments"+selectedDate)) {
                                for (timeSlots in snapshot.children) {
                                    mentorTimeSlots.add(timeSlots.key.toString())
                                }
                                for (timeslot in totalTimeSlots) {
                                    if (!mentorTimeSlots.contains(timeslot)) {
                                        availableTimeSlots.add(timeslot)
                                    }
                                }
                            foundDate.postValue(true)

                        }else{
                            availableTimeSlots = totalTimeSlots
                        }
                        var timeSlotDialog = AlertDialog.Builder(context)
                        timeSlotDialog.setTitle("Select the Time")

                        timeSlotDialog.setSingleChoiceItems(
                            availableTimeSlots.toTypedArray(),
                            0
                        ) { dialog, selectedIndex ->
                            selectedTimeSlot = availableTimeSlots[selectedIndex]
                            binding.bookingTimeSlotButtonInBookingFragment.text = selectedTimeSlot
                            dialog.dismiss()
                        }
                        timeSlotDialog.setPositiveButton("Ok") { dialog, which ->
                            selectedTimeSlot = availableTimeSlots[0]
                            binding.bookingTimeSlotButtonInBookingFragment.text = selectedTimeSlot
                            dialog.dismiss()
                        }
                        timeSlotDialog.create().show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            } else {
                Toast.makeText(context, "Select the date first", Toast.LENGTH_SHORT).show()
            }
        }
        binding.confirmBookingInBookingFragment.setOnClickListener {
            var problemDescription = binding.problemDescriptionInputInBookingFragment.text.toString()
            mentorNameSelected = mentorSelected.name.toString()
            if(mentorTypeSelected == "NA"){
                binding.mentorTypeButtonInBookingFragment.error = "Select type"
                binding.mentorTypeButtonInBookingFragment.requestFocus()
                return@setOnClickListener
            }
            if(mentorNameSelected == "NA" || mentorNameSelected.isEmpty()){
                binding.mentorNameButtonInBookingFragment.error = "Select mentor"
                binding.mentorNameButtonInBookingFragment.requestFocus()
                return@setOnClickListener
            }

            if(problemDescription.isEmpty()){
                binding.problemDescriptionInputInBookingFragment.error = "Write problem description first"
                binding.problemDescriptionInputInBookingFragment.requestFocus()
                return@setOnClickListener
            }

            var bookingLive = context?.let { it1 ->
                BookingAccess(it1,sharedViewModel).bookAppointment(Appointment(
                    date = selectedDate,
                    timeSlot = selectedTimeSlot,
                    mentorID = mentorSelected.userName,
                    studentID = sharedViewModel.currentUserID,
                    completed = false,
                    mentorType = mentorTypeSelected,
                    problemDescription = problemDescription
                ))
            }
            bookingLive!!.observe(viewLifecycleOwner){bookingSuccess->
                if(bookingSuccess){
                    Toast.makeText(context,"Booked",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.studentDashBoardFragment)
                }
            }
        }



        return binding.root
    }

}