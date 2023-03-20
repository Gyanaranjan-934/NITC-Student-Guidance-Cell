package com.nitc.projectsgc

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.nitc.projectsgc.Login.LoginFragment
import com.nitc.projectsgc.booking.BookingFragment
import com.nitc.projectsgc.student.StudentDashboardFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            if (pSuccessLive.value == true) {
                when (sharedViewModel.userType) {
                    "Student" -> {
                        var ft = parentFragmentManager.beginTransaction()
                        ft.replace(R.id.navHostFragment,StudentDashboardFragment())
                        ft.addToBackStack(null)
                        ft.commit()
                    }
                    "Mentor" -> {
                        var ft = parentFragmentManager.beginTransaction()
                        ft.replace(R.id.navHostFragment,BookingFragment())
                        ft.addToBackStack(null)
                        ft.commit()
                    }
                }
            } else {
                Log.d("profile","false")
                var ft = parentFragmentManager.beginTransaction()
                ft.replace(R.id.navHostFragment, LoginFragment())
                ft.addToBackStack(null)
                ft.commit()
            }
        }


        Log.d("profile","backIn Profile")

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


}