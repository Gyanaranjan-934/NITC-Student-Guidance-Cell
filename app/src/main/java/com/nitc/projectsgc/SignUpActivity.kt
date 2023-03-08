package com.nitc.projectsgc

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.nitc.projectsgc.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var counsellorCard : MaterialCardView
    lateinit var studentCard: MaterialCardView
    //    lateinit var submitButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        counsellorCard = findViewById(R.id.counsellorCard)
        studentCard = findViewById(R.id.studentCard)
//        submitButton = findViewById(R.id.submit_button)
        binding.counsellorCard.setOnClickListener{
            Toast.makeText(this,"You selected the counsellor",Toast.LENGTH_LONG).show()
        }
        binding.studentCard.setOnClickListener{
            val intent = Intent(this,SignUpStudent::class.java)
            startActivity(intent)
        }
    }
}