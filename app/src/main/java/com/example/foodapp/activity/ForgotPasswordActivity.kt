package com.example.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.widget.doOnTextChanged
import com.example.foodapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityForgotPasswordBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //configure actionbar
        actionBar =supportActionBar!!
        actionBar.hide()
        //Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //
        var check:Boolean = false

        binding.emailET.editText?.doOnTextChanged {text, start, before, count ->
            if(TextUtils.isEmpty(text)){
                binding.emailET.helperText ="Required"
                check=false
            }else if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                binding.emailET.helperText ="Invalid email format"
                check=false
            }else{
                binding.emailET.isHelperTextEnabled = false
                check=true
            }
        }
        firebaseAuth = FirebaseAuth.getInstance()
        binding.sendResetPassRqBtn.setOnClickListener {
            if(check == true){
                email =binding.emailET.editText?.text.toString().trim()
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, SendMailResetPassActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this,"Email does not exist or an error has occurred, please try again later ",Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this,"Please double check the information you entered ",Toast.LENGTH_SHORT).show()
            }
        }
    }
    //onclick Home button on actionbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            // app icon in action bar clicked; go home
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true;
        }
        return super.onOptionsItemSelected(item)
    }
}




