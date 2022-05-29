package com.example.foodapp.ui.ordermanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.activity.admin.category.AddCategoryActivity
import com.example.foodapp.activity.admin.category.UpdateCategoryActivity
import com.example.foodapp.activity.admin.order.UpdateOrderActivity
import com.example.foodapp.adapter.ManagerCategoryAdapter
import com.example.foodapp.adapter.ManagerOrderAdapter
import com.example.foodapp.databinding.FragmentCategoryManagementBinding
import com.example.foodapp.databinding.FragmentOrderManagementBinding
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.HomeHorModel
import com.example.foodapp.model.OrderModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class OrderManagementFragment : Fragment(), OnItemViewManagerClick {
    private var _binding: FragmentOrderManagementBinding? = null
    //ActionBar
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private var orderManagementAdapter: ManagerOrderAdapter? = null
    private var orderRecyclerView: RecyclerView? = null
    private var orderModelList: ArrayList<OrderModel>? = null
    private var allowRefresh = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentOrderManagementBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        orderRecyclerView = binding.managerOrderRv
        orderRecyclerView!!.layoutManager = GridLayoutManager(activity,1, GridLayoutManager.VERTICAL, false)
        orderRecyclerView!!.setHasFixedSize(true)
        displayOrder()
        binding.cardCompleted.setOnClickListener {
            displayCompletedOrder()
        }
        binding.cardPending.setOnClickListener {
            displayPendingOrder()
        }
        binding.cardDelivery.setOnClickListener {
            displayDeliveryOrder()
        }
        binding.cardCancel.setOnClickListener {
            displayCancelOrder()
        }

        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val items: ArrayList<OrderModel>
                        = ArrayList()
                for (document in result) {
                    if(document["status"] == "Pending"){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                binding.pendingTv.text = "${items.size} Pending orders"
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val items: ArrayList<OrderModel>
                        = ArrayList()
                for (document in result) {
                    if(document["cancel_request"] == true){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                binding.cencelTv.text = "${items.size} Cancel Request"
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val items: ArrayList<OrderModel>
                        = ArrayList()
                for (document in result) {
                    if(document["status"] == "Being delivery"){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                binding.deliveryTv.text = "${items.size} Being delivery orders"
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val items: ArrayList<OrderModel>
                        = ArrayList()
                for (document in result) {
                    if(document["status"] == "Completed"){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                binding.completeTv.text = "${items.size} Completed orders"
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun displayCancelOrder() {
        binding.title.text = "Request Cancel Orders"
        val items: ArrayList<OrderModel> = ArrayList()
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["cancel_request"] == true){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                orderModelList = items
                orderManagementAdapter = ManagerOrderAdapter(activity!!, orderModelList!!,this)
                orderRecyclerView?.adapter = orderManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun displayDeliveryOrder() {
        binding.title.text = "Being Delivery Orders"
        val items: ArrayList<OrderModel> = ArrayList()
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["status"] == "Being delivery"){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                orderModelList = items
                orderManagementAdapter = ManagerOrderAdapter(activity!!, orderModelList!!,this)
                orderRecyclerView?.adapter = orderManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun displayCompletedOrder() {
        binding.title.text = "Completed Orders"
        val items: ArrayList<OrderModel> = ArrayList()
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["status"] == "Completed"){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                orderModelList = items
                orderManagementAdapter = ManagerOrderAdapter(activity!!, orderModelList!!,this)
                orderRecyclerView?.adapter = orderManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun displayOrder(){
        binding.title.text = "Orders List"
        val items: ArrayList<OrderModel> = ArrayList()
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject(OrderModel::class.java)
                    items.add(item)
                }
                orderModelList = items
                orderManagementAdapter = ManagerOrderAdapter(activity!!, orderModelList!!,this)
                orderRecyclerView?.adapter = orderManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun displayPendingOrder(){
        binding.title.text = "Pending Orders"
        val items: ArrayList<OrderModel> = ArrayList()
        db.collection("Orders")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["status"] == "Pending"){
                        val item = document.toObject(OrderModel::class.java)
                        items.add(item)
                    }
                }
                orderModelList = items
                orderManagementAdapter = ManagerOrderAdapter(activity!!, orderModelList!!,this)
                orderRecyclerView?.adapter = orderManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }


    override fun OnItemClick(position: Int) {
        val intent = Intent(context, UpdateOrderActivity::class.java)
        intent.putExtra("orderId", orderModelList!!.get(position).orderId)
        startActivity(intent)
    }

    override fun OnDeleteClick(position: Int) {
        val orderModel = orderModelList!![position]
        val dialog = MaterialAlertDialogBuilder(context!!)
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete this order ? Deletion cannot be undone! Do you want continue?")
            .setNegativeButton("Yes"){ dialog, which ->
                db.collection("Orders")
                    .document(orderModel.orderId.toString())
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context,"Delete this order successfully!", Toast.LENGTH_SHORT).show()
                        orderModelList!!.removeAt(position)
                        orderManagementAdapter!!.notifyItemRemoved(position)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context,"Something went wrong, please try again later!",
                            Toast.LENGTH_SHORT).show()
                    }
            }
            .setPositiveButton("No"){ dialog, which ->
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        if(allowRefresh){
            allowRefresh=false;
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.nav_manager_order)
            }
        }
        Log.d("TAG","onResume")
    }

    override fun onPause() {
        super.onPause()
        if(!allowRefresh){
            allowRefresh = true
        }
        Log.d("TAG","onPause")
    }
}