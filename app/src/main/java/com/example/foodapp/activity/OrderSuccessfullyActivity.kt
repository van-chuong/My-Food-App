package com.example.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodapp.MainActivity
import com.example.foodapp.R
import com.example.foodapp.databinding.ActivityCheckoutBinding
import com.example.foodapp.databinding.ActivityOrderSuccessfullyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class OrderSuccessfullyActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityOrderSuccessfullyBinding
    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessfullyBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        setContentView(binding.root)
        binding.backToHome.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}