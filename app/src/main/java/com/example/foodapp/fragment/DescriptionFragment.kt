package com.example.foodapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentDescriptionBinding
import com.example.foodapp.databinding.FragmentFeaturedBinding
import com.example.foodapp.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject


class DescriptionFragment : Fragment() {
    private var _binding: FragmentDescriptionBinding? = null
    private val binding get() = _binding!!
    //
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val productId = arguments?.get("productId")
        db = FirebaseFirestore.getInstance()
        db.collection("AllProducts")
            .document(productId.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                     val item = document.toObject<ProductModel>()
                    if (item != null) {
                        binding.productDesTv.text = item.description
                    }
                } else {
                    Log.d("TAG", "No such document")
                }
            }

        // Inflate the layout for this fragment
        return root
    }
    fun newInstance(productId: String): DescriptionFragment {
        val fragment = DescriptionFragment()
        val args = Bundle()
        args.putString("productId", productId)
        fragment.arguments = args
        return fragment
    }
}