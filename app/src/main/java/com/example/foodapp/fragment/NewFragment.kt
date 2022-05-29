package com.example.foodapp.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.activity.DetailProductActivity
import com.example.foodapp.adapter.DetailDailyMealAdapter
import com.example.foodapp.adapter.NewProductAdapter
import com.example.foodapp.databinding.FragmentNewBinding
import com.example.foodapp.databinding.FragmentPopularBinding
import com.example.foodapp.helper.OnItemDetailDailyMeal
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject


class NewFragment : Fragment(), OnItemDetailDailyMeal {
    private var _binding: FragmentNewBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: NewProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var modelList: ArrayList<ProductModel>
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        recyclerView = binding.newRv
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        loadData()

        return root
    }
    private fun loadData(){
        val list = ArrayList<ProductModel>()
        db.collection("AllProducts")
            .orderBy("created", Query.Direction.DESCENDING)
            .limit(7)
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<ProductModel>()
                    list.add(item)
                }
                modelList = list
                adapter = NewProductAdapter(context!!,modelList,this)
                recyclerView.adapter = adapter
            }
    }

    override fun onProductClick(position: Int) {
        val intent = Intent(context, DetailProductActivity::class.java)
        intent.putExtra("productId", modelList.get(position).id)
        startActivity(intent)
    }

    override fun onAddToCartClick(position: Int) {
        val product = modelList.get(position)
        val progresDialog = ProgressDialog(context)
        progresDialog.setMessage("Adding to cart ....")
        progresDialog.show()
        //Check product in cart
        db.collection("ShoppingCart")
            .document(firebaseUser.uid)
            .collection("products")
            .document(product.id!!)
            .get()
            .addOnSuccessListener { document->
                val oldProduct = document.toObject(MyCartModel::class.java)
                if (oldProduct !=null){
                    val newQty = oldProduct.productQuantity.toString().toInt() + 1
                    if(firebaseUser.uid != null){
                        db.collection("ShoppingCart")
                            .document(firebaseUser.uid)
                            .collection("products")
                            .document(product.id.toString())
                            .update("productQuantity",newQty.toString())
                            .addOnSuccessListener {
                                Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(context,"Success", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                progresDialog.dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,"Error writing document by$e", Toast.LENGTH_SHORT).show()
                progresDialog.dismiss()
            }
    }


}