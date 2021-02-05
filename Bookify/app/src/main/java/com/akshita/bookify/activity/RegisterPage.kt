package com.akshita.bookify.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.akshita.bookify.R
import com.akshita.bookify.util.NetworkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterPage : AppCompatActivity() {
    private lateinit var etUserName: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etPhoneNo: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSignUp: Button
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var auth:FirebaseAuth
    private var phoneNo="^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}\$".toRegex()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@RegisterPage, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        etUserName = findViewById(R.id.etUsername)
        etPhoneNo = findViewById((R.id.etPhoneNo))
        etEmail = findViewById(R.id.etEmail)
        btnSignUp = findViewById(R.id.btn_SignUp)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        auth = Firebase.auth
        //check network
        if (NetworkManager().checkConnectivity(this)) {
            //when click SignUp btn
            btnSignUp.setOnClickListener {
                val inputUserName = etUserName.text.toString()
                val inputPhoneNo = etPhoneNo.text.toString()
                val inputEmail = etEmail.text.toString()
                val inputPassword = etPassword.text.toString()
                val inputConfirmPassword = etConfirmPassword.text.toString()
                if (!(inputUserName === "" && inputPhoneNo === "" && inputEmail === "" && inputPassword === "" && inputConfirmPassword === "")) {
                    if (inputPassword.length < 6) {
                        Toast.makeText(
                            this,
                            "Password is too short,enter minimum 6 characters!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else if(!inputPhoneNo.matches(phoneNo)){
                        Toast.makeText(this,"Phone number entered is not valid!",Toast.LENGTH_SHORT).show()
                    }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()){
                        Toast.makeText(this,"Email entered is not valid!",Toast.LENGTH_SHORT).show()
                    }
                    else if (inputPassword == inputConfirmPassword) {
                        saveSharedPreferences()
                        sharedPreferences.edit().putString("UserName", inputUserName).apply()
                        sharedPreferences.edit().putString("PhoneNo", inputPhoneNo).apply()
                        sharedPreferences.edit().putString("Email", inputEmail).apply()
                        registerNewUser(inputEmail, inputPassword)
                    }
                } else {
                    Toast.makeText(this, "Enter complete credentials!", Toast.LENGTH_SHORT).show()
                }
            }
        } else{
        val dialog = AlertDialog.Builder(this@RegisterPage as Context)
        dialog.setMessage("No Internet connection found").setTitle("OOPS")
        dialog.setPositiveButton("Open settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
            this@RegisterPage.finish()
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this@RegisterPage as Activity)

        }
        dialog.create()
        dialog.show()
    }
}
    private fun registerNewUser(email: String, password: String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                //signIn successful,update UI with new user
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterPage, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration Failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun saveSharedPreferences(){
        sharedPreferences.edit().putBoolean("isLogged",true).apply()
    }
}