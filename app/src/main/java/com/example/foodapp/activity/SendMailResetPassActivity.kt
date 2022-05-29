package com.example.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import com.example.foodapp.databinding.ActivitySendMailResetPassBinding

class SendMailResetPassActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivitySendMailResetPassBinding
    //ActionBar
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendMailResetPassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //configure actionbar
        actionBar =supportActionBar!!
        actionBar.hide()
        //Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    fun open_mail_app(view: android.view.View) {
        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.APP_EMAIL")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(Intent.createChooser(intent, ""))
        finish()
    }

    fun loginc(view: android.view.View) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    fun forgot_password(view: android.view.View) {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }
}