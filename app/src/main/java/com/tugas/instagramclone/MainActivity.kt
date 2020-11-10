package com.tugas.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tugas.instagramclone.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener( onBottomNavListener)

        val frag = supportFragmentManager.beginTransaction()
        frag.add(R.id.frag_container, HomeFragment())
        frag.commit()
    }
    private val onBottomNavListener = BottomNavigationView.OnNavigationItemSelectedListener { i->
        var selectedFragment: Fragment = HomeFragment()

        when(i.itemId){
            R.id.itemHome ->{
                selectedFragment = HomeFragment()

            }
            R.id.itemSearch ->{
                selectedFragment = SearchFragment()

            }
            R.id.itemAddPost -> {
                startActivity(Intent(this, AddPostActivity::class.java))
            }
            R.id.itemActivity -> {
                selectedFragment = ActivityFragment()

            }
            R.id.itemProfile ->{
                selectedFragment = ProfileFragment()

            }
        }

        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.frag_container,selectedFragment)
        frag.commit()

        true
    }

}