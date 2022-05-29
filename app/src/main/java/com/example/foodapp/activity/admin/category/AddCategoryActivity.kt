package com.example.foodapp.activity.admin.category

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.foodapp.R
import com.example.foodapp.databinding.ActivityAddCategoryBinding
import com.example.foodapp.databinding.ActivityAddProductBinding
import com.example.foodapp.helper.RandomStringHelper
import com.example.foodapp.model.HomeHorModel
import com.example.foodapp.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class AddCategoryActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityAddCategoryBinding

    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private lateinit var storageReference: StorageReference
    private lateinit var imgUri: Uri
    private var imgUrl:String ?= ""
    private var check:Boolean = false
    private var actionSuccess:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        progressDialog = ProgressDialog(this)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        //Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //Select img and upload img
        binding.selectImgBtn.setOnClickListener {
            pickImageGallery()
        }
        binding.uploadImgBtn.setOnClickListener {
            if(check){
                progressDialog.setMessage("Uploading....")
                progressDialog.show()
                uploadImage()
            }else{
                Toast.makeText(this,"Please Select Image !!!",Toast.LENGTH_SHORT).show()
            }
        }
        //Add btn
        binding.addCategoryBtn.setOnClickListener {
            if(binding.categoryName.editText!!.text.trim().toString() == ""){
                binding.categoryName.editText!!.error = "Required"
            }else if(binding.categoryDes.editText!!.text.trim().toString() == ""){
                binding.categoryDes.editText!!.error = "Required"
            }else if(imgUrl == ""){
                Toast.makeText(this,"Image has not been uploaded, please try again !!!",Toast.LENGTH_SHORT).show()
            }else{
                val id = RandomStringHelper().getRandomString(20)
                db.collection("HomeCategory")
                    .document(id)
                    .set(HomeHorModel(id,
                        binding.categoryName.editText!!.text.trim().toString(),
                        imgUrl,
                        binding.categoryDes.editText!!.text.trim().toString()
                    ))
                    .addOnSuccessListener {
                        actionSuccess = true
                        Toast.makeText(this,"Add Category Success !!!",Toast.LENGTH_SHORT).show()
                    }
            }

        }

        //

    }

    private fun uploadImage() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("image/${fileName}")
        val uploadTask = storageReference.putFile(imgUri).addOnSuccessListener { it->
            storageReference.downloadUrl.addOnSuccessListener { it->
                imgUrl = it.toString()
                Log.d("TAG", it.toString())
            }
            binding.displayImgSelect.setImageURI(null)
            progressDialog.dismiss()
            Toast.makeText(this,"Upload Image Successfully!!!",Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data !=null){
            imgUri = data.data!!
            binding.displayImgSelect.setImageURI(imgUri)
            check = true
        }
    }
    companion object{
        val IMAGE_REQUEST_CODE = 100
    }

}