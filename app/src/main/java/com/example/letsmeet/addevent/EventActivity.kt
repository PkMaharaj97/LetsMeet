package com.example.letsmeet.addevent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.letsmeet.R

class EventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        supportActionBar?.hide()

    }
}
