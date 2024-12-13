package com.example.trashissue

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trashissue.api.ApiClient
import com.example.trashissue.databinding.LoginActivityBinding
import com.example.trashissue.home.HomeActivity
import com.example.trashissue.models.LoginRequest
import com.example.trashissue.models.LoginResponse
import com.example.trashissue.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Cek apakah ada token yang tersimpan
        val token = sessionManager.fetchAuthToken()
        if (!token.isNullOrEmpty()) {
            // Token sudah ada, langsung arahkan ke HomeActivity
            navigateToHome()
        }

        // Back Button
        binding.btnBackLogin.setOnClickListener {
            finish()
        }

        // Redirect to Sign Up
        binding.registerLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Login Button
        binding.btnLogin.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val apiService = ApiClient.instance
        val loginRequest = LoginRequest(email, password)

        apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                try {
                    if (response.isSuccessful) {
                        // Memeriksa apakah loginResult dan token ada
                        val loginResult = response.body()?.loginResult
                        val token = loginResult?.token

                        if (!token.isNullOrEmpty()) {
                            // Simpan token ke SessionManager
                            sessionManager.saveAuthToken(token)
                            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                            navigateToHome()
                        } else {
                            Log.e("LoginActivity", "Token kosong dalam loginResult: $loginResult")
                            Toast.makeText(this@LoginActivity, "Token tidak valid", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Menampilkan error message jika login gagal
                        val errorMessage = response.errorBody()?.string() ?: "Terjadi kesalahan saat login"
                        Log.e("LoginActivity", "Login gagal: $errorMessage")
                        Toast.makeText(this@LoginActivity, "Login gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Error parsing response: ${e.localizedMessage}", e)
                    Toast.makeText(this@LoginActivity, "Terjadi kesalahan internal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Error: ${t.message}", t)
                Toast.makeText(this@LoginActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToHome() {
        Log.d("LoginActivity", "Navigating to HomeActivity")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
