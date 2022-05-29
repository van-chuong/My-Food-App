package com.example.foodapp.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.foodapp.fragment.*


class DetailProductFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                                   private val productId:String) :
    FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }
    public fun setData()
    {

    }
    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> {
                return ReviewFragment().newInstance(productId)
            }
            else -> {
//                val bundle = Bundle()
//                val fragment = DescriptionFragment()
//                bundle.putString("product_des", "123")
//                fragment.arguments = bundle
//                fragment.enterTransition
                return DescriptionFragment().newInstance(productId)!!
            }
        }
    }
}