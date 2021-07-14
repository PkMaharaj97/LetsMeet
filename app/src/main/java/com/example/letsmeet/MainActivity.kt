
package com.example.letsmeet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.letsmeet.map.MapActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    var tabposition: Int = 0

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()


        val viewPagerAdapter = ViewPageAdapter(supportFragmentManager)

        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = viewPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.addTab(tabs.newTab().setIcon(R.drawable.users))
        tabs.addTab(tabs.newTab().setIcon(R.drawable.calendar))
        tabs.addTab(tabs.newTab().setIcon(R.drawable.graph))

        tabs.tabGravity = TabLayout.GRAVITY_FILL

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                tabposition = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

//---------------------------------------------------------------------------------------------------------------------------//
        val mapfab: FloatingActionButton = findViewById(R.id.maps_fab)
        mapfab.setOnClickListener { view ->
            var intent = Intent(view.context, MapActivity::class.java)
            startActivity(intent)


        }
    }
}

