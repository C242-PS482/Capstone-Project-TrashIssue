package com.example.trashissue.home

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trashissue.LoginActivity
import com.example.trashissue.MainActivity
import com.example.trashissue.R
import com.example.trashissue.adapter.EventAdapter
import com.example.trashissue.api.ApiClient
import com.example.trashissue.api.UserApiService
import com.example.trashissue.models.EventsResponse
import com.example.trashissue.scan.ScanActivity
import com.example.trashissue.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: UserApiService
    private lateinit var recommendedAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)

        // Initialize
        sessionManager = SessionManager(this)
        apiClient = ApiClient.instance

        setupRecyclerView()
        setupClickListeners()
        fetchEvents()
    }

    private fun setupRecyclerView() {
        val rvRecommended: RecyclerView = findViewById(R.id.rv_recommended_campaign)
        recommendedAdapter = EventAdapter()
        rvRecommended.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
            adapter = recommendedAdapter
        }
    }

    private fun setupClickListeners() {
        val ivProfile: ImageView = findViewById(R.id.iv_profile)
        val tvGreeting: TextView = findViewById(R.id.tv_greeting)
        val ivLogout: ImageView = findViewById(R.id.iv_logout)
        val ivHome: ImageView = findViewById(R.id.iv_home)
        val ivAdd: ImageView = findViewById(R.id.iv_add)
        val ivSettings: ImageView = findViewById(R.id.iv_settings)

        tvGreeting.text = "HELLO!, The Greener!"

        ivLogout.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(this@HomeActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        ivAdd.setOnClickListener {
            val intent = Intent(this@HomeActivity, ScanActivity::class.java)
            startActivity(intent)
        }

        ivProfile.setOnClickListener {
            // Handle profile click
        }

        ivHome.setOnClickListener {
            // Handle home click
        }

        ivSettings.setOnClickListener {
            // Handle settings click
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchEvents() {
        apiClient.getEvents().enqueue(object : Callback<EventsResponse> {
            override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { eventsResponse ->
                        if (!eventsResponse.error) {
                            recommendedAdapter.setEvents(eventsResponse.listEvents)
                        } else {
                            Toast.makeText(this@HomeActivity,
                                "Error: ${eventsResponse.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (sessionManager.fetchAuthToken() == null) {
            val intent = Intent(this@HomeActivity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}