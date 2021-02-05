package com.akshita.bookify.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.akshita.bookify.R

class DrawerHeader : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtUserName:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer_header)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        txtUserName = findViewById(R.id.txtUserName)
        val username=sharedPreferences.getString("UserName","Name")
        //ToDo check how to print the user name on the header
        txtUserName.text = username

    }
}