package com.example.gpunch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // Set default fragment
        loadFragment(PunchInFragment())

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_punch -> {
                    loadFragment(PunchInFragment())
                    true
                }
                R.id.navigation_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

class PunchInFragment : Fragment(R.layout.fragment_punch_in)
class HistoryFragment : Fragment(R.layout.fragment_history)
class ProfileFragment : Fragment(R.layout.fragment_profile)