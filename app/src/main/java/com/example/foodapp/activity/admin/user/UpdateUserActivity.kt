package com.example.foodapp.activity.admin.user

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.foodapp.R
import com.example.foodapp.databinding.ActivityUpdateOrderBinding
import com.example.foodapp.databinding.ActivityUpdateUserBinding
import com.example.foodapp.model.OrderModel
import com.example.foodapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class UpdateUserActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityUpdateUserBinding
    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        val userId= intent.getStringExtra("userId").toString()
        setContentView(binding.root)
        // Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //
        var item:User ?=null
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                     item = document.toObject<User>()
                    if(item !=null){
                        binding.displayName.editText!!.setText(item!!.name)
                        binding.address.editText!!.setText(item!!.address)
                        binding.phone.editText!!.setText(item!!.phone)
                        binding.email.editText!!.setText(item!!.email)
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }
        //Update Btn
        binding.updateUserBtn.setOnClickListener {
            if (binding.displayName.editText!!.text.trim().toString() == ""){
                binding.displayName.editText!!.error = "Required"
            }else{
                progressDialog.setMessage("Updating....")
                progressDialog.show()
                db.collection("Users")
                    .document(userId)
                    .set(User(userId,binding.displayName.editText!!.text.trim().toString(),
                        binding.email.editText!!.text.toString(),false, item?.created.toString(),
                        item?.image,binding.address.editText!!.text.toString(),binding.phone.editText!!.text.toString()
                    ))
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this,"Update User Successfully!",Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}