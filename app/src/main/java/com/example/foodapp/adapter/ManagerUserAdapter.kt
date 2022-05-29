package com.example.foodapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.OrderModel
import com.example.foodapp.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ManagerUserAdapter(val context: Context, val list: ArrayList<User>, val listener: OnItemViewManagerClick) :
RecyclerView.Adapter<ManagerUserAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.user_name)
        val email: TextView = view.findViewById(R.id.user_email)
        val create: TextView = view.findViewById(R.id.created)
        val image: ImageView = view.findViewById(R.id.user_img)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.manager_user_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = list.get(position).name.toString()
        viewHolder.email.text = list.get(position).email.toString()
        viewHolder.create.text = "Create at : ${list.get(position).created.toString()}"
        if(list[position].image != null && list[position].image != "" && list[position].image != "null"  ){
            Glide.with(context).load(list[position].image).into(viewHolder.image)
        }
        viewHolder.itemView.setOnClickListener {
            listener.OnItemClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }
}