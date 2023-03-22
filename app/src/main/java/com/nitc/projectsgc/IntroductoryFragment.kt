package com.nitc.projectsgc

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.Login.LoginFragment
import com.nitc.projectsgc.booking.BookingFragment
import com.nitc.projectsgc.student.StudentDashboardFragment
import kotlinx.coroutines.*
import java.util.concurrent.Executors


class IntroductoryFragment : Fragment() {

    private val sharedViewModel:SharedViewModel by activityViewModels()
    private val SPLASH_SCREEN:Long = 3000
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        var pSuccessLive = MutableLiveData<Boolean?>(false)
        var userTypeLive = MutableLiveData<String>("NA")
        coroutineScope.launch {
            val pSuccess = context?.let {
                ProfileAccess(
                    it,
                    sharedViewModel,
                    this@IntroductoryFragment
                ).getProfile()
            }
            pSuccessLive.value = pSuccess // Use setValue() instead of postValue()

            // Move the code that needs to execute after getProfile() inside the coroutine block
            Log.d("profile","backIn Profile")
            if(pSuccessLive != null) {
                if (pSuccessLive.value == true) {
                    val studentDashboard = StudentDashboardFragment()
                    when (sharedViewModel.userType) {
                        "Student" -> {
                            coroutineScope.cancel()
//                        var ft = parentFragmentManager.beginTransaction()
//                        ft.replace(R.id.navHostFragment,studentDashboard)
////                        ft.addToBackStack(null)
//                        ft.commitNow()
                            findNavController().navigate(R.id.studentDashBoardFragment)
                        }
                        "Mentor" -> {
                            coroutineScope.cancel()
//                        var ft = parentFragmentManager.beginTransaction()
//                        ft.add(R.id.navHostFragment,BookingFragment())
//                        ft.addToBackStack(null)
//                        ft.commit()
                            findNavController().navigate(R.id.mentorDashboardFragment)
                        }
                    }
                } else {
                    coroutineScope.cancel()
                    Log.d("profile", "false")
//                var ft = parentFragmentManager.beginTransaction()
//                ft.add(R.id.navHostFragment, LoginFragment())
//                ft.addToBackStack(null)
//                ft.commit()
                    findNavController().navigate(R.id.loginFragment)
                }
            }else {
                coroutineScope.cancel()
                Log.d("profile", "false")
//                var ft = parentFragmentManager.beginTransaction()
//                ft.add(R.id.navHostFragment, LoginFragment())
//                ft.addToBackStack(null)
//                ft.commit()
                findNavController().navigate(R.id.loginFragment)
            }
        }


        Log.d("profile","backIn Profile")
//        userTypeLive.observe(viewLifecycleOwner){userType->
//            if(userType == "Student"){
//                findNavController().navigate()
//            }else if(userType == "Mentor"){
//                findNavController().navigate(R.id.mentorUpdateFragment)
//            }else{
//                findNavController().navigate(R.id.loginFragment)
//            }
//        }

        val backgroundExecutor = Executors.newSingleThreadScheduledExecutor()

//        coroutineScope.launch{
//            delay(SPLASH_SCREEN)
//
////            withContext(Dispatchers.IO){
////
////            }
//        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }


}