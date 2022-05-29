package com.example.foodapp.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.foodapp.adapter.FavouriteFragmentAdapter
import com.example.foodapp.databinding.FragmentFavouriteBinding
import com.google.android.material.tabs.TabLayout

class FavouriteFragment : Fragment(), TabLayout.OnTabSelectedListener {

    private var _binding: FragmentFavouriteBinding? = null
    private var tabLayout:TabLayout ?= null
    private var viewPager2:ViewPager2 ?= null
    private var fragmentAdapter:FavouriteFragmentAdapter ?= null
    private var fragmentManager:FragmentManager ?= null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fragmentManager = activity?.supportFragmentManager
        fragmentAdapter = FavouriteFragmentAdapter(fragmentManager!!,lifecycle)
        viewPager2 = binding.viewPaper
        viewPager2!!.adapter = fragmentAdapter
        tabLayout = binding.tabLayout
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Featured"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Popular"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("New"))
        tabLayout!!.addOnTabSelectedListener(this)
        viewPager2!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout!!.selectTab(tabLayout!!.getTabAt(position))
            }
        })
        return root
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewPager2!!.currentItem = tab.position
    }
    override fun onTabUnselected(tab: TabLayout.Tab) {

    }
    override fun onTabReselected(tab: TabLayout.Tab) {

    }
}