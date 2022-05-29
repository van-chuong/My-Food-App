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
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.HomeHorModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManagerCategoryAdapter(val context: Context, val list: ArrayList<HomeHorModel>, val listener: OnItemViewManagerClick) :
    RecyclerView.Adapter<ManagerCategoryAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.category_name)
        val imageView: ImageView = view.findViewById(R.id.category_img)
        val deleteBtn: FloatingActionButton = view.findViewById(R.id.delete_btn)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.manager_category_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).name
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