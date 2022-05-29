package com.example.foodapp.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.foodapp.R
import com.example.foodapp.adapter.DetailProductFragmentAdapter
import com.example.foodapp.adapter.DetailProductSildeAdapter
import com.example.foodapp.adapter.FavouriteFragmentAdapter
import com.example.foodapp.adapter.SliderHomeAdapter
import com.example.foodapp.databinding.ActivityDetailProductBinding
import com.example.foodapp.fragment.DescriptionFragment
import com.example.foodapp.model.HomeSliderModel
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.ProductImageModel
import com.example.foodapp.model.ProductModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.snapshot.Index
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import java.text.DecimalFormat


class DetailProductActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {
    //ViewBinding
    private lateinit var binding: ActivityDetailProductBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //
    private var db: FirebaseFirestore? = null
    //
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    private var productModel:ProductModel?= null
    private var tabLayout: TabLayout?= null
    private var viewPager2: ViewPager2?= null
    private var fragmentAdapter: DetailProductFragmentAdapter?= null
    private var fragmentManager: FragmentManager?= null
    private var slideView: SliderView?= null
    private var detailProductSlideAdapter: DetailProductSildeAdapter?= null
    private var detailProductSliderList:ArrayList<ProductImageModel> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        //configure actionbar
        actionBar =supportActionBar!!
        actionBar.hide()
        //Show Product Images
        val productId= intent.getStringExtra("productId").toString()
        db = FirebaseFirestore.getInstance()
        if (productId!=null){
            val items:ArrayList<ProductImageModel> = ArrayList()
            db!!.collection("AllProducts")
                .document(productId)
                .collection("DetailImage")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val item = document.toObject<ProductImageModel>()
                        items.add(item)
                    }
                    detailProductSliderList = items
                    slideView = binding.imageSlider
                    detailProductSlideAdapter = DetailProductSildeAdapter(this,detailProductSliderList!!)
                    slideView!!.setSliderAdapter(detailProductSlideAdapter!!)
                    slideView!!.setIndicatorAnimation(IndicatorAnimationType.WORM)
                    slideView!!.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
                }
        }
        //Display Product Detail Information
        var item: ProductModel? =null
        db!!.collection("AllProducts")
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    item = document.toObject<ProductModel>()
                    productModel = item
                    binding.productName.text = productModel!!.name
                    binding.productRating.text = productModel!!.rating
                    binding.category.text = productModel!!.type
                    binding.productPrice.text = "$${productModel!!.price.toString().toDouble()}"
                    binding.ratingBar.rating = productModel!!.rating!!.toFloat()
                    binding.productTotal.text =productModel!!.price.toString().toDouble().toString()
                    binding.productQty.text = "1"
                } else {
                    Log.d("TAG", "No such document")
                }
            }
        //Set fragment with ViewPager2
        fragmentManager = supportFragmentManager
        fragmentAdapter = DetailProductFragmentAdapter(fragmentManager!!,lifecycle,productId)
//        val bundle = Bundle()
//        val fragment = DescriptionFragment()
//        bundle.putString("product_des", "123")
//        fragment.arguments = bundle
//        supportFragmentManager.beginTransaction()
        viewPager2 = binding.viewPaper
        viewPager2!!.adapter = fragmentAdapter
        tabLayout = binding.tabLayout2
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Description"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Review"))
        tabLayout!!.addOnTabSelectedListener(this)
        viewPager2!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout!!.selectTab(tabLayout!!.getTabAt(position))
            }
        })
        //Add to Cart Btn
        binding.addToCartBtn.setOnClickListener {
            val qty = binding.productQty.text
            val product = productModel
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Adding to cart ....")
            progressDialog.show()
            //Check product in cart
            db!!.collection("ShoppingCart")
                .document(firebaseUser.uid)
                .collection("products")
                .document(product!!.id!!)
                .get()
                .addOnSuccessListener { document->
                    val oldProduct = document.toObject(MyCartModel::class.java)
                    if (oldProduct !=null){
                        val newQty = oldProduct.productQuantity.toString().toInt() + qty.toString().toInt()
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
                            val myCartModel = MyCartModel(product.image,product.name,product.price,
                                qty.toString(),product.description,product.id)
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
                    progressDialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,"Error writing document by$e", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
        }
        //Back Button
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //Add and minus Button
        binding.addBtn.setOnClickListener {
            val qty = binding.productQty.text.toString().toInt()+1
            binding.productQty.text = qty.toString()
            updateTotal()
        }
        binding.minusBtn.setOnClickListener {
            if(binding.productQty.text.toString().toInt() > 1){
                val qty = binding.productQty.text.toString().toInt()-1
                binding.productQty.text = qty.toString()
            }
            updateTotal()
        }
    }
    private fun updateTotal(){
        val qty = binding.productQty.text.toString().toInt()
        val price = productModel!!.price.toString().toDouble()
        binding.productTotal.text = (qty*price).toString()
    }
    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewPager2!!.currentItem = tab!!.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}