package com.example.foodapp.ui.usermanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.activity.admin.user.UpdateUserActivity
import com.example.foodapp.adapter.ManagerUserAdapter
import com.example.foodapp.databinding.FragmentUserManagementBinding
import com.example.foodapp.helper.OnItemViewManagerClick
import com.example.foodapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UserManagementFragment : Fragment(), OnItemViewManagerClick {
    private var _binding: FragmentUserManagementBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private var userManagementAdapter: ManagerUserAdapter? = null
    private var userRecyclerView: RecyclerView? = null
    private var userModelList: ArrayList<User>? = null
    private var allowRefresh = false

    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser

    private val now = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        userRecyclerView = binding.managerUserRv
        displayUserList()
        binding.cardNewUser.setOnClickListener {
            displayNewUser()
        }
        binding.cardUser.setOnClickListener {
            binding.title.text = "Users List"
            displayUserList()
        }
        val items1: ArrayList<User> = ArrayList()
        db.collection("Users")
            .orderBy("created", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["created"] == now && document["role"] == false){
                        val item = document.toObject(User::class.java)
                        items1.add(item)
                    }
                }
                binding.newUser.text = items1.size.toString()
            }
        // Inflate the layout for this fragment
        return binding.root
    }
    private fun displayNewUser(){
        val items1: ArrayList<User> = ArrayList()
        db.collection("Users")
            .orderBy("created", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if(document["created"] == now && document["role"] == false){
                        val item = document.toObject(User::class.java)
                        items1.add(item)
                    }
                }
                binding.title.text = "New Users"
                userModelList = items1
                userManagementAdapter = ManagerUserAdapter(activity!!, userModelList!!, this)
                userRecyclerView!!.layoutManager =
                    GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
                userRecyclerView!!.setHasFixedSize(true)
                userRecyclerView?.adapter = userManagementAdapter
            }
    }

    private fun displayUserList(){
        val items: ArrayList<User> = ArrayList()
        db.collection("Users")
            .orderBy("created", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject(User::class.java)
                    if (document["role"] == false) {
                        items.add(item)
                    }
                }
                binding.user.text = items.size.toString()
                userModelList = items
                userManagementAdapter = ManagerUserAdapter(activity!!, userModelList!!, this)
                userRecyclerView!!.layoutManager =
                    GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
                userRecyclerView!!.setHasFixedSize(true)
                userRecyclerView?.adapter = userManagementAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    override fun OnItemClick(position: Int) {
        val intent = Intent(context, UpdateUserActivity::class.java)
        intent.putExtra("userId", userModelList!!.get(position).id)
        startActivity(intent)
    }

    override fun OnDeleteClick(position: Int) {
    }

    override fun onResume() {
        super.onResume()
        if (allowRefresh) {
            allowRefresh = false;
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.nav_manager_user)
            }
        }
        Log.d("TAG", "onResume")
    }

    override fun onPause() {
        super.onPause()
        if (!allowRefresh) {
            allowRefresh = true
        }
        Log.d("TAG", "onPause")
    }


}