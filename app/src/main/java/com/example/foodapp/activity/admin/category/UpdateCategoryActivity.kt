package com.example.foodapp.activity.admin.category

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
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.databinding.ActivityUpdateCategoryBinding
import com.example.foodapp.databinding.ActivityUpdateProductBinding
import com.example.foodapp.model.HomeHorModel
import com.example.foodapp.model.ProductModel
import com.example.foodapp.ui.productmanagement.ProductManagementFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class UpdateCategoryActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityUpdateCategoryBinding

    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private lateinit var storageReference: StorageReference
    private lateinit var categoryModel: HomeHorModel
    private lateinit var imgUri: Uri
    private var imgUrl: String? = ""
    private var check: Boolean = false
    private var checkChangeImg: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateCategoryBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)
        val categoryId = intent.getStringExtra("categoryId").toString()
        //Display Category Detail Information
        var item: HomeHorModel? = null
        db.collection("HomeCategory")
            .document(categoryId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    item = document.toObject<HomeHorModel>()
                    categoryModel = item!!
                    binding.categoryName.editText!!.setText(item!!.name.toString())
                    binding.categoryDes.editText!!.setText(item!!.description)
                    Glide.with(this).load(item!!.image).into(binding.displayImgSelect)
                } else {
                    Log.d("TAG", "No such document")
                }
            }
        //Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //Select img and upload img
        binding.selectImgBtn.setOnClickListener {
            pickImageGallery()
        }
        binding.uploadImgBtn.setOnClickListener {
            if (check) {
                progressDialog.setTitle("UpLoading.....")
                progressDialog.show()
                uploadImage()
            } else {
                Toast.makeText(this, "Please Select Image !!!", Toast.LENGTH_SHORT).show()
            }
        }
        //Save btn
        binding.updateCategoryBtn.setOnClickListener {
            if (binding.categoryName.editText!!.text.trim().toString() == "") {
                binding.categoryName.editText!!.error = "Required"
            } else if (binding.categoryDes.editText!!.text.trim().toString() == "") {
                binding.categoryDes.editText!!.error = "Required"
            } else if (checkChangeImg == false) {
                db.collection("HomeCategory")
                    .document(categoryId)
                    .set(HomeHorModel(categoryId,
                        binding.categoryName.editText!!.text.trim().toString(),
                        categoryModel.image,
                        binding.categoryDes.editText!!.text.trim().toString()
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Add Category Success !!!", Toast.LENGTH_SHORT).show()
                    }
            } else if (imgUrl == "") {
                Toast.makeText(this,
                    "Image has not been uploaded, please try again !!!",
                    Toast.LENGTH_SHORT).show()
            } else {
                db.collection("HomeCategory")
                    .document(categoryId)
                    .set(HomeHorModel(categoryId,
                        binding.categoryName.editText!!.text.trim().toString(),
                        imgUrl,
                        binding.categoryDes.editText!!.text.trim().toString()
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Add Category Success !!!", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

    private fun uploadImage() {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("image/${fileName}")
        val uploadTask = storageReference.putFile(imgUri).addOnSuccessListener { it ->
            storageReference.downloadUrl.addOnSuccessListener { it ->
                imgUrl = it.toString()
                Log.d("TAG", it.toString())
            }
            binding.displayImgSelect.setImageURI(null)
            progressDialog.dismiss()
            Toast.makeText(this, "Upload Image Successfully!!!", Toast.LENGTH_SHORT).show()
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
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imgUri = data.data!!
            binding.displayImgSelect.setImageURI(imgUri)
            check = true
            checkChangeImg = true
        }
    }

    companion object {
        val IMAGE_REQUEST_CODE = 100
    }
}