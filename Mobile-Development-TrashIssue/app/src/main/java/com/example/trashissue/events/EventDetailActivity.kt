package com.example.trashissue.ui.events

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.trashissue.R
import com.example.trashissue.models.Event

class EventDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EVENT = "extra_event"
    }

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        setupBackButton()
        setupFavoriteButton()

        val event = intent.getParcelableExtra<Event>(EXTRA_EVENT)
        event?.let { setupEventDetail(it) }
    }

    private fun setupBackButton() {
        val ivBack: ImageView = findViewById(R.id.iv_back)
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupFavoriteButton() {
        val ivFavorite: ImageView = findViewById(R.id.iv_favorite)
        ivFavorite.setOnClickListener {
            isFavorite = !isFavorite
            ivFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }
    }

    private fun setupEventDetail(event: Event) {
        val ivEventCover: ImageView = findViewById(R.id.iv_event_cover)
        val tvEventName: TextView = findViewById(R.id.tv_event_name)
        val tvEventTime: TextView = findViewById(R.id.tv_event_time)
        val tvEventLocation: TextView = findViewById(R.id.tv_event_location)
        val tvEventDescription: TextView = findViewById(R.id.tv_event_description)

        Glide.with(this)
            .load(event.mediaCover)
            .centerCrop()
            .into(ivEventCover)

        tvEventName.text = event.name
        tvEventTime.text = "${event.beginTime} - ${event.endTime}"
        tvEventLocation.text = event.cityName

        val cleanDescription = event.description
            .replace("<p>", "")
            .replace("</p>", "\n")
            .replace("<br />", "\n")
            .replace("<br/>", "\n")
            .replace("<br>", "\n")
            .trim()

        tvEventDescription.text = cleanDescription
    }
}