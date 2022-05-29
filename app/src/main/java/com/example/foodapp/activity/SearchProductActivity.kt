package com.example.foodapp.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.adapter.SearchAdapter
import com.example.foodapp.databinding.ActivityRegistrationBinding
import com.example.foodapp.databinding.ActivitySearchProductBinding
import com.example.foodapp.helper.OnItemSearchClickListener
import com.example.foodapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import kotlin.collections.ArrayList

class SearchProductActivity : AppCompatActivity(), OnItemSearchClickListener {
    //ViewBinding
    private lateinit var binding: ActivitySearchProductBinding
    //ActionBar
    private lateinit var actionBar: ActionBar
    //ProgressDiaLog
    private lateinit var progressDiaLog: ProgressDialog
    //FireBaseDatabase
    private lateinit var db: FirebaseFirestore
    //
    private lateinit var searchAdapter: SearchAdapter
    //
    private lateinit var productModelList: ArrayList<ProductModel>
    //
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar = supportActionBar!!
        actionBar.hide()
        recyclerView = binding.searchRv
        db = FirebaseFirestore.getInstance()
        recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        binding.inputSearch.editText!!.doOnTextChanged { text, start, before, count ->
            val searchText = binding.inputSearch.editText!!.text.trim().toString().capitalizeWords()
            loadData(searchText)
        }
        //Back btn
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }
    private fun loadData(searchText:String){
        val list = ArrayList<ProductModel>()
        if(searchText.isEmpty()){
            binding.countResult.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }else{
            db.collection("AllProducts")
                .orderBy("name")
                .startAt(searchText)
                .endAt("$searchText\uf8ff")
                .get()
                .addOnSuccessListener { it->
                    for(i in it){
                        val item = i.toObject<ProductModel>()
                        list.add(item)
                        Log.d("TAG",item.name.toString())
                    }
                    productModelList = list
                    recyclerView.visibility = View.VISIBLE
                    binding.countResult.visibility = View.VISIBLE
                    binding.countResult.text = "There were ${list.size} results found"
                    searchAdapter = SearchAdapter(this ,productModelList,this)
                    recyclerView.adapter = searchAdapter
                }
        }


    }
    fun String.capitalizeWords() = split(' ').joinToString(" ", transform = String::capitalize)

    override fun OnItemClick(position: Int) {
        val intent = Intent(this, DetailProductActivity::class.java)
        intent.putExtra("productId", productModelList.get(position).id)
        startActivity(intent)
    }
}