package com.example.foodapp.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.helper.OnItemHorHomeClickListener
import com.example.foodapp.model.HomeHorModel

class HomeHorAdapter(val activity:Activity, val list: ArrayList<HomeHorModel>,
                     val listener:OnItemHorHomeClickListener) :
    RecyclerView.Adapter<HomeHorAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.hor_text)
        val imageView: ImageView = view.findViewById(R.id.hor_img)
        val cardView: CardView = view.findViewById(R.id.cardView)
        }
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.home_horizontal_item, viewGroup, false)

        return ViewHolder(view)
    }
    var row_index:Int = 0
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(activity).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).name
        viewHolder.itemView.setOnClickListener {
            row_index=position
            listener.OnItemClick(position)
            notifyDataSetChanged()
        }
        if(row_index==position){
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#FF5353"));
            viewHolder.name.setTextColor(Color.parseColor("#ffffff"));
        }
        else
        {
            viewHolder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            viewHolder.name.setTextColor(Color.parseColor("#000000"));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }
}



