package com.akshita.bookify.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.akshita.bookify.R
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordPage : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var etEmail:EditText
    lateinit var btnSend:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password_page)
        etEmail=findViewById(R.id.etEmail)
        auth=FirebaseAuth.getInstance()
        btnSend=findViewById(R.id.btnSend)
        btnSend.setOnClickListener {
            val inputEmail=etEmail.text.toString()
            if (TextUtils.isEmpty(inputEmail)){
                Toast.makeText(this,"Enter valid email!",Toast.LENGTH_SHORT).show()
            }
            else{
                auth.sendPasswordResetEmail(inputEmail)
                    .addOnCompleteListener{ task->
                        if (task.isSuccessful){
                            Toast.makeText(this@ForgetPasswordPage,"Check email to reset your Password!",Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@ForgetPasswordPage,"Fail to send reset Password email!",Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}