package com.example.foodapp.ui.mycart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.activity.CheckoutActivity
import com.example.foodapp.adapter.MyCartAdapter
import com.example.foodapp.databinding.FragmentMyCartBinding
import com.example.foodapp.helper.OnItemClickListener
import com.example.foodapp.model.MyCartModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class MyCartFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentMyCartBinding ? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db: FirebaseFirestore? = null
    private var myCartAdapter: MyCartAdapter? = null
    private var mycartRecyclerView: RecyclerView? = null
    private var myCartModelList: ArrayList<MyCartModel>? = null
    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        //Place order btn
        binding.placeOrderBtn.setOnClickListener{
            startActivity(Intent(activity,CheckoutActivity::class.java))
        }
        binding.itemsTotal.visibility = View.GONE
        binding.totalPrice.visibility = View.GONE
        binding.myCartRv.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        val items: ArrayList<MyCartModel> = ArrayList()
        db = FirebaseFirestore.getInstance()
        var process:Int = 0
        db!!.collection("ShoppingCart")
            .document(firebaseUser.uid)
            .collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    process++
                    val item = document.toObject<MyCartModel>()
                    items.add(item)
                    myCartAdapter?.notifyDataSetChanged()
                }
                updateCart(items)
                binding.itemsTotal.visibility = View.VISIBLE
                binding.totalPrice.visibility = View.VISIBLE
                binding.myCartRv.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                if(result.size()==0){
                    binding.emptyCartTv.visibility = View.VISIBLE
                    binding.myCartRv.visibility = View.GONE
                }
            }
        //Show Cart Items
        myCartModelList = items
        myCartAdapter = MyCartAdapter(activity!!, myCartModelList!!,this)
        mycartRecyclerView = root.findViewById(R.id.my_cart_rv)
        mycartRecyclerView!!.layoutManager = GridLayoutManager(activity,1, GridLayoutManager.VERTICAL, false)
        mycartRecyclerView!!.setHasFixedSize(true)
        mycartRecyclerView?.adapter = myCartAdapter

        // Inflate the layout for this fragment
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCart(myCartModelList: ArrayList<MyCartModel>?){
        var itemTotal:Int?=0
        for(list in myCartModelList!!){
            itemTotal = itemTotal?.plus(list.productPrice!!.toInt() * list.productQuantity!!.toInt())
        }
        binding.itemsTotal.text = "$ ${itemTotal.toString().toDouble()}"
        binding.totalPrice.text = "$ ${itemTotal.toString().toDouble()}"
    }

    override fun onAddClick(position: Int) {
        val product=myCartModelList!!.get(position)
        var newQty:Int ?=0
        newQty= product.productQuantity!!.toInt()+1
        db!!.collection("ShoppingCart")
            .document(firebaseUser.uid)
            .collection("products")
            .document(product.productId.toString())
            .update("productQuantity",newQty.toString())
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
        product.productQuantity = newQty.toString()
        myCartModelList!!.set(position, product)
        myCartAdapter!!.notifyItemChanged(position)

        updateCart(myCartModelList)
    }

    override fun onMinusClick(position: Int) {
        val product=myCartModelList!!.get(position)
        var newQty:Int ?=0
        if(product.productQuantity!!.toInt()>0){
            newQty = product.productQuantity!!.toInt()-1
        }else{
            newQty = 0
        }
        db!!.collection("ShoppingCart")
            .document(firebaseUser.uid)
            .collection("products")
            .document(product.productId.toString())
            .update("productQuantity",newQty.toString())
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error updating document", e) }
        product.productQuantity = newQty.toString()
        if(newQty == 0){
            myCartModelList!!.removeAt(position)
            db!!.collection("ShoppingCart")
                .document(firebaseUser.uid)
                .collection("products")
                .document(product.productId.toString())
                .delete()
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
        }else{
            myCartModelList!!.set(position, product)
        }
        myCartAdapter!!.notifyItemChanged(position)
        updateCart(myCartModelList)
    }

}