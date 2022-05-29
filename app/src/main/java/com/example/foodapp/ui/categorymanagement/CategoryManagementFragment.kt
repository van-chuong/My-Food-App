package com.example.foodapp.ui.categorymanagement

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
import com.example.foodapp.adapter.ManagerCategoryAdapter
import com.example.foodapp.databinding.FragmentCategoryManagementBinding
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.HomeHorModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore


class CategoryManagementFragment : Fragment(), OnItemViewManagerClick {
    private var _binding: FragmentCategoryManagementBinding? = null
    //ActionBar
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private var categoryManagementAdapter: ManagerCategoryAdapter? = null
    private var categoryRecyclerView: RecyclerView? = null
    private var categoryModelList: ArrayList<HomeHorModel>? = null
    private var allowRefresh = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCategoryManagementBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        categoryRecyclerView = binding.managerCategoryRv
        val items: ArrayList<HomeHorModel> = ArrayList()
        db.collection("HomeCategory")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject(HomeHorModel::class.java)
                    items.add(item)
                }
                binding.categoryQty.text ="${items.size} product categories"
                categoryModelList = items
                categoryManagementAdapter = ManagerCategoryAdapter(activity!!, categoryModelList!!,this)
                categoryRecyclerView!!.layoutManager = GridLayoutManager(activity,2, GridLayoutManager.HORIZONTAL, false)
                categoryRecyclerView!!.setHasFixedSize(true)
                categoryRecyclerView?.adapter = categoryManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        //Add Category Btn
        binding.addCategoryBtn.setOnClickListener {
            val intent = Intent(context, AddCategoryActivity::class.java)
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun OnItemClick(position: Int) {
        val intent = Intent(context, UpdateCategoryActivity::class.java)
        intent.putExtra("categoryId", categoryModelList!!.get(position).id)
        startActivity(intent)
    }

    override fun OnDeleteClick(position: Int) {
        val categoryModel = categoryModelList!![position]
        var check:String ?=null
        Log.d("TAG",categoryModel.name.toString())
        db.collection("AllProducts")
            .whereEqualTo("type",categoryModel.name.toString())
            .get()
            .addOnSuccessListener { documents ->
                check = documents.documents.toString()
                Log.d("TAG",check.toString())
                if(check != "[]"){
                    val dialog = MaterialAlertDialogBuilder(context!!)
                        .setTitle("Alert")
                        .setMessage("You cannot delete this category because there are products in this category")
                        .setNegativeButton("Cancel"){ dialog, which ->
                        }
                        .show()
                }else{
                    val dialog = MaterialAlertDialogBuilder(context!!)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete this category ? Deletion cannot be undone! Do you want continue?")
                        .setNegativeButton("Yes"){ dialog, which ->
                            db.collection("HomeCategory")
                                .document(categoryModel.id.toString())
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context,"Delete this category successfully!", Toast.LENGTH_SHORT).show()
                                    categoryModelList!!.removeAt(position)
                                    categoryManagementAdapter!!.notifyItemRemoved(position)
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
            }


    }

    override fun onResume() {
        super.onResume()
        if(allowRefresh){
            allowRefresh=false;
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.nav_manager_categories)
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