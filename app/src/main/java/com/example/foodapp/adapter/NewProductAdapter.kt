package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.helper.OnItemDetailDailyMeal
import com.example.foodapp.model.ProductModel

class NewProductAdapter(val context: Context, val list: ArrayList<ProductModel>?, val listener: OnItemDetailDailyMeal) :
    RecyclerView.Adapter<NewProductAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.detail_name)
        val imageView: ImageView = view.findViewById(R.id.detail_img)
        val rating: TextView = view.findViewById(R.id.detail_rating)
        val desTV: TextView = view.findViewById(R.id.detail_description)
        val timing: TextView = view.findViewById(R.id.detail_timing)
        val price: TextView = view.findViewById(R.id.detail_price)
        val addToCartBtn: Button = view.findViewById(R.id.add_to_cart_btn)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.new_product_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list!!.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).name
        viewHolder.desTV.text = list.get(position).description
        viewHolder.rating.text = list.get(position).rating
        viewHolder.timing.text = list.get(position).timing
        viewHolder.price.text = list.get(position).price
        viewHolder.addToCartBtn.setOnClickListener {
            listener.onAddToCartClick(position)
        }
        viewHolder.itemView.setOnClickListener {
            listener.onProductClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (list != null) {
            return list.size
        }
        return 0
    }
}