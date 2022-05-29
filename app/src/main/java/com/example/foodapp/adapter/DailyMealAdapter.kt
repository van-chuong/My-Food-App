package com.example.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.MainActivity
import com.example.foodapp.R
import com.example.foodapp.activity.DetailDailyMealActivity
import com.example.foodapp.model.DailyMealModel
import com.makeramen.roundedimageview.RoundedImageView

class DailyMealAdapter(val context: Context, val list: ArrayList<DailyMealModel>,val listener: DailyMealAdapter.OnItemClickListener) :
    RecyclerView.Adapter<DailyMealAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.findViewById(R.id.daily_name)
        val imageView: RoundedImageView = view.findViewById(R.id.daily_img)
        val desTV: TextView = view.findViewById(R.id.desTV)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position:Int = adapterPosition
            if(position!= RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }


    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.daily_meal_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(list.get(position).image).into(viewHolder.imageView)
        viewHolder.name.text = list.get(position).name
        viewHolder.desTV.text = list.get(position).description
//        viewHolder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailDailyMealActivity::class.java)
//            intent.putExtra("categorydailymeals",
//                list.get(position).name!!.replace("\\s".toRegex(), "").lowercase())
//            context.startActivity(intent)
//        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }

}
