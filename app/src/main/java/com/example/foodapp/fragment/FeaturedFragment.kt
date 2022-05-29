package com.example.foodapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.adapter.FeaturedHorAdapter
import com.example.foodapp.adapter.HomeHorAdapter
import com.example.foodapp.databinding.FragmentFavouriteBinding
import com.example.foodapp.databinding.FragmentFeaturedBinding
import com.example.foodapp.databinding.FragmentHomeBinding
import com.example.foodapp.model.FeaturedSlideModel
import com.example.foodapp.model.HomeHorModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class FeaturedFragment : Fragment() {
    private var _binding: FragmentFeaturedBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var db: FirebaseFirestore? = null
    private var featuredHorAdapter:FeaturedHorAdapter ?= null
    private var featuredSlideModelList:ArrayList<FeaturedSlideModel> ?= null
    private var featuredRecyclerView:RecyclerView ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeaturedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val items:ArrayList<FeaturedSlideModel> = ArrayList()
        // Get Data and Add to ArrayList
        db = FirebaseFirestore.getInstance()
        var process1:Int = 0
        db!!.collection("FeaturedSlides")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    process1 ++
                    val item = document.toObject<FeaturedSlideModel>()
                    items.add(item)
                    featuredHorAdapter?.notifyDataSetChanged()
                    if(process1==4 || result.size() < 4){
                    }
                }
            }
        //Show Home HorizontalRecyclerView Items
        featuredSlideModelList = items
        featuredHorAdapter = FeaturedHorAdapter(context!!, featuredSlideModelList!!)
        featuredRecyclerView = binding.featuredHorRv
        featuredRecyclerView!!.layoutManager = LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        featuredRecyclerView!!.setHasFixedSize(true)
        featuredRecyclerView?.adapter = featuredHorAdapter

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}