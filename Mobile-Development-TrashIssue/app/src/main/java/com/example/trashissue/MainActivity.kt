package com.example.trashissue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.trashissue.home.HomeActivity
import com.example.trashissue.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        // Cek apakah token sudah ada
        val token = sessionManager.fetchAuthToken()
        if (!token.isNullOrEmpty()) {
            // Jika token sudah ada, langsung arahkan ke HomeActivity
            navigateToHome()
        } else {
            // Jika token tidak ada, tampilkan tombol login dan sign up
            setupUI()
        }
    }

    private fun setupUI() {
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)

        // Aksi untuk tombol Login
        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Aksi untuk tombol Sign Up
        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun navigateToHome() {
        // Navigasi ke HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Menutup MainActivity
    }
}
