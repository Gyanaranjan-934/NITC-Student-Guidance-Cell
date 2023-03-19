package com.nitc.projectsgc.booking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import com.nitc.projectsgc.ConsultationType
import com.nitc.projectsgc.Mentors
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.databinding.FragmentBookingBinding
import java.util.*

class BookingFragment : Fragment() {
    lateinit var binding : FragmentBookingBinding
    lateinit var mentorType : String
    var mentorNameSelected = "NA"
    var selectedDate = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val database : FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference : DatabaseReference = database.reference.child("types")
        var mentors = arrayListOf<Mentors>()

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
                var mentorNamesLive =
                    context?.let { it1 -> MentorsAccess(it1).getMentorNames(mentorTypeSelected) }
                if (mentorNamesLive != null) {
                    mentorNamesLive.observe(viewLifecycleOwner) { mentorNames ->
                        if (mentorNames != null) {
                            val mentorNameBuilder = AlertDialog.Builder(context)
                            mentorNameBuilder.setTitle("Choose Mentor Name")
                            mentorNameBuilder.setSingleChoiceItems(
                                mentorNames.map { it }.toTypedArray(),
                                0
                            ) { dialog, selectedIndex ->
                                mentorNameSelected = mentorNames[selectedIndex].toString()
                                binding.mentorNameButtonInBookingFragment.text = mentorTypeSelected
                                dialog.dismiss()
                            }
                            mentorNameBuilder.setPositiveButton("Go") { dialog, which ->
                                mentorNameSelected = mentorNames[0].toString()
                                binding.mentorNameButtonInBookingFragment.text = mentorTypeSelected
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



        return binding.root
    }

}