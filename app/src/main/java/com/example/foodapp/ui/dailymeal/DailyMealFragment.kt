package com.example.foodapp.ui.dailymeal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.activity.DetailDailyMealActivity
import com.example.foodapp.adapter.DailyMealAdapter
import com.example.foodapp.databinding.FragmentDailyMealBinding
import com.example.foodapp.model.DailyMealModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class DailyMealFragment : Fragment(), DailyMealAdapter.OnItemClickListener {
    private var _binding: FragmentDailyMealBinding? = null
    private var db: FirebaseFirestore? = null
    private var dailyMealAdapter: DailyMealAdapter? = null
    private var dailyMealRecyclerView: RecyclerView? = null
    private var dailyMealModelList: ArrayList<DailyMealModel>? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDailyMealBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.progressBar.visibility = View.VISIBLE
        binding.dailyRV.visibility = View.GONE
        val items: ArrayList<DailyMealModel> = ArrayList()
        db = FirebaseFirestore.getInstance()
        var process:Int = 0
        db!!.collection("DailyMeal")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    process++
                    val item = document.toObject<DailyMealModel>()
                    items.add(item)
                    dailyMealAdapter?.notifyDataSetChanged()
                    if(process==4 || result.size() < 4){
                        binding.progressBar.visibility = View.GONE
                        binding.dailyRV.visibility = View.VISIBLE
                    }
                }
            }
        //Show Home HorizontalRecyclerView Items
        dailyMealModelList = items
        dailyMealAdapter = DailyMealAdapter(activity!!, dailyMealModelList!!,this)
        dailyMealRecyclerView = root.findViewById(R.id.dailyRV)
        dailyMealRecyclerView!!.layoutManager = GridLayoutManager(activity,1, GridLayoutManager.VERTICAL, false)
        dailyMealRecyclerView!!.setHasFixedSize(true)
        dailyMealRecyclerView?.adapter = dailyMealAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(context, DetailDailyMealActivity::class.java)
        intent.putExtra("DetailDailyMealImage",dailyMealModelList!!.get(position).image)
        intent.putExtra("DetailDailyMealName","${dailyMealModelList!!.get(position).name} Menu")
        intent.putExtra("categorydailymeals",
            dailyMealModelList!!.get(position).name!!.replace("\\s".toRegex(), "").lowercase())
        startActivity(intent)
    }
}