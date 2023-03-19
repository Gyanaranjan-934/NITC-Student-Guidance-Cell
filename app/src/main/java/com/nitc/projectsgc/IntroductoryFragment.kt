package com.nitc.projectsgc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class IntroductoryFragment : Fragment() {

    private val sharedViewModel:SharedViewModel by activityViewModels()
    private val SPLASH_SCREEN:Long = 3000
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)

        val backgroundExecutor = Executors.newSingleThreadScheduledExecutor()

        coroutineScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main) {


                var profileSuccessLive = context?.let {
                    ProfileAccess(
                        it,
                        sharedViewModel,
                        this@IntroductoryFragment
                    ).getProfile()
                }
                if (profileSuccessLive != null) {
                    profileSuccessLive.observe(viewLifecycleOwner) { profileSuccess ->
                        if (profileSuccess) {
                            when (sharedViewModel.userType) {
                                "Student" -> {
                                    findNavController().navigate(R.id.studentDashBoardFragment)
                                }
                                "Mentor" -> {
                                    findNavController().navigate(R.id.bookingFragment)
                                }
                            }
                        } else {
                            findNavController().navigate(R.id.loginFragment)
                        }
                    }
                }
            }
            delay(SPLASH_SCREEN)

        }

        return view
    }


}