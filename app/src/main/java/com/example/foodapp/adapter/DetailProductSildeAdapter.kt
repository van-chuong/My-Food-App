package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.model.ProductImageModel
import com.smarteist.autoimageslider.SliderViewAdapter

class DetailProductSildeAdapter(val context: Context, val list: ArrayList<ProductImageModel>):
    SliderViewAdapter<DetailProductSildeAdapter.ViewHolder>() {
    class ViewHolder(view: View): SliderViewAdapter.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.slide_img)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.product_img_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, position: Int) {
        Glide.with(context).load(list[position].image).into(viewHolder!!.imageView)
    }
}