package com.example.foodapp.ui.reviewmanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.adapter.ManagerReviewAdapter
import com.example.foodapp.adapter.NewProductAdapter
import com.example.foodapp.databinding.FragmentNewBinding
import com.example.foodapp.databinding.FragmentReviewBinding
import com.example.foodapp.databinding.FragmentReviewManagementBinding
import com.example.foodapp.helper.OnApprovalClickListener
import com.example.foodapp.model.ProductModel
import com.example.foodapp.model.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject


class ReviewManagementFragment : Fragment(), OnApprovalClickListener {
    private var _binding: FragmentReviewManagementBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ManagerReviewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var modelList: ArrayList<ReviewModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentReviewManagementBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //
        db = FirebaseFirestore.getInstance()
        recyclerView = binding.managerReviewRv
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        loadData()
        getWaitReview()
        //
        binding.cardTotal.setOnClickListener {
            binding.title.text = "Reviews List"
            loadData()
        }
        binding.cardWait.setOnClickListener {
            loadWaitReview()
        }

        return root
    }

    private fun loadData() {
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                modelList = list
                adapter = ManagerReviewAdapter(context!!,modelList,this)
                recyclerView.adapter = adapter
                //Display
                binding.totalReview.text = modelList.size.toString()
            }
    }
    private fun getWaitReview() {
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("active",false)
            .get()
            .addOnSuccessListener {
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                modelList = list
                binding.reviewWait.text = modelList.size.toString()
            }
    }
    private fun loadWaitReview() {
        binding.title.text = "Waiting for approval Reviews"
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("active",false)
            .get()
            .addOnSuccessListener {
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                modelList = list
                adapter = ManagerReviewAdapter(context!!,modelList,this)
                recyclerView.adapter = adapter
            }
    }
    override fun onButtonClick(position: Int, status: Boolean) {
        val model = modelList.get(position)
        if(model.active == false){
            model.id?.let {
                db.collection("Reviews")
                    .document(it)
                    .update("active",true)
                    .addOnSuccessListener {
                        getWaitReview()
                        Toast.makeText(context,"Successful approval",Toast.LENGTH_SHORT).show()
                    }
            }
        }else{
            model.id?.let {
                db.collection("Reviews")
                    .document(it)
                    .update("active",false)
                    .addOnSuccessListener {
                        Toast.makeText(context,"Successful disapproval",Toast.LENGTH_SHORT).show()
                        getWaitReview()
                    }
            }
        }
    }


}