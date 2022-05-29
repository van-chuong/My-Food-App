package com.example.foodapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.helper.OnApprovalClickListener
import com.example.foodapp.model.ReviewModel
import com.google.android.material.button.MaterialButton
import com.makeramen.roundedimageview.RoundedImageView

class ManagerReviewAdapter (val context: Context, val list: ArrayList<ReviewModel>,val listener:OnApprovalClickListener) :
    RecyclerView.Adapter<ManagerReviewAdapter.ViewHolder>() {

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
        val button: MaterialButton = view.findViewById(R.id.approval_btn)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.manager_review_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
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
        if(list[position].active == false){
            viewHolder.button.backgroundTintList = context.resources.getColorStateList(R.color.greennew)
            viewHolder.button.text = "Approval"
        }else{
            viewHolder.button.backgroundTintList = context.resources.getColorStateList(R.color.red)
            viewHolder.button.text = "Disapproval"
        }
        viewHolder.button.setOnClickListener {
            if(list[position].active == true){
                viewHolder.button.backgroundTintList = context.resources.getColorStateList(R.color.greennew)
                viewHolder.button.text = "Approval"
            }else{
                viewHolder.button.backgroundTintList = context.resources.getColorStateList(R.color.red)
                viewHolder.button.text = "Disapproval"
            }
            listener.onButtonClick(position,list[position].active)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }

}