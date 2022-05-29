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
import com.example.foodapp.helper.OnItemSearchClickListener
import com.example.foodapp.model.ProductModel
import com.example.foodapp.model.ReviewModel
import com.makeramen.roundedimageview.RoundedImageView

class ReviewsAdapter (val context: Context, val list: ArrayList<ReviewModel>) :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val img: RoundedImageView = view.findViewById(R.id.roundedImageView)
        val rating: TextView = view.findViewById(R.id.rating)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val content: TextView = view.findViewById(R.id.content)
        val date: TextView = view.findViewById(R.id.date)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.review_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if(list[position].image == null || list[position].image == "null" || list[position].image == ""){
        }else{
            Glide.with(context).load(list[position].image).into(viewHolder.img)
        }
        viewHolder.name.text = list.get(position).name.toString()
        viewHolder.rating.text = "${list.get(position).rating.toString().toFloat()}"
        viewHolder.ratingBar.rating = list.get(position).rating!!.toFloat()
        viewHolder.date.text = "${list.get(position).date}"
        viewHolder.content.text = "${list.get(position).content}"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }
}