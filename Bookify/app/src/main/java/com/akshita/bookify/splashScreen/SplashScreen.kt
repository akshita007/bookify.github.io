package com.akshita.bookify.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akshita.bookify.activity.LoginPage
import com.akshita.bookify.R

class SplashScreen : AppCompatActivity() {
    private var handler=android.os.Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handler.postDelayed( {
            val startAct= Intent(this@SplashScreen,
                LoginPage::class.java)
            startActivity(startAct)
            finish()
        },2000)

    }
}