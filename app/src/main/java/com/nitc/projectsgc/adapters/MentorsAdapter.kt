package com.nitc.projectsgc.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nitc.projectsgc.Mentors
import com.nitc.projectsgc.R
import com.nitc.projectsgc.SharedViewModel
import com.nitc.projectsgc.admin.access.MentorsAccess
import com.nitc.projectsgc.admin.access.StudentsAccess
import com.nitc.projectsgc.updateprofile.MentorUpdateFragment
import org.w3c.dom.Text

class MentorsAdapter(
    var context: Context,
    var isAdmin : Boolean,
    var mentors:ArrayList<Mentors>,
    var parentFragment: Fragment,
    var sharedViewModel: SharedViewModel
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
            var confirmDeleteBuilder = AlertDialog.Builder(context)
            confirmDeleteBuilder.setTitle("Are you sure ?")
                .setMessage("You want to delete this mentor?")
                .setPositiveButton("Yes"){dialog,which->
                    var deletedLive = MentorsAccess(context).deleteMentor(mentors[position].userName.toString(),mentors[position].type.toString())
                    deletedLive.observe(parentFragment.viewLifecycleOwner){deleted->
                        if(deleted){
                            Toast.makeText(context,"Mentor deleted", Toast.LENGTH_SHORT).show()
                            mentors.removeAt(position)
                            notifyItemChanged(position)
                        }
                    }
                    dialog.dismiss()

                }
                .setNegativeButton("No"){dialog,which->
                    dialog.dismiss()
                }
                .create().show()
        }

        holder.updateMentorButton.setOnClickListener {
            sharedViewModel.updateMentor = mentors[position]
            parentFragment.findNavController().navigate(R.id.mentorUpdateFragment)
        }
    }

}