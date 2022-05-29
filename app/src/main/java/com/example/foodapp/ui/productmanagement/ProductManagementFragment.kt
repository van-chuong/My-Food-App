package com.example.foodapp.ui.productmanagement

import android.app.ActionBar
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
import com.example.foodapp.activity.DetailProductActivity
import com.example.foodapp.activity.admin.product.AddProductActivity
import com.example.foodapp.activity.admin.product.UpdateProductActivity
import com.example.foodapp.adapter.ManagerProductAdapter
import com.example.foodapp.adapter.MyCartAdapter
import com.example.foodapp.databinding.FragmentProductManagementBinding
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.ProductModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore


class ProductManagementFragment : Fragment(), OnItemViewManagerClick {
    private var _binding: FragmentProductManagementBinding? = null

    //ActionBar
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private var productManagementAdapter: ManagerProductAdapter? = null
    private var productRecyclerView: RecyclerView? = null
    private var productModelList: ArrayList<ProductModel>? = null
    private var allowRefresh = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProductManagementBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()

        displayProduct()
        binding.cardOutOfStock.setOnClickListener {
            binding.title.text = "Products Out Of Stock  "
            displayOutOfProduct()
        }
        binding.cardTotal.setOnClickListener {
            binding.title.text = "Products List"
            displayProduct()
        }

        //Add product Btn
        binding.addProductBtn.setOnClickListener {
            val intent = Intent(context, AddProductActivity::class.java)
            startActivity(intent)
        }

        val items: ArrayList<ProductModel> = ArrayList()
        db.collection("AllProducts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["quantity"] == "0" || document["quantity"] == "null" || document["quantity"] == ""|| document["quantity"] == null){
                        val item = document.toObject(ProductModel::class.java)
                        items.add(item)
                    }
                }
                binding.outOfStockProduct.text = items.size.toString()
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        // Inflate the layout for this fragment
        return binding.root

    }
    private fun displayOutOfProduct() {
        val items: ArrayList<ProductModel> = ArrayList()
        db.collection("AllProducts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["quantity"] == "0" || document["quantity"] == "null" || document["quantity"] == ""|| document["quantity"] == null){
                        val item = document.toObject(ProductModel::class.java)
                        items.add(item)
                    }
                }
                productModelList = items
                productManagementAdapter =
                    ManagerProductAdapter(activity!!, productModelList!!, this)
                productRecyclerView = binding.managerProductRv
                productRecyclerView!!.layoutManager =
                    GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
                productRecyclerView!!.setHasFixedSize(true)
                productRecyclerView?.adapter = productManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }
    private fun displayProduct() {
        val items: ArrayList<ProductModel> = ArrayList()
        db.collection("AllProducts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject(ProductModel::class.java)
                    items.add(item)
                }
                binding.totalProduct.text = items.size.toString()
                productModelList = items
                productManagementAdapter =
                    ManagerProductAdapter(activity!!, productModelList!!, this)
                productRecyclerView = binding.managerProductRv
                productRecyclerView!!.layoutManager =
                    GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
                productRecyclerView!!.setHasFixedSize(true)
                productRecyclerView?.adapter = productManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    override fun OnItemClick(position: Int) {
        val intent = Intent(context, UpdateProductActivity::class.java)
        intent.putExtra("productId", productModelList!!.get(position).id)
        startActivity(intent)
    }

    override fun OnDeleteClick(position: Int) {
        val productModel = productModelList!![position]
        val dialog = MaterialAlertDialogBuilder(context!!)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product ? Deletion cannot be undone! Do you want continue?")
            .setNegativeButton("Yes") { dialog, which ->
                db.collection("AllProducts")
                    .document(productModel.id.toString())
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Delete product successfully!", Toast.LENGTH_SHORT)
                            .show()
                        productModelList!!.removeAt(position)
                        productManagementAdapter!!.notifyItemChanged(position)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context,
                            "Something went wrong, please try again later!",
                            Toast.LENGTH_SHORT).show()
                    }
            }
            .setPositiveButton("No") { dialog, which ->
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (allowRefresh) {
            allowRefresh = false;
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.nav_manager_product)
            }
        }
        Log.d("TAG", "onResume")
    }

    override fun onPause() {
        super.onPause()
        if (!allowRefresh) {
            allowRefresh = true
        }
        Log.d("TAG", "onPause")
    }
}