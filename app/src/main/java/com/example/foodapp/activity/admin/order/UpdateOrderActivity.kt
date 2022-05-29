package com.example.foodapp.activity.admin.order

import android.app.ProgressDialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.adapter.CheckoutAdater
import com.example.foodapp.databinding.ActivityUpdateCategoryBinding
import com.example.foodapp.databinding.ActivityUpdateOrderBinding
import com.example.foodapp.helper.RandomStringHelper
import com.example.foodapp.model.HomeHorModel
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.OrderModel
import com.example.foodapp.model.StatisticalModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpdateOrderActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityUpdateOrderBinding

    // Firebase Store
    private lateinit var db: FirebaseFirestore

    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //Now
    private val now = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())

    //Adapter
    private var adapter: CheckoutAdater? = null
    private var recyclerView: RecyclerView? = null
    private var productList: ArrayList<MyCartModel>? = null
    private var item: OrderModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateOrderBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        progressDialog = ProgressDialog(this)
        setContentView(binding.root)
        recyclerView = binding.productRv
        db = FirebaseFirestore.getInstance()
        val orderId = intent.getStringExtra("orderId").toString()
        // Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //Display Category Detail Information
        db.collection("Orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    item = document.toObject<OrderModel>()
                    binding.deliName.editText!!.setText(item?.name)
                    binding.deliAddress.editText!!.setText(item?.address)
                    binding.deliPhone.editText!!.setText(item?.phone)
                    if (item != null) {
                        when (item!!.status) {
                            "Completed" -> {
                                binding.radioCompleted.isChecked = true
                            }
                            "Being delivery" -> {
                                binding.radioDelivery.isChecked = true
                            }
                            "Pending" -> {
                                binding.radioPending.isChecked = true
                            }
                            "Canceled" -> {
                                binding.radioCanceled.isChecked = true
                            }
                            else -> {
                                Log.d("TAG", "No such document")
                            }
                        }
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }
        //Order Total
        var orderTotal: Int = 0
        val items: ArrayList<MyCartModel> = ArrayList()
        db.collection("Orders")
            .document(orderId)
            .collection("products")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val add = document.toObject<MyCartModel>()
                    items.add(add)
                    orderTotal += add.productQuantity.toString()
                        .toInt() * add.productPrice.toString().toInt()
//                    myCartAdapter?.notifyDataSetChanged()

                }
                productList = items
                //Show Order Detail
                adapter = CheckoutAdater(this, productList!!)
                recyclerView!!.layoutManager =
                    GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
                recyclerView!!.setHasFixedSize(true)
                recyclerView?.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
        // Update Order Btn
        binding.updateOrderBtn.setOnClickListener {
            val radioId = binding.status.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(radioId)
            if (binding.deliPhone.editText!!.text.trim().toString() == "") {
                binding.deliPhone.editText!!.error = "Required"
            } else if (binding.deliName.editText!!.text.trim().toString() == "") {
                binding.deliName.editText!!.error = "Required"
            } else if (binding.deliAddress.editText!!.text.trim().toString() == "") {
                binding.deliAddress.editText!!.error = "Required"
            } else if (radioId < 0) {
                Toast.makeText(this, "Please choose a status", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog.setMessage("Updating order .......")
                progressDialog.show()
                db.collection("Orders")
                    .document(orderId)
                    .get()
                    .addOnSuccessListener { document ->
                        val item = document.toObject<OrderModel>()
                        if (radioButton.text.toString() == "Completed") {
                            if (item != null) {
                                if(item.status != "Completed"){
                                    //Check Statistical
                                    db.collection("Statistical")
                                        .whereEqualTo("date", now)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (documents.size() != 0) {
                                                val statisticals: ArrayList<StatisticalModel> =
                                                    ArrayList()
                                                for (i in documents) {
                                                    val statistical = i.toObject<StatisticalModel>()
                                                    statisticals.add(statistical)
                                                }
                                                val statisticalModel = statisticals[0]
                                                var newOrderTotal =0
                                                if(statisticalModel.order_total.toString() ==""||statisticalModel.order_total.toString() =="null"||statisticalModel.order_total.toString() == null){
                                                    newOrderTotal = 1
                                                }else {
                                                    newOrderTotal =statisticalModel.order_total.toString().toInt() +1
                                                }
                                                var sales = 0
                                                if(statisticalModel.sales.toString() ==""||statisticalModel.sales.toString() =="null"||statisticalModel.sales.toString() == null){
                                                    sales = orderTotal
                                                }else {
                                                    sales =statisticalModel.sales.toString().toInt() + orderTotal
                                                }
                                                val profit = sales * 10 / 100
                                                db.collection("Statistical")
                                                    .document(statisticalModel.id.toString())
                                                    .update("profit", profit.toString())
                                                db.collection("Statistical")
                                                    .document(statisticalModel.id.toString())
                                                    .update("sales", sales.toString())
                                                db.collection("Statistical")
                                                    .document(statisticalModel.id.toString())
                                                    .update("order_total", newOrderTotal.toString())
                                            } else {
                                                val sales = orderTotal
                                                val profit = sales * 10 / 100
                                                val id = RandomStringHelper().getRandomString(20)
                                                db.collection("Statistical")
                                                    .document(id)
                                                    .set(StatisticalModel(id,
                                                        now,
                                                        "1",
                                                        profit.toString(),
                                                        sales.toString()))
                                            }
                                        }
                                }
                            }
                        }
                        if(radioButton.text.toString() == "Canceled"){
                            db.collection("Orders")
                                .document(orderId)
                                .set(OrderModel(orderId,
                                    item?.uid,
                                    binding.deliName.editText!!.text.toString(),
                                    binding.deliPhone.editText!!.text.toString(),
                                    binding.deliAddress.editText!!.text.toString(),
                                    item?.payment,
                                    radioButton.text.toString(),
                                    item?.date,
                                    item?.total,false
                                ))
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Update this order successfully!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .addOnFailureListener {
                                    progressDialog.dismiss()
                                }
                        }else{
                            db.collection("Orders")
                                .document(orderId)
                                .set(OrderModel(orderId,
                                    item?.uid,
                                    binding.deliName.editText!!.text.toString(),
                                    binding.deliPhone.editText!!.text.toString(),
                                    binding.deliAddress.editText!!.text.toString(),
                                    item?.payment,
                                    radioButton.text.toString(),
                                    item?.date,
                                    item?.total,item?.cancel_request
                                ))
                                .addOnSuccessListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(this, "Update this order successfully!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .addOnFailureListener {
                                    progressDialog.dismiss()
                                }
                        }
                    }

            }
        }

    }
}