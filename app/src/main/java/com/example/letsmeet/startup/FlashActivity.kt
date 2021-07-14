package com.example.letsmeet.startup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.letsmeet.R

class FlashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash)
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            startActivity(Intent(this, Login::class.java))

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)

    }
}
