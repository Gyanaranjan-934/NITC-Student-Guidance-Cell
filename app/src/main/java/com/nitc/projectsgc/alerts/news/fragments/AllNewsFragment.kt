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
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.news.access.NewsAccess
import com.nitc.projectsgc.databinding.FragmentAllNewsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AllNewsFragment:Fragment() {

    lateinit var binding:FragmentAllNewsBinding

    private val sharedViewModel:SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllNewsBinding.inflate(inflater,container,false)

        if(sharedViewModel.userType == "Mentor"){
            binding.addNewsButtonInAllNewsFragment.visibility = View.VISIBLE
        }else binding.addNewsButtonInAllNewsFragment.visibility = View.GONE
        var coroutineScope = CoroutineScope(Dispatchers.Main)
        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        coroutineScope.launch {
            loadingDialog.show()
            val news = NewsAccess(requireContext(),sharedViewModel,this@AllNewsFragment).getNews()
            if(news != null){
                if(news.isEmpty()){
                    Toast.makeText(context,"No news found. Try again",Toast.LENGTH_SHORT).show()
                    coroutineScope.cancel()
                    loadingDialog.cancel()
                }
            }else{
                Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
                coroutineScope.cancel()
                loadingDialog.cancel()
            }


        }

        return binding.root
    }
}