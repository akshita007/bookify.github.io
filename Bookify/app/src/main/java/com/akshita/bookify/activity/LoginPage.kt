package com.akshita.bookify.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.akshita.bookify.R
import com.akshita.bookify.util.NetworkManager
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {
    private lateinit var etEmail:EditText
    private lateinit var etPassword:EditText
    private lateinit var btnLogin:Button
    private lateinit var txtForgetPassword:TextView
    private lateinit var txtRegister:TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@LoginPage, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btn_login)
        txtForgetPassword = findViewById(R.id.txtForgetPassword)
        txtRegister = findViewById(R.id.txtRegister)
        auth = FirebaseAuth.getInstance()
        //check network connectivity
        if (NetworkManager().checkConnectivity(this)) {
            //when click login btn
            btnLogin.setOnClickListener {
                val inputEmail = etEmail.text.toString()
                val inputPassword = etPassword.text.toString()
                if (inputEmail !== "" && inputPassword !== "") {
                    savePreferences()
                    sharedPreferences.edit().putString("Email", inputEmail).apply()
                    auth.signInWithEmailAndPassword(inputEmail, inputPassword)
                        .addOnCompleteListener(this) { task ->
                            //signIn successful,update UI with new user
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Successfully Logged In!", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this@LoginPage, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "LogIn Failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Fill complete credentials!", Toast.LENGTH_SHORT).show()
                }
            }
            //when click forget password text
            txtForgetPassword.setOnClickListener {
                val intent = Intent(this@LoginPage, ForgetPasswordPage::class.java)
                startActivity(intent)
            }
            //when click on signup text
            txtRegister.setOnClickListener {
                val intent = Intent(this@LoginPage, RegisterPage::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            val dialog = AlertDialog.Builder(this@LoginPage as Context)
            dialog.setMessage("No Internet connection found").setTitle("OOPS!")
            dialog.setPositiveButton("Open settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
            this@LoginPage.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this@LoginPage as Activity)
            }
            dialog.create()
            dialog.show()
    }
    }
    private fun savePreferences(){
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
    }
}