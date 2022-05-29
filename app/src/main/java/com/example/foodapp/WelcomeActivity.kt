package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import com.example.foodapp.activity.LoginActivity
import com.example.foodapp.activity.RegistrationActivity
import com.example.foodapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityWelcomeBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        actionBar =supportActionBar!!
        actionBar.hide()
    }
    private fun checkUser() {
        var firebaseUser = firebaseAuth.currentUser
        if(firebaseUser!= null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
    fun register(view: android.view.View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    fun login(view: android.view.View) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }
}