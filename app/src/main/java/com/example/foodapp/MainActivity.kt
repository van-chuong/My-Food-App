package com.example.foodapp

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.replace
import com.bumptech.glide.Glide
import com.example.foodapp.activity.LoginActivity
import com.example.foodapp.databinding.ActivityMainBinding
import com.example.foodapp.model.User
import com.example.foodapp.ui.mycart.MyCartFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    //FireBaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //FireBaseUser
    private lateinit var firebaseUser: FirebaseUser

    //FireBaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Configure
        db = FirebaseFirestore.getInstance()
        setSupportActionBar(binding.appBarMain.toolbar)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        if (firebaseUser.uid == null) {
            recreate()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navView.visibility = View.GONE
        db.collection("Users")
            .document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        if (user.role == true) {
                            binding.navView.menu.removeItem(R.id.nav_daily_meal)
                            binding.navView.menu.removeItem(R.id.nav_favourite)
                            binding.navView.menu.removeItem(R.id.nav_mycart)
                            Log.d("TAG", user.role.toString())
                            navView.visibility = View.VISIBLE
                        } else if (user.role == false) {
                            binding.navView.menu.removeItem(R.id.nav_dashboard)
                            binding.navView.menu.removeItem(R.id.nav_manager_categories)
//                            binding.navView.menu.removeItem(R.id.nav_manager_daily_meal)
                            binding.navView.menu.removeItem(R.id.nav_manager_product)
                            binding.navView.menu.removeItem(R.id.nav_manager_order)
                            binding.navView.menu.removeItem(R.id.nav_manager_user)
                            binding.navView.menu.removeItem(R.id.nav_manager_review)
                            navView.visibility = View.VISIBLE
                        }
                    }

                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.nameTV).text = firebaseUser.displayName
        headerView.findViewById<TextView>(R.id.emailTV).text = firebaseUser.email
        val profileIV = headerView.findViewById<ImageView>(R.id.profileIV)
        if (firebaseUser.photoUrl != null) {
            Glide.with(this).load(firebaseUser.photoUrl).into(profileIV)
        }
        Log.d("TAG", firebaseUser.photoUrl.toString())

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home,
            R.id.nav_daily_meal,
            R.id.nav_favourite,
            R.id.nav_mycart,
            R.id.nav_dashboard,
            R.id.nav_manager_categories,
//            R.id.nav_manager_daily_meal,
            R.id.nav_manager_product,
            R.id.nav_manager_order,
            R.id.nav_manager_user,
            R.id.nav_manager_review), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun logout(view: android.view.View) {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}