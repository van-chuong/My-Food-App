package com.example.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.adapter.NewProductAdapter
import com.example.foodapp.adapter.OrdersAdapter
import com.example.foodapp.databinding.ActivityOrderListBinding
import com.example.foodapp.databinding.ActivityProfileBinding
import com.example.foodapp.helper.OnItemHorHomeClickListener
import com.example.foodapp.model.OrderModel
import com.example.foodapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class OrderListActivity : AppCompatActivity(), OnItemHorHomeClickListener {
    //ViewBinding
    private lateinit var binding: ActivityOrderListBinding
    //ActionBar
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    //
    private lateinit var adapter: OrdersAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var modelList: ArrayList<OrderModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        actionBar = supportActionBar!!
        actionBar.hide()
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        recyclerView = binding.ordersRv
        recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        loadData()
        //Back btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }
    private fun loadData(){
        val list = ArrayList<OrderModel>()
        db.collection("Orders")
            .whereEqualTo("uid",firebaseUser.uid)
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<OrderModel>()
                    list.add(item)
                }

                modelList = list
                adapter = OrdersAdapter(this,modelList,this)
                recyclerView.adapter = adapter
            }
    }

    override fun OnItemClick(position: Int) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra("orderId", modelList[position].orderId)
        startActivity(intent)
    }
}