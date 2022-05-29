package com.example.foodapp.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.adapter.CheckoutAdater
import com.example.foodapp.databinding.ActivityCheckoutBinding
import com.example.foodapp.helper.RandomStringHelper
import com.example.foodapp.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckoutActivity : AppCompatActivity() {
    //ViewBinding
    private lateinit var binding: ActivityCheckoutBinding

    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar

    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser

    //Database
    private lateinit var db: FirebaseFirestore

    //Progress
    private lateinit var progressDialog: ProgressDialog
    private var checkoutAdapter: CheckoutAdater? = null
    private var checkoutRecyclerView: RecyclerView? = null
    private var checkoutModelList: ArrayList<MyCartModel>? = null
    private val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        setContentView(binding.root)
        // Back Btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        progressDialog = ProgressDialog(this)
        //Init Firebase
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        //
        db.collection("Users")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val item = document.toObject<User>()
                if (item != null) {
                    binding.checkoutName.editText!!.setText(item.name)
                    binding.checkoutAddress.editText!!.setText(item.address)
                    binding.checkoutPhone.editText!!.setText(item.phone)
                }
            }
        //
        var subTotal: Int = 0
        val items: ArrayList<MyCartModel> = ArrayList()
        db.collection("ShoppingCart")
            .document(firebaseUser.uid)
            .collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject<MyCartModel>()
                    items.add(item)
                    subTotal += item.productQuantity!!.toInt() * item.productPrice!!.toInt()
                }
                binding.subTotal.text = subTotal.toDouble().toString()
                binding.total.text = subTotal.toDouble().toString()
                checkoutModelList = items
                //Show Order Detail
                checkoutAdapter = CheckoutAdater(this, checkoutModelList!!)
                checkoutRecyclerView = binding.checkoutRv
                checkoutRecyclerView!!.layoutManager =
                    GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
                checkoutRecyclerView!!.setHasFixedSize(true)
                checkoutRecyclerView?.adapter = checkoutAdapter
            }
        //Confirm Btn
        binding.confirmButton.setOnClickListener {
            val radioId = binding.paymentOption.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(radioId)
            if (binding.checkoutPhone.editText!!.text.trim().toString() == "") {
                binding.checkoutPhone.editText!!.error = "Required"
            } else if (binding.checkoutName.editText!!.text.trim().toString() == "") {
                binding.checkoutName.editText!!.error = "Required"
            } else if (binding.checkoutAddress.editText!!.text.trim().toString() == "") {
                binding.checkoutAddress.editText!!.error = "Required"
            } else if (radioId < 0) {
                Toast.makeText(this, "Please choose a payment option", Toast.LENGTH_SHORT).show()
            } else if (radioButton.text == "With Google PLay") {
                Toast.makeText(this,
                    "This option of payment is not accepted please choose another payment option",
                    Toast.LENGTH_SHORT).show()
            } else {
                progressDialog.setMessage("Processing this Order ........")
                progressDialog.show()
                val orderId = RandomStringHelper().getRandomString(20)

                Log.d("TAG", now)
                db.collection("Orders")
                    .document(orderId)
                    .set(OrderModel(orderId,
                        firebaseUser.uid,
                        binding.checkoutName.editText!!.text.toString(),
                        binding.checkoutPhone.editText!!.text.toString(),
                        binding.checkoutAddress.editText!!.text.toString(),
                        radioButton.text.toString(),
                        "Pending",
                        now,
                        binding.total.text.toString(),false))
                    .addOnSuccessListener {
                        for (i in checkoutModelList!!) {
                            db.collection("AllProducts")
                                .document(i.productId.toString())
                                .get()
                                .addOnSuccessListener { document ->
                                    val product = document.toObject<ProductModel>()
                                    if (product != null) {
                                        val newqty = product.quantity.toString().toInt() - 1
                                        val newsale = product.sales.toString().toInt() + 1
                                        db.collection("AllProducts")
                                            .document(i.productId.toString())
                                            .update("quantity", newqty.toString())
                                        db.collection("AllProducts")
                                            .document(i.productId.toString())
                                            .update("sales", newsale.toString())
                                    }
                                }
                            db.collection("Orders")
                                .document(orderId)
                                .collection("products")
                                .add(i)
                            db.collection("ShoppingCart")
                                .document(firebaseUser.uid)
                                .collection("products")
                                .document(i.productId.toString())
                                .delete()
                        }
                        progressDialog.dismiss()
                        val intent = Intent(this, OrderSuccessfullyActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
            }
        }
        //
    }

}