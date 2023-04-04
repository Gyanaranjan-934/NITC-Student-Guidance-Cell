package com.nitc.projectsgc.alerts.news.fragments

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
import androidx.navigation.fragment.findNavController
import com.nitc.projectsgc.News
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.news.access.NewsAccess
import com.nitc.projectsgc.databinding.FragmentAddNewsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddNewsFragment: Fragment() {

    lateinit var binding:FragmentAddNewsBinding
    private val sharedViewModel:SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNewsBinding.inflate(inflater,container,false)

        binding.addNewsButtonInAddNewsFragment.setOnClickListener {
            var newsText = binding.newsInputInAddNewsFragment.text.toString().trim()
            if(newsText.isEmpty()){
                Toast.makeText(context,"Enter some news",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var coroutineScope = CoroutineScope(Dispatchers.Main)
            val loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
            loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.create()
            coroutineScope.launch {

                loadingDialog.show()
                var addedNews = NewsAccess(requireContext(),sharedViewModel,this@AddNewsFragment).addNews(
                    News(
                        news = newsText,
                        mentorID = sharedViewModel.currentMentor.userName,
                        publishDate = SimpleDateFormat("dd-MM-yyyy").format(Date()),
                        mentorName = sharedViewModel.currentMentor.name.toString()
                    )
                )
                if (addedNews) {
                    Toast.makeText(context,"Added",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.allAlertsFragment)
                } else {

                }
            }
        }


        return binding.root
    }

}