package com.example.foodapp.activity

import android.app.ProgressDialog
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
import com.example.foodapp.MainActivity
import com.example.foodapp.WelcomeActivity
import com.example.foodapp.databinding.ActivityRegistrationBinding
import com.example.foodapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RegistrationActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityRegistrationBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //ProgressDiaLog
    private lateinit var progressDiaLog: ProgressDialog
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseDatabase
    private lateinit var database: FirebaseFirestore
    private var email=""
    private var password=""
    private var confirmpassword=""
    private var name=""
    private val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        //Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //configure actionbar
        actionBar =supportActionBar!!
        actionBar.hide()

        //configure progress dialog
        progressDiaLog = ProgressDialog(this)
        progressDiaLog.setTitle("Please Wait!!")
        progressDiaLog.setMessage("Creating account In..")
        progressDiaLog.setCanceledOnTouchOutside(false)
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        //init firebase datavase
        database = FirebaseFirestore.getInstance()
        binding.registerBtn.setOnClickListener{
            validateData()
        }
    }

    private fun validateData() {
        email = binding.emailET.editText?.text.toString().trim()
        password = binding.passwordET.editText?.text.toString().trim()
        name = binding.nameET.editText?.text.toString().trim()
        confirmpassword = binding.confirmpassET.editText?.text.toString().trim()
        if(TextUtils.isEmpty(name)){
            binding.nameET.error = "Please enter Full name"
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailET.error = "Invalid email format"
        }else if(TextUtils.isEmpty(password)){
            binding.passwordET.error = "Please enter password"
        }else if(password.length < 6){
            binding.passwordET.error = "Password must at least 6 characters long"
        }else{
            firebaseRegister()
        }
    }

    private fun firebaseRegister() {
        progressDiaLog.show()
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                progressDiaLog.dismiss()
                val firebaseUser = firebaseAuth.currentUser
                val uid = firebaseUser?.uid
                val email = firebaseUser!!.email
                val img = firebaseUser.photoUrl
                val phone = firebaseUser.phoneNumber
                val userModel = User(uid,name, email,false,now, img.toString(),"",phone)
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                firebaseUser.updateProfile(profileUpdates)
                    .addOnFailureListener {e->
                        Toast.makeText(this,"Update failed due to ${e.message}",Toast.LENGTH_SHORT).show()
                    }
                if (uid != null) {
                    database.collection("Users")
                        .document(uid)
                        .set(userModel)
                }
                Toast.makeText(this,"Account create with email $email",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener {e->
                progressDiaLog.dismiss()
                Toast.makeText(this,"Register failed due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    fun login(view: android.view.View) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }
    //onclick Home button on actionbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            // app icon in action bar clicked; go home
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

}