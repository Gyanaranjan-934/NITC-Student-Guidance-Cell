package com.nitc.projectsgc.alerts.news.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.alerts.news.access.NewsAccess
import com.nitc.projectsgc.alerts.news.adapters.AllNewsAdapter
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
            Log.d("userType","mentor is here")
        }else binding.addNewsButtonInAllNewsFragment.visibility = View.GONE
        var coroutineScope = CoroutineScope(Dispatchers.Main)
        val loadingDialog = Dialog(requireContext())
        loadingDialog.setContentView(requireActivity().layoutInflater.inflate(R.layout.loading_dialog,null))
        loadingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        coroutineScope.launch {
            loadingDialog.show()
            getNews()
            coroutineScope.cancel()
            loadingDialog.cancel()

        }
//        binding.allNewsSwipeLayout.setOnRefreshListener {
//            var swipeCoroutineScope = CoroutineScope(Dispatchers.Main)
//            swipeCoroutineScope.launch {
//                loadingDialog.show()
//                getNews()
//                swipeCoroutineScope.cancel()
//                loadingDialog.cancel()
//                binding.allNewsSwipeLayout.isRefreshing = false
//            }
//        }
        binding.addNewsButtonInAllNewsFragment.setOnClickListener {
            findNavController().navigate(R.id.addNewsFragment)
        }

        return binding.root
    }

    private suspend fun getNews() {
        val news = NewsAccess(requireContext(),sharedViewModel,this@AllNewsFragment).getNews()
        if(news != null){
            if(news.isEmpty()){
                binding.noNewsTVInAllNewsFragment.visibility = View.VISIBLE
                binding.allNewsRecyclerViewInAllNewsFragment.visibility = View.GONE
                return
            }else{
                binding.allNewsRecyclerViewInAllNewsFragment.visibility = View.VISIBLE
                binding.allNewsRecyclerViewInAllNewsFragment.layoutManager = LinearLayoutManager(context)
                binding.allNewsRecyclerViewInAllNewsFragment.adapter = AllNewsAdapter(requireContext(), news = news, sharedViewModel = sharedViewModel, parentFragment = this)
            }
        }else{
            Toast.makeText(context,"Some error occurred. Try again",Toast.LENGTH_SHORT).show()
            return
        }
    }
}