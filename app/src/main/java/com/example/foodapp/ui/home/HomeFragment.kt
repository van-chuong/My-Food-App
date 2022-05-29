package com.example.foodapp.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.activity.DetailProductActivity
import com.example.foodapp.activity.ProfileActivity
import com.example.foodapp.activity.SearchProductActivity
import com.example.foodapp.adapter.HomeHorAdapter
import com.example.foodapp.adapter.HomeVerAdapter
import com.example.foodapp.adapter.SliderHomeAdapter
import com.example.foodapp.databinding.FragmentHomeBinding
import com.example.foodapp.helper.OnItemHorHomeClickListener
import com.example.foodapp.helper.OnOpenBottomSheetListener
import com.example.foodapp.model.HomeHorModel
import com.example.foodapp.model.HomeSliderModel
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.ProductModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.makeramen.roundedimageview.RoundedImageView
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class HomeFragment : Fragment(),
    OnOpenBottomSheetListener, OnItemHorHomeClickListener {

    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    private var db: FirebaseFirestore? = null
    private var _binding: FragmentHomeBinding? = null
    private var homeHorizontalRecyclerView: RecyclerView? = null
    private var homeHorModelList: ArrayList<HomeHorModel>? = null
    private var homeHorAdapter: HomeHorAdapter? = null
    private var homeVerticalRecyclerView: RecyclerView? = null
    private var productModelList: ArrayList<ProductModel>? = null
    private var homeVerAdapter: HomeVerAdapter? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var slideView: SliderView? = null
    private var sliderHomeAdapter: SliderHomeAdapter? = null
    private var homeSliderModelList: ArrayList<HomeSliderModel>? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        //Profile Btn
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(activity, ProfileActivity::class.java))
        }
        //Search Btn
        binding.searchBtn.setOnClickListener {
            startActivity(Intent(activity, SearchProductActivity::class.java))
        }
        if (firebaseUser.displayName != null) {
            binding.helloTxt.text = "Hello ${firebaseUser.displayName}"
        }

        binding.progressBarHor.visibility = View.VISIBLE
        binding.constraintLayout.visibility = View.GONE
        binding.progressBarVer.visibility = View.GONE
        db = FirebaseFirestore.getInstance()
        val item3: ArrayList<HomeSliderModel> = ArrayList()
        db!!.collection("HomeSlides")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject<HomeSliderModel>()
                    item3.add(item)
                }
                binding.progressBarHor.visibility = View.GONE
                binding.constraintLayout.visibility = View.VISIBLE
                homeSliderModelList = item3
                slideView = binding.imageSlider
                sliderHomeAdapter = SliderHomeAdapter(context!!, homeSliderModelList!!)
                slideView!!.setSliderAdapter(sliderHomeAdapter!!)
                slideView!!.setIndicatorAnimation(IndicatorAnimationType.WORM)
                slideView!!.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
                slideView!!.startAutoCycle();
                showHorHomeRv()
            }
        return root
    }

    private fun showHorHomeRv() {
        val items: ArrayList<HomeHorModel> = ArrayList()
        db!!.collection("HomeCategory")
            .orderBy("name")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject<HomeHorModel>()
                    items.add(item)
                }
                //Show Home HorizontalRecyclerView Items
                homeHorModelList = items
                homeHorAdapter = HomeHorAdapter(activity!!, homeHorModelList!!, this)
                homeHorizontalRecyclerView = binding.homeHorRV
                homeHorizontalRecyclerView!!.layoutManager =
                    LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                homeHorizontalRecyclerView!!.setHasFixedSize(true)
                homeHorizontalRecyclerView?.adapter = homeHorAdapter
                showVerHomeRv()
            }
    }

    private fun showVerHomeRv() {
        //Show Home VerticalRecyclerView Items
        val items2: ArrayList<ProductModel> = ArrayList()
        db!!.collection("AllProducts")
            .whereEqualTo("type", homeHorModelList!!.get(0).name!!)
            .limit(4)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject<ProductModel>()
                    items2.add(item)
                }
                productModelList = items2
                homeVerAdapter = HomeVerAdapter(activity!!, productModelList!!, this)
                homeVerticalRecyclerView = binding.homeVerRV
                homeVerticalRecyclerView!!.layoutManager =
                    GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                homeVerticalRecyclerView!!.setHasFixedSize(true)
                homeVerticalRecyclerView?.adapter = homeVerAdapter
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProductClick(position: Int) {
        val intent = Intent(context, DetailProductActivity::class.java)
        intent.putExtra("productId", productModelList!!.get(position).id)
        startActivity(intent)
    }

    override fun onOpenBottomSheetClick(position: Int) {
        val product = productModelList!!.get(position)
        bottomSheetDialog = BottomSheetDialog(context!!, R.style.BottomSheetTheme)
        val sheetView: View =
            LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null)
        sheetView.findViewById<Button>(R.id.add_to_cart_btn).setOnClickListener {
            val progresDialog = ProgressDialog(context)
            progresDialog.setMessage("Adding to cart ....")
            progresDialog.show()
            //Check product in cart
            db!!.collection("ShoppingCart")
                .document(firebaseUser.uid)
                .collection("products")
                .document(product.id!!)
                .get()
                .addOnSuccessListener { document ->
                    val oldProduct = document.toObject(MyCartModel::class.java)
                    if (oldProduct != null) {
                        val newQty = oldProduct.productQuantity.toString().toInt() + 1
                        if (firebaseUser.uid != null) {
                            db!!.collection("ShoppingCart")
                                .document(firebaseUser.uid)
                                .collection("products")
                                .document(product.id.toString())
                                .update("productQuantity", newQty.toString())
                                .addOnSuccessListener {
                                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else if (oldProduct == null) {
                        if (firebaseUser.uid != null) {
                            val myCartModel = MyCartModel(product.image,
                                product.name,
                                product.price,
                                "1",
                                product.description,
                                product.id)
                            db!!.collection("ShoppingCart")
                                .document(firebaseUser.uid)
                                .collection("products")
                                .document(product.id!!)
                                .set(myCartModel)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    progresDialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error writing document by$e", Toast.LENGTH_SHORT)
                        .show()
                    progresDialog.dismiss()
                }
//            bottomSheetDialog!!.dismiss()
        }
        Glide.with(sheetView).load(product.image)
            .into(sheetView.findViewById<RoundedImageView>(R.id.product_img))
        sheetView.findViewById<TextView>(R.id.bottom_sheet_name).text = product.name
        sheetView.findViewById<TextView>(R.id.bottom_sheet_rating).text = product.rating
        sheetView.findViewById<TextView>(R.id.bottom_sheet_timing).text = product.timing
        sheetView.findViewById<TextView>(R.id.bottom_sheet_price).text =
            product.price.toString().toDouble().toString()
        sheetView.findViewById<TextView>(R.id.bottom_sheet_description).text = product.description
        bottomSheetDialog!!.setContentView(sheetView)
        bottomSheetDialog!!.show()
    }

    override fun OnItemClick(position: Int) {
        val homeHorModel: HomeHorModel = homeHorModelList?.get(position)!!
        val type: String = homeHorModel.name.toString()
        val productModel: ArrayList<ProductModel> = ArrayList()
        db!!.collection("AllProducts")
            .whereEqualTo("type", type)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject<ProductModel>()
                    productModel.add(item)
                }
                productModelList = productModel
                homeVerAdapter = HomeVerAdapter(activity!!, productModelList!!, this)
                homeVerticalRecyclerView = binding.homeVerRV
                homeVerticalRecyclerView!!.layoutManager =
                    GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                homeVerticalRecyclerView!!.setHasFixedSize(true)
                homeVerticalRecyclerView?.adapter = homeVerAdapter
            }
    }
}