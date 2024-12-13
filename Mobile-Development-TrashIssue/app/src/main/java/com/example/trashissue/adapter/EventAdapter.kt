package com.example.trashissue.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trashissue.databinding.ItemEventBinding
import com.example.trashissue.models.Event
import com.example.trashissue.ui.events.EventDetailActivity

class EventAdapter : ListAdapter<Event, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            with(binding) {
                textEventName.text = event.name
                textEventSummary.text = event.summary
                textEventLocation.text = event.cityName
                textEventTime.text = event.beginTime

                Glide.with(itemView.context)
                    .load(event.mediaCover)
                    .centerCrop()
                    .into(imageEventLogo)

                root.setOnClickListener {
                    val intent = Intent(itemView.context, EventDetailActivity::class.java).apply {
                        putExtra(EventDetailActivity.EXTRA_EVENT, event)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Method untuk backward compatibility dengan kode yang masih menggunakan setEvents
    fun setEvents(newEvents: List<Event>) {
        submitList(newEvents)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }
        }
    }
}