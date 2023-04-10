package com.nitc.projectsgc.student.fragments

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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitc.projectsgc.Mentor
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.Student
import com.nitc.projectsgc.admin.access.AddMentorAccess
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.databinding.FragmentStudentProfileBinding
import com.nitc.projectsgc.student.access.BasicStudentAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class StudentProfileFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var binding: FragmentStudentProfileBinding
    lateinit var studentLive: MutableLiveData<Student?>
    var oldPassword = "NA"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        if (sharedViewModel.userType == "Student" || sharedViewModel.userType == "Admin") {

            if (sharedViewModel.userType == "Admin") {
                studentLive = StudentsAccess(
                    requireContext(),
                    this
                ).getStudent(sharedViewModel.idForStudentProfile)
            } else {
                studentLive =
                    StudentsAccess(requireContext(), this).getStudent(sharedViewModel.currentUserID)
            }

            if (studentLive != null) {
                binding.headingTVStudentProfileFragment.text = sharedViewModel.currentStudent.name
                binding.nameFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.name)
                binding.emailFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.emailId)
                binding.phoneNumberStudentProfileFragment.setText(sharedViewModel.currentStudent.phoneNumber)
                binding.passwordFieldStudentProfileFragment.setText(sharedViewModel.currentStudent.password)
                binding.emailFieldStudentProfileFragment.isEnabled = false
                oldPassword = sharedViewModel.currentStudent.password

            }
            binding.updateButtonStudentProfileFragment.setOnClickListener {
                val studentName = binding.nameFieldStudentProfileFragment.text.toString()
                var studentPhone = binding.phoneNumberStudentProfileFragment.text.toString()
                val studentPassword = binding.passwordFieldStudentProfileFragment.text.toString()

                if (studentName.isEmpty()) {
                    binding.nameFieldStudentProfileFragment.error = "Name field cannot be empty"
                    binding.nameFieldStudentProfileFragment.requestFocus()
                    return@setOnClickListener
                }

                if (studentPassword.length < 8) {
                    binding.passwordFieldStudentProfileFragment.error =
                        "Password should contain at least 8 characters"
                    binding.passwordFieldStudentProfileFragment.requestFocus()
                    return@setOnClickListener
                }

                studentPhone = studentPhone.trim()
                if (studentPhone.length < 10 || studentPhone.length > 10) {
                    studentPhone.trim()
                    binding.phoneNumberStudentProfileFragment.error =
                        "Phone number should be 10 digits only"
                    binding.phoneNumberStudentProfileFragment.requestFocus()
                    return@setOnClickListener
                } else if (!isDigitsOnly(studentPhone)) {
                    binding.phoneNumberStudentProfileFragment.setText("")
                    Toast.makeText(
                        context,
                        "Oops !! you entered phone number in wrong format",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.phoneNumberStudentProfileFragment.requestFocus()
                    return@setOnClickListener
                }

                var updateCoroutineScope = CoroutineScope(Dispatchers.Main)
                val loadingDialog = Dialog(requireContext())
                loadingDialog.setContentView(
                    requireActivity().layoutInflater.inflate(
                        R.layout.loading_dialog,
                        null
                    )
                )
                loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                updateCoroutineScope.launch {
                    loadingDialog.create()
                    loadingDialog.show()


                    if (studentLive != null) {
//                val updatedMentor = BasicStudentAccess(requireContext()).updateStudent(student,oldPassword)
                        var student = Student(sharedViewModel.currentStudent.rollNo,studentName,sharedViewModel.currentStudent.dateOfBirth,sharedViewModel.currentStudent.emailId,sharedViewModel.currentStudent.gender,studentPassword,studentPhone)
                            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                            val reference: DatabaseReference = database.reference.child("students")
                            val auth: FirebaseAuth = FirebaseAuth.getInstance()
                            val updates = mapOf<String,String>(
                                "name" to student.name,
                                "password" to student.password,
                                "phoneNumber" to student.phoneNumber
                            )
                            reference.child(student.rollNo).updateChildren(updates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (student.password != oldPassword) {
                                            auth.currentUser?.updatePassword(student.password)
                                                ?.addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            context,
                                                            "Password updated successfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Password update failed",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        }
                                        Toast.makeText(
                                            context,
                                            "Profile updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.headingTVStudentProfileFragment.text = studentName
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Update failed please try after some time",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            loadingDialog.cancel()
                            updateCoroutineScope.cancel()

                        }
                }

            }


        } else {
            binding.passwordInputCardStudentProfileFragment.visibility = View.GONE
            binding.updateButtonStudentProfileFragment.visibility = View.GONE
        }
        binding.updateButtonStudentProfileFragment.visibility = View.VISIBLE




        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::studentLive.isInitialized) studentLive.removeObservers(viewLifecycleOwner)
    }

    private fun isDigitsOnly(str: String): Boolean {
        return str.matches(Regex("[0-9]+"))
    }
}