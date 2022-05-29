package com.example.foodapp.activity.admin.product

import android.R.attr
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.example.foodapp.databinding.ActivityAddProductBinding
import com.example.foodapp.helper.RandomStringHelper

import com.example.foodapp.model.HomeHorModel
import com.example.foodapp.model.ProductModel
import com.example.foodapp.ui.productmanagement.ProductManagementFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.data





class AddProductActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityAddProductBinding

    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private lateinit var storageReference: StorageReference
    private lateinit var imgUri: Uri
    private var imgUrl:String ?= ""
    private var check:Boolean = false
    private var actionSuccess:Boolean = false
    private lateinit var type: String
    private val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)
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
        binding.addProductBtn.setOnClickListener {
            if(binding.proName.editText!!.text.trim().toString() == ""){
                binding.proName.editText!!.error = "Required"
            }else if(binding.proPrice.editText!!.text.trim().toString() == ""){
                binding.proPrice.editText!!.error = "Required"
            }else if(binding.proTiming.editText!!.text.trim().toString() == ""){
                binding.proTiming.editText!!.error = "Required"
            }else if(binding.proQty.editText!!.text.trim().toString() == ""){
                binding.proQty.editText!!.error = "Required"
            }else if(binding.proDes.editText!!.text.trim().toString() == ""){
                binding.proDes.editText!!.error = "Required"
            }else if(imgUrl == ""){
                Toast.makeText(this,"Please Select Image !!!",Toast.LENGTH_SHORT).show()
            }else{
                val id = RandomStringHelper().getRandomString(20)
                db.collection("AllProducts")
                    .document(id)
                    .set(ProductModel(id,
                        binding.proName.editText!!.text.trim().toString(),
                        imgUrl,binding.proTiming.editText!!.text.trim().toString(),
                        "5.0",binding.proPrice.editText!!.text.trim().toString(),
                        type,"",binding.proDes.editText!!.text.trim().toString(),
                        "0",binding.proQty.editText!!.text.trim().toString(),now
                        ))
                    .addOnSuccessListener {
                        actionSuccess = true
                        Toast.makeText(this,"Add Product Success !!!",Toast.LENGTH_SHORT).show()
                    }
            }

        }



        //

        db.collection("HomeCategory")
            .get()
            .addOnSuccessListener { result ->
                val data = arrayListOf<String>()
                for (document in result) {
                    val item = document.toObject(HomeHorModel::class.java)
                    data.add(item.name.toString())
                }
                val arrayAdapter = ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line,
                    data)
                binding.proType.adapter = arrayAdapter
                binding.proType.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long,
                        ) {
                            type = data.get(p2).toString()
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun uploadImage() {
//        progressDialog.setMessage("Uploading ....")
//        progressDialog.show()
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("image/${fileName}")
        val uploadTask = storageReference.putFile(imgUri).addOnSuccessListener { it->
            storageReference.downloadUrl.addOnSuccessListener { it->
                imgUrl = it.toString()
                Log.d("TAG", it.toString())
            }
            progressDialog.dismiss()
            binding.displayImgSelect.setImageURI(null)
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
