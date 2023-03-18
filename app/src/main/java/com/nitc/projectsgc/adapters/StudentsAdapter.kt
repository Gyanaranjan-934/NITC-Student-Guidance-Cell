package com.nitc.projectsgc.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.R
import com.nitc.projectsgc.Students

class StudentsAdapter(
    var context: Context,
    var isAdmin:Boolean,
    var students:Array<Students>
): RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder>() {
    class StudentsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var studentNameText = itemView.findViewById<TextView>(R.id.nameInStudentCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.student_card,parent,false)
        return StudentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: StudentsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}