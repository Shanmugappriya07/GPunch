package com.example.gpunch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
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

class PunchInFragment : Fragment(R.layout.fragment_punch_in) {
    private val PREFS_NAME = "GPunchPrefs"
    private val KEY_PUNCH_IN_TIME = "punch_in_time"
    private val KEY_IS_PUNCHED_IN = "is_punched_in"

    private var isPunchedIn = false
    private var punchInTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val btnClockIn = view.findViewById<MaterialCardView>(R.id.btnClockIn)
        val tvLabel = view.findViewById<TextView>(R.id.tvLabel)
        val ivIcon = view.findViewById<ImageView>(R.id.ivIcon)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        
        val tvInTime = view.findViewById<TextView>(R.id.tvInTime)
        val tvOutTime = view.findViewById<TextView>(R.id.tvOutTime)
        val tvWorkTime = view.findViewById<TextView>(R.id.tvWorkTime)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())

        // Load Persisted State
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isPunchedIn = prefs.getBoolean(KEY_IS_PUNCHED_IN, false)
        punchInTime = prefs.getLong(KEY_PUNCH_IN_TIME, 0)

        // Real-time Clock and Work Timer logic
        runnable = object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                tvTime?.text = timeFormat.format(calendar.time)
                tvDate?.text = dateFormat.format(calendar.time)
                
                if (isPunchedIn && punchInTime > 0) {
                    val diff = calendar.timeInMillis - punchInTime
                    val hours = diff / (1000 * 60 * 60)
                    val minutes = (diff / (1000 * 60)) % 60
                    tvWorkTime?.text = String.format("%02dh %02dm", hours, minutes)
                    tvInTime?.text = timeFormat.format(Date(punchInTime))
                }
                
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)

        // Apply Initial UI State based on Persisted Data
        updateUI(isPunchedIn, tvLabel, ivIcon, btnClockIn)

        btnClockIn?.setOnClickListener {
            val currentTime = Calendar.getInstance().timeInMillis
            isPunchedIn = !isPunchedIn
            
            val editor = prefs.edit()
            editor.putBoolean(KEY_IS_PUNCHED_IN, isPunchedIn)

            if (isPunchedIn) {
                punchInTime = currentTime
                editor.putLong(KEY_PUNCH_IN_TIME, punchInTime)
                tvInTime?.text = timeFormat.format(Date(punchInTime))
                tvOutTime?.text = "--:--"
                Toast.makeText(requireContext(), "Punched In Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                tvOutTime?.text = timeFormat.format(Date(currentTime))
                Toast.makeText(requireContext(), "Punched Out Successfully!", Toast.LENGTH_SHORT).show()
            }
            editor.apply()
            updateUI(isPunchedIn, tvLabel, ivIcon, btnClockIn)
        }
    }

    private fun updateUI(punchedIn: Boolean, tvLabel: TextView?, ivIcon: ImageView?, btnClockIn: MaterialCardView?) {
        if (punchedIn) {
            tvLabel?.text = "PUNCH OUT"
            ivIcon?.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            ivIcon?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.error))
            btnClockIn?.strokeColor = ContextCompat.getColor(requireContext(), R.color.error)
        } else {
            tvLabel?.text = "PUNCH IN"
            ivIcon?.setImageResource(android.R.drawable.ic_menu_edit)
            ivIcon?.setColorFilter(ContextCompat.getColor(requireContext(), R.color.accent_green))
            btnClockIn?.strokeColor = ContextCompat.getColor(requireContext(), R.color.accent_green)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
    }
}

class HistoryFragment : Fragment(R.layout.fragment_history)

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            // Optional: Clear punch state on logout if desired
            // requireContext().getSharedPreferences("GPunchPrefs", Context.MODE_PRIVATE).edit().clear().apply()
            
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}