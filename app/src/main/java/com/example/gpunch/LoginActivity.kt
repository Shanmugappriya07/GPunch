package com.example.gpunch

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvTitle = findViewById<TextView>(R.id.tvLoginTitle)
        val tvSub = findViewById<TextView>(R.id.tvLoginSub)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmail)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitLogin)

        // Animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        tvTitle.startAnimation(fadeIn)
        tvSub.startAnimation(fadeIn)
        tilEmail.startAnimation(slideUp)
        tilPassword.startAnimation(slideUp)
        btnSubmit.startAnimation(slideUp)

        btnSubmit.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}