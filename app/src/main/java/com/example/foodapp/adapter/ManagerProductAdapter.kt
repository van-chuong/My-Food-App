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
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.ProductModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManagerProductAdapter(val context: Context, val list: ArrayList<ProductModel>, val listener: OnItemViewManagerClick) :
    RecyclerView.Adapter<ManagerProductAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.product_name)
        val imageView: ImageView = view.findViewById(R.id.product_img)
        val timing: TextView = view.findViewById(R.id.product_timing)
        val sale: TextView = view.findViewById(R.id.product_sale)
        val qty: TextView = view.findViewById(R.id.product_qty)
        val price: TextView = view.findViewById(R.id.product_price)
        val deleteBtn: FloatingActionButton = view.findViewById(R.id.delete_btn)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.manager_product_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).name.toString()
        viewHolder.timing.text = list.get(position).timing.toString()
        viewHolder.qty.text = "In Stock : ${list.get(position).quantity.toString()}"
        viewHolder.sale.text = "Sales : ${list.get(position).sales.toString()}"
        viewHolder.price.text = "$${list.get(position).price.toString().toDouble().toString()}"
        viewHolder.itemView.setOnClickListener {
            listener.OnItemClick(position)
        }
        viewHolder.deleteBtn.setOnClickListener {
            listener.OnDeleteClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }
}