package com.nitc.projectsgc.updateprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.R
import com.nitc.projectsgc.databinding.FragmentStudentUpdateBinding

class StudentUpdateFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var updateBinding: FragmentStudentUpdateBinding
    lateinit var studentGender: Spinner
    lateinit var selectedGenderTextView: String
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
    var firebase : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference : DatabaseReference = firebase.reference
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        updateBinding = FragmentStudentUpdateBinding.inflate(inflater,container,false)
        if (currentUserId != null) {
            Log.d("user Id",currentUserId)
        }
        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        studentGender = updateBinding.genderSpinnerInUpdateProfileFragment
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        studentGender.adapter = arrayAdapter
        studentGender.onItemSelectedListener = this

//        updateBinding.nameFieldInUpdateProfileFragment.text =

        return updateBinding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            selectedGenderTextView = parent.getItemAtPosition(position).toString()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}