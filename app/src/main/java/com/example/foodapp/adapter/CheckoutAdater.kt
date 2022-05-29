package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.model.FeaturedSlideModel
import com.example.foodapp.model.MyCartModel

class CheckoutAdater(val context: Context, val list: ArrayList<MyCartModel>) :
    RecyclerView.Adapter<CheckoutAdater.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.product_name)
        val price: TextView = view.findViewById(R.id.product_price)
        val qty: TextView = view.findViewById(R.id.product_qty)
        val itemTotal: TextView = view.findViewById(R.id.product_item_total)
        val imageView: ImageView = view.findViewById(R.id.product_img)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.checkout_item, viewGroup, false)
        return CheckoutAdater.ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).productName
        viewHolder.price.text = list.get(position).productPrice.toString().toDouble().toString()
        viewHolder.qty.text = list.get(position).productQuantity
        viewHolder.itemTotal.text = (list.get(position).productPrice.toString().toInt()* list.get(position).productQuantity.toString().toInt()).toString().toDouble().toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}