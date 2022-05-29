package com.example.foodapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.adapter.NewProductAdapter
import com.example.foodapp.adapter.ReviewsAdapter
import com.example.foodapp.databinding.FragmentReviewBinding
import com.example.foodapp.helper.RandomStringHelper
import com.example.foodapp.model.ProductModel
import com.example.foodapp.model.ReviewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    //FirebaseFirestore
    private lateinit var db: FirebaseFirestore

    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    //
    private lateinit var adapter: ReviewsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var modelList: ArrayList<ProductModel>
    private val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val productId = arguments?.get("productId")
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        recyclerView = binding.commentRv
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        //Load Comment
        loadComment(productId.toString())
        getRating1(productId.toString())
        getRating2(productId.toString())
        getRating3(productId.toString())
        getRating4(productId.toString())
        getRating5(productId.toString())
        //Submit Review
        binding.submitReview.setOnClickListener {
            if (binding.textInputLayout.editText!!.text.trim().toString() == "") {
                binding.textInputLayout.editText!!.error = "Required"
            } else {
                val id = RandomStringHelper().getRandomString(20)
                db.collection("Reviews")
                    .document(id)
                    .set(ReviewModel(
                        id,
                        firebaseUser.uid,
                        productId.toString(),
                        binding.ratingBar.rating.toString(),
                        binding.textInputLayout.editText!!.text.trim().toString(),
                        false,
                        firebaseUser.displayName,
                        firebaseUser.photoUrl.toString(),now
                    ))
                    .addOnSuccessListener {
                        Toast.makeText(context,
                            "Submit review successfully, please wait for approval",
                            Toast.LENGTH_LONG).show()
                    }
            }
        }
        //Check Comment
        checkComment(productId.toString(),firebaseUser.uid)
        // Inflate the layout for this fragment
        return root
    }

    fun newInstance(productId: String): ReviewFragment {
        val fragment = ReviewFragment()
        val args = Bundle()
        args.putString("productId", productId)
        fragment.arguments = args
        return fragment
    }
    private fun checkComment(productId: String, uid:String){
        db.collection("Reviews")
            .whereEqualTo("uid",uid)
            .whereEqualTo("proid",productId)
            .get()
            .addOnSuccessListener { it->
                if(it.size()!=0){
                    binding.comment.visibility = View.GONE
                }
            }
    }
    private fun loadComment(productId: String){
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("proid",productId)
            .whereEqualTo("active",true)
            .get()
            .addOnSuccessListener { it->
                var total:Float = 0F
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                    total += item.rating.toString().toFloat()
                }
                if(list.size == 0){
                    binding.ratingTotal.text = "5.0"
                    binding.ratingBar1.rating = 5.0f
                }else{
                    binding.ratingTotal.text = (total/list.size).toString()
                    binding.ratingBar1.rating = total/list.size
                }
                binding.totalReview.text = "Base on ${list.size} reviews"
                adapter = ReviewsAdapter(context!!,list)
                recyclerView.adapter = adapter

            }
    }
    private fun getRating1(productId: String){
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("proid",productId)
            .whereEqualTo("active",true)
            .whereIn("rating",mutableListOf("1.0","1.5"))
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                binding.review1.text = "${list.size} Reviews"
            }
    }
    private fun getRating2(productId: String){
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("proid",productId)
            .whereEqualTo("active",true)
            .whereIn("rating",mutableListOf("2.0","2.5"))
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                binding.review2.text = "${list.size} Reviews"
            }
    }
    private fun getRating3(productId: String){
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("proid",productId)
            .whereEqualTo("active",true)
            .whereIn("rating",mutableListOf("3.0","3.5"))
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                binding.review3.text = "${list.size} Reviews"
            }
    }
    private fun getRating4(productId: String){
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("proid",productId)
            .whereEqualTo("active",true)
            .whereIn("rating",mutableListOf("4.0","4.5"))
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                binding.review4.text = "${list.size} Reviews"
            }
    }
    private fun getRating5(productId: String){
        val list = ArrayList<ReviewModel>()
        db.collection("Reviews")
            .whereEqualTo("proid",productId)
            .whereEqualTo("active",true)
            .whereIn("rating",mutableListOf("5.0"))
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<ReviewModel>()
                    list.add(item)
                }
                binding.review5.text = "${list.size} Reviews"
            }
    }
}