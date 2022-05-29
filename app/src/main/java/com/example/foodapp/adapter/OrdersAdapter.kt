package com.example.foodapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.helper.OnItemHorHomeClickListener
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.OrderModel

class OrdersAdapter (val context: Context, val list: ArrayList<OrderModel>, val listener: OnItemHorHomeClickListener) :
    RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.order_name)
        val total: TextView = view.findViewById(R.id.order_total)
        val address: TextView = view.findViewById(R.id.order_address)
        val status: TextView = view.findViewById(R.id.order_status)
        val date: TextView = view.findViewById(R.id.order_date)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.orders_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.name.text = list.get(position).name.toString()
        viewHolder.total.text = "Total : $${list.get(position).total.toString().toDoubleOrNull().toString()}"
        viewHolder.address.text = list.get(position).address.toString()
        viewHolder.date.text = "Order day : ${list.get(position).date}"
        when {
            list.get(position).status.toString()=="Pending" -> {
                viewHolder.status.setTextColor(Color.parseColor("#ffc107"))
            }
            list.get(position).status.toString()=="Being delivery" -> {
                viewHolder.status.setTextColor(Color.parseColor("#17a2b8"))
            }
            list.get(position).status.toString()=="Canceled" -> {
                viewHolder.status.setTextColor(Color.parseColor("#FF0000"))
            }
            else -> {
                viewHolder.status.setTextColor(Color.parseColor("#28a745"))
            }
        }
        viewHolder.status.text = list.get(position).status.toString()
        viewHolder.itemView.setOnClickListener {
            listener.OnItemClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }
}