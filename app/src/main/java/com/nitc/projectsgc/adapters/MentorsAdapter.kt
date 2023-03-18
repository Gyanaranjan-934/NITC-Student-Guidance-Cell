package com.nitc.projectsgc.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.Mentors
import com.nitc.projectsgc.R
import org.w3c.dom.Text

class MentorsAdapter(
    var context: Context,
    var isAdmin : Boolean,
    var mentors:ArrayList<Mentors>
): RecyclerView.Adapter<MentorsAdapter.MentorsViewHolder>() {
    class MentorsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var nameText = itemView.findViewById<TextView>(R.id.nameInMentorCard)
        var typeText = itemView.findViewById<TextView>(R.id.typeInMentorCard)
        var phoneText = itemView.findViewById<TextView>(R.id.phoneInMentorCard)
        var emailText = itemView.findViewById<TextView>(R.id.emailIdInMentorCard)
        var updateMentorButton = itemView.findViewById<Button>(R.id.updateMentorButtonInMentorCard)
        var deleteButton = itemView.findViewById<Button>(R.id.deleteButtonInMentorCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.mentor_card,parent,false)
        return MentorsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mentors.size
    }

    override fun onBindViewHolder(holder: MentorsViewHolder, position: Int) {
        holder.nameText.text = mentors[position].name.toString()
        holder.phoneText.text = mentors[position].phone.toString()
        holder.typeText.text = mentors[position].type
        holder.emailText.text = mentors[position].email
        holder.deleteButton.setOnClickListener {

        }
    }

}