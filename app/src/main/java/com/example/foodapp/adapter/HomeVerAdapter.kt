package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.OnReceiveContentListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.helper.OnItemClickListener
import com.example.foodapp.helper.OnOpenBottomSheetListener
import com.example.foodapp.model.ProductModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeVerAdapter(val context: Context, val list: ArrayList<ProductModel>, val listener: OnOpenBottomSheetListener) :
    RecyclerView.Adapter<HomeVerAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.productNameTV)
        val imageView: ImageView = view.findViewById(R.id.ver_img)
        val rating: TextView = view.findViewById(R.id.rating)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val timing: TextView = view.findViewById(R.id.timing)
        val price: TextView = view.findViewById(R.id.price)
        val openBottomSheetBtn: FloatingActionButton = view.findViewById(R.id.open_bottom_sheet)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.home_vertical_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).name
        viewHolder.rating.text = list.get(position).rating
        viewHolder.ratingBar.rating = list.get(position).rating!!.toFloat()
        viewHolder.timing.text = list.get(position).timing
        viewHolder.price.text = "$${list[position].price.toString().toDouble()}"
        viewHolder.openBottomSheetBtn.setOnClickListener {
            listener.onOpenBottomSheetClick(position)
        }
        viewHolder.itemView.setOnClickListener {
            listener.onProductClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }

}
