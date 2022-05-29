package com.example.foodapp.adapter


import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.foodapp.fragment.FeaturedFragment
import com.example.foodapp.fragment.NewFragment
import com.example.foodapp.fragment.PopularFragment

class FavouriteFragmentAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return PopularFragment()
            2 -> return NewFragment()
            else -> { // Note the block
                return FeaturedFragment()
            }
        }
    }
}