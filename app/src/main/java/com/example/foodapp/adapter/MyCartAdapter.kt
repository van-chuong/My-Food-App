package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.motion.widget.FloatLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.helper.OnItemClickListener
import com.example.foodapp.model.MyCartModel
import com.example.foodapp.model.ProductModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyCartAdapter(val context: Context, val list: ArrayList<MyCartModel>,val listener: OnItemClickListener) :
    RecyclerView.Adapter<MyCartAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.product_name)
        val imageView: ImageView = view.findViewById(R.id.product_img)
        val price: TextView = view.findViewById(R.id.product_price)
        val desTv: TextView = view.findViewById(R.id.product_description)
        val qty: TextView = view.findViewById(R.id.product_qty)
        val minus_btn:FloatingActionButton = view.findViewById(R.id.minus_btn)
        val add_btn:FloatingActionButton = view.findViewById(R.id.add_btn)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.my_cart_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).productName
        viewHolder.price.text = "$${list.get(position).productPrice.toString().toDouble().toString()}"
        viewHolder.qty.text = list.get(position).productQuantity
        viewHolder.desTv.text = list.get(position).description
        viewHolder.minus_btn.setOnClickListener {
            listener.onMinusClick(position)
        }
        viewHolder.add_btn.setOnClickListener {
            listener.onAddClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }

}
