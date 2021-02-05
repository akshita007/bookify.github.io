package com.akshita.bookify.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.akshita.bookify.R
import com.akshita.bookify.fragment.AboutUsFragment
import com.akshita.bookify.fragment.DashboardFragment
import com.akshita.bookify.fragment.FavsFragment
import com.akshita.bookify.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences:SharedPreferences
    private var previousItem:MenuItem?=null
    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        sharedPreferences=getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        drawerLayout=findViewById(R.id.DrawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        navigationView=findViewById(R.id.NavigationView)
        toolbar=findViewById(R.id.Toolbar)
        auth=FirebaseAuth.getInstance()
        //call function set toolbar and open dashboard by default
        setUpToolbar()
        openDashboard()
        //open and close action on drawer layout
        toggle= ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //on item selected
        navigationView.setNavigationItemSelectedListener {
            if (previousItem!=null){
                previousItem?.isChecked
            }
            it.isCheckable=true
            it.isChecked=true
            previousItem=it
            when(it.itemId){
                R.id.dashboard -> {
                    openDashboard()
                }
                 R.id.favourites -> {
                     supportFragmentManager.beginTransaction()
                         .replace(R.id.FrameLayout, FavsFragment()).commit()
                     drawerLayout.closeDrawers()
                     supportActionBar?.title = "Favourites"                 }
                 R.id.Profile -> {
                     supportFragmentManager.beginTransaction()
                         .replace(R.id.FrameLayout, ProfileFragment()).commit()
                     drawerLayout.closeDrawers()
                     supportActionBar?.title = "User Profile"
                 }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FrameLayout, AboutUsFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "FAQ"                }
                R.id.logOut -> {
                    val dialog=AlertDialog.Builder(this)
                    dialog.setTitle("Sign Out").setMessage("Are you sure you want to sign out?")
                    dialog.setPositiveButton("Yes"){ _, _ ->
                        auth.signOut()
                        val intent = Intent(this@MainActivity, LoginPage::class.java)
                        startActivity(intent)
                        sharedPreferences.edit().clear().apply()
                        finish()
                    }
                    dialog.setNegativeButton("No"){ _, _ ->
                        dialog.setCancelable(true)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
             return@setNavigationItemSelectedListener true
            }
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openDashboard(){
        val fragment=DashboardFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FrameLayout,fragment)
        transaction.commit()
        supportActionBar?.title="Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when(supportFragmentManager.findFragmentById(R.id.FrameLayout)){
            !is DashboardFragment-> openDashboard()
            else->super.onBackPressed()
    }
}
}