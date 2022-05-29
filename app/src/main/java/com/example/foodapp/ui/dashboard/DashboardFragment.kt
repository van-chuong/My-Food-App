package com.example.foodapp.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.activity.DetailProductActivity
import com.example.foodapp.adapter.DetailDailyMealAdapter
import com.example.foodapp.adapter.OrdersAdapter
import com.example.foodapp.adapter.TopSalesAdapter
import com.example.foodapp.databinding.FragmentDashboardBinding
import com.example.foodapp.helper.OnItemSearchClickListener
import com.example.foodapp.model.ProductModel
import com.example.foodapp.model.StatisticalModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

import kotlin.collections.ArrayList


class DashboardFragment : Fragment(), OnItemSearchClickListener {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    //FirebaseFirestore
    private lateinit var db: FirebaseFirestore

    //LineChart
    private lateinit var lineChart: LineChart
    //Pie Chart
    private lateinit var pieChart: PieChart
    //
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TopSalesAdapter
    private lateinit var modelList: ArrayList<ProductModel>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //
        db = FirebaseFirestore.getInstance()
        lineChart = binding.lineChart
        pieChart = binding.pieChart
        recyclerView = binding.topSalesRv
        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        loadTopSales()
        setLineChartValue()
        setPieChartValue()
        return root
    }

    private fun loadTopSales() {
        val list = ArrayList<ProductModel>()
        db.collection("AllProducts")
            .orderBy("sales",Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { it->
                for(i in it){
                    val item = i.toObject<ProductModel>()
                    list.add(item)
                }
                modelList = list
                adapter = TopSalesAdapter(context!!,modelList,this)
                recyclerView.adapter = adapter
            }
    }

    private fun setLineChartValue() {
        db.collection("Statistical")
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val xvalue = ArrayList<String>()
                val lineEntry = ArrayList<Entry>()
                var index = 0
                for (i in documents) {
                    val item = i.toObject<StatisticalModel>()
                    xvalue.add(item.date.toString())
                    lineEntry.add(Entry(i["profit"].toString().toFloat(), index++))
                    Log.d("TAG", i["profit"].toString().toFloat().toString())
                }
                val lineDataSet = LineDataSet(lineEntry, "Profits")
                lineDataSet.valueTextSize = 15f
                lineDataSet.color = Color.GREEN
                lineDataSet.circleRadius = 0f
                lineDataSet.setDrawFilled(true)
                lineDataSet.fillColor = Color.BLUE
                lineDataSet.fillAlpha = 30
                val data = LineData(xvalue, lineDataSet)
                lineChart.data = data
                lineChart.setBackgroundColor(Color.WHITE)
                lineChart.animateXY(3000, 3000)
                lineChart.getAxisLeft().setDrawGridLines(false)
                lineChart.getXAxis().setDrawGridLines(false)
                lineChart.setDescription("")
                lineChart.invalidate()
            }

    }
    private fun setPieChartValue(){
        val xvalue = ArrayList<String>()
        xvalue.add("Product")
        xvalue.add("Category")
        xvalue.add("Order")
        xvalue.add("User")
        val entries: ArrayList<Entry> = ArrayList()
        db.collection("AllProducts")
            .get()
            .addOnSuccessListener { it->
                entries.add(Entry(it.documents.size.toFloat(), 0))
                db.collection("HomeCategory")
                    .get()
                    .addOnSuccessListener { it->
                        entries.add(Entry(it.documents.size.toFloat(), 1))
                        db.collection("Orders")
                            .get()
                            .addOnSuccessListener { it->
                                entries.add(Entry(it.documents.size.toFloat(), 2))
                                db.collection("Users")
                                    .get()
                                    .addOnSuccessListener { it->
                                        entries.add(Entry(it.documents.size.toFloat(), 3))
                                        val color = ArrayList<Int>()
                                        color.add(Color.GREEN)
                                        color.add(Color.YELLOW)
                                        color.add(Color.RED)
                                        color.add(Color.BLUE)
                                        val dataSet = PieDataSet(entries,"")
                                        dataSet.colors = color
                                        dataSet.valueTextColor = Color.WHITE
                                        dataSet.valueTextSize = 15f
                                        dataSet.sliceSpace = 3f
                                        val data = PieData(xvalue,dataSet)
                                        pieChart.legend.formSize = 15f
                                        pieChart.data = data
                                        pieChart.holeRadius = 5f
                                        pieChart.setDescription("")
                                        pieChart.transparentCircleRadius = 0f
                                        pieChart.holeRadius = 0.0f
                                        pieChart.setCenterTextSize(35f)
                                        pieChart.invalidate()
                                    }
                            }
                    }
            }




    }

    override fun OnItemClick(position: Int) {
        val intent = Intent(context, DetailProductActivity::class.java)
        intent.putExtra("productId", modelList.get(position).id)
        startActivity(intent)
    }
}