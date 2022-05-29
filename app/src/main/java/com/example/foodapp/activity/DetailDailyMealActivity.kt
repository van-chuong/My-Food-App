package com.example.foodapp.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.adapter.DetailDailyMealAdapter
import com.example.foodapp.databinding.ActivityDetailDailyMealBinding
import com.example.foodapp.helper.OnItemDetailDailyMeal
import com.example.foodapp.model.DailyMealModel
import com.example.foodapp.model.HomeHorModel
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class DetailDailyMealActivity : AppCompatActivity(), OnItemDetailDailyMeal {
    private lateinit var binding: ActivityDetailDailyMealBinding
    private var db: FirebaseFirestore? = null
    private var detailDailyMealAdapter: DetailDailyMealAdapter? = null
    private var detailDailyMealRecyclerView: RecyclerView? = null
    private var detailDailyMealModelList: ArrayList<ProductModel>? = null
    private var actionBar:ActionBar?=null
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        binding = ActivityDetailDailyMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar = supportActionBar
        actionBar?.hide()
        binding.progressBar.visibility = View.VISIBLE
        binding.detailDailyMealRV.visibility = View.GONE
        val categorydailymeals = intent.getStringExtra("categorydailymeals")
        val detailDailyMealImage = intent.getStringExtra("DetailDailyMealImage")
        val detailDailyMealName = intent.getStringExtra("DetailDailyMealName")
        Glide.with(this).load(detailDailyMealImage).into(binding.dailyMealImg)
        binding.toolbar.title = detailDailyMealName
        val items:ArrayList<ProductModel> = ArrayList()
        db = FirebaseFirestore.getInstance()
        var process:Int = 0
        db!!.collection("AllProducts")
            .whereEqualTo("categorydailymeals",categorydailymeals)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    process++
                    val item = document.toObject<ProductModel>()
                    items.add(item)
                    detailDailyMealAdapter?.notifyDataSetChanged()
                    if(process==4 || result.size() < 4){
                        binding.progressBar.visibility = View.GONE
                        binding.detailDailyMealRV.visibility = View.VISIBLE
                    }
                }

            }
        detailDailyMealModelList = items
        detailDailyMealRecyclerView = binding.detailDailyMealRV
        detailDailyMealRecyclerView!!.layoutManager = LinearLayoutManager(this)
        detailDailyMealAdapter = DetailDailyMealAdapter(this,detailDailyMealModelList,this)
        detailDailyMealRecyclerView!!.adapter = detailDailyMealAdapter
        //Back Button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        // Cart Button
    }

    override fun onProductClick(position: Int) {
        val intent = Intent(this,DetailProductActivity::class.java)
        intent.putExtra("productId", detailDailyMealModelList!!.get(position).id)
        startActivity(intent)
    }

    override fun onAddToCartClick(position: Int) {
        val product = detailDailyMealModelList!!.get(position)
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Adding to cart ....")
        progresDialog.show()
        //Check product in cart
        db!!.collection("ShoppingCart")
            .document(firebaseUser.uid)
            .collection("products")
            .document(product.id!!)
            .get()
            .addOnSuccessListener { document->
                val oldProduct = document.toObject(MyCartModel::class.java)
                if (oldProduct !=null){
                    val newQty = oldProduct.productQuantity.toString().toInt() + 1
                    if(firebaseUser.uid != null){
                        db!!.collection("ShoppingCart")
                            .document(firebaseUser.uid)
                            .collection("products")
                            .document(product.id.toString())
                            .update("productQuantity",newQty.toString())
                            .addOnSuccessListener {
                                Toast.makeText(this,"success", Toast.LENGTH_SHORT).show()
                            }
                    }
                }else if(oldProduct == null){
                    if(firebaseUser.uid != null){
                        val myCartModel = MyCartModel(product.image,product.name,product.price,"1",product.description,product.id)
                        db!!.collection("ShoppingCart")
                            .document(firebaseUser.uid)
                            .collection("products")
                            .document(product.id!!)
                            .set(myCartModel)
                            .addOnSuccessListener {
                                Toast.makeText(this,"success", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                progresDialog.dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"Error writing document by$e", Toast.LENGTH_SHORT).show()
                progresDialog.dismiss()
            }
    }
}