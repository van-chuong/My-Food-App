package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.model.FeaturedSlideModel

class FeaturedHorAdapter(val context: Context, val list: ArrayList<FeaturedSlideModel>) :
    RecyclerView.Adapter<FeaturedHorAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.featured_name)
        val imageView: ImageView = view.findViewById(R.id.featured_img)
        val desTV: TextView = view.findViewById(R.id.featured_des)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.featured_hor_item, viewGroup, false)
        return FeaturedHorAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).name
        viewHolder.desTV.text = list.get(position).description
    }

    override fun getItemCount(): Int {
        return list.size
    }


}