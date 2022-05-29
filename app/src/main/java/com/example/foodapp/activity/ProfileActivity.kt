package com.example.foodapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.ActivityProfileBinding
import com.example.foodapp.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import android.R
import android.content.Intent

import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.foodapp.ui.home.HomeFragment


class ProfileActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityProfileBinding
    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        //
        binding.orderCardView.setOnClickListener {
            startActivity(Intent(this,OrderListActivity::class.java))
        }
        //Display Information
        var item:User ?=null
        db.collection("Users")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { it->
                if(it.data.toString() != "[]"){
                    item = it.toObject<User>()!!
                    binding.proAddress.editText!!.setText(item!!.address.toString())
                }
            }
        db.collection("Orders")
            .whereEqualTo("uid",firebaseUser.uid)
            .get()
            .addOnSuccessListener { it->
                binding.orderLabel.setText(it.documents.size.toString())
            }
        binding.profileName.text = firebaseUser.displayName
        binding.proName.editText!!.setText(firebaseUser.displayName)
        binding.proEmail.editText!!.setText(firebaseUser.email)
        if(firebaseUser.photoUrl != null){
            Glide.with(this).load(firebaseUser.photoUrl).into(binding.profileImg)
        }
        //Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //Update Btn
        binding.updateProfileBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Are you sure to update your profile and password? Do you want continue?")
                .setNegativeButton("Yes"){ dialog, which ->
                    val profileUpdates = userProfileChangeRequest {
                        displayName = binding.proName.editText!!.text.toString()
                    }
                    firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                db.collection("Users")
                                    .document(firebaseUser.uid)
                                    .update("name",binding.proName.editText!!.text.toString())
                                    .addOnSuccessListener {
                                        db.collection("Users")
                                            .document(firebaseUser.uid)
                                            .update("address",binding.proAddress.editText!!.text.toString())
                                            .addOnSuccessListener {
                                                Log.d("TAG", "User profile updated.")
                                                recreate()
                                            }
                                    }
                            }
                        }
                    if (binding.proPass.editText!!.text.toString() != "" ){
                        firebaseUser.updatePassword(binding.proPass.editText!!.text.toString())
                            .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("TAG", "Password updated.")
                                recreate()
                            }
                        }
                            .addOnFailureListener {
                                Toast.makeText(this,"You are logging in with an external method, so don't update your password",Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .setPositiveButton("No"){ dialog, which ->
                }
                .show()
        }
    }


}


