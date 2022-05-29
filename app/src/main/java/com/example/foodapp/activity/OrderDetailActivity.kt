package com.example.foodapp.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.adapter.CheckoutAdater
import com.example.foodapp.adapter.OrdersAdapter
import com.example.foodapp.databinding.ActivityOrderDetailBinding
import com.example.foodapp.databinding.ActivityOrderListBinding
import com.example.foodapp.databinding.ActivityUpdateOrderBinding
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.OrderModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class OrderDetailActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityOrderDetailBinding
    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    private lateinit var db: FirebaseFirestore
    //
    private lateinit var adapter: CheckoutAdater
    private lateinit var recyclerView: RecyclerView
    private lateinit var modelList: ArrayList<MyCartModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        db = FirebaseFirestore.getInstance()
        setContentView(binding.root)
        val orderId = intent.getStringExtra("orderId").toString()
        displayOrder(orderId)
        //Display Orders Detail Product
        recyclerView = binding.productRv
        recyclerView.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        loadItem(orderId)
        //back btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //Cancedled Order Btn
        binding.canceledOrderBtn.setOnClickListener{
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle("Alert")
                .setMessage("Are you sure to update your profile and password? Do you want continue?")
                .setPositiveButton("Yes"){dialog, which ->
                    db.collection("Orders")
                        .document(orderId)
                        .update("cancel_request",true)
                    dialog.dismiss()
                    binding.canceledOrderBtn.isEnabled = false
                }
                .setNegativeButton("No"){dialog, which ->
                }
                .show()
        }
    }
    private fun loadItem(orderId:String){
        val items: ArrayList<MyCartModel> = ArrayList()
        db.collection("Orders")
            .document(orderId)
            .collection("products")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val add = document.toObject<MyCartModel>()
                    items.add(add)
                }
                modelList = items
                //Show Order Detail
                adapter = CheckoutAdater(this, modelList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }
    @SuppressLint("ResourceAsColor")
    private fun displayOrder(orderId:String) {
        //Display Category Detail Information
        db.collection("Orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val item = document.toObject<OrderModel>()
                    binding.deliName.editText!!.setText(item?.name)
                    binding.deliAddress.editText!!.setText(item?.address)
                    binding.deliPhone.editText!!.setText(item?.phone)
                    if (item != null) {
                        when (item.status) {
                            "Completed" -> {
                                binding.radioCompleted.isChecked = true
                                binding.canceledOrderBtn.isEnabled = false
                                binding.canceledOrderBtn.text = "Order has been completed"
                                binding.canceledOrderBtn.setBackgroundColor(R.color.green)
                            }
                            "Being delivery" -> {
                                binding.radioDelivery.isChecked = true
                                binding.canceledOrderBtn.isEnabled = false
                                binding.canceledOrderBtn.text = "Order is being delivery"
                                binding.canceledOrderBtn.setBackgroundColor(R.color.blue)
                            }
                            "Pending" -> {
                                binding.radioPending.isChecked = true
                            }
                            "Canceled" -> {
                                binding.radioCanceled.isChecked = true
                                binding.canceledOrderBtn.isEnabled = false
                                binding.canceledOrderBtn.text = "Order has been cancelled"
                                binding.canceledOrderBtn.setBackgroundColor(R.color.red)
                            }
                            else -> {
                                Log.d("TAG", "No such document")
                            }
                        }
                    }
                }
            }
    }
}