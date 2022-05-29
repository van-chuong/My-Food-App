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
import com.example.foodapp.helper.OnItemSearchClickListener
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.ProductModel
import com.example.foodapp.model.User

class SearchAdapter(val context: Context, val list: ArrayList<ProductModel>, val listener: OnItemSearchClickListener) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.pro_name)
        val img: ImageView = view.findViewById(R.id.pro_img)
        val price: TextView = view.findViewById(R.id.pro_price)
        val type: TextView = view.findViewById(R.id.pro_type)
        val sales: TextView = view.findViewById(R.id.pro_sales)
        val timing:TextView  = view.findViewById(R.id.pro_timing)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.search_product_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position].image).into(viewHolder.img)
        viewHolder.name.text = list.get(position).name.toString()
        viewHolder.price.text = "$ ${list.get(position).price.toString().toFloat().toString()}"
        viewHolder.type.text = list.get(position).type.toString()
        viewHolder.sales.text = "Sales : ${list.get(position).sales}"
        viewHolder.timing.text = "Timing : ${list.get(position).timing.toString()}"
        viewHolder.itemView.setOnClickListener {
            listener.OnItemClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }
}