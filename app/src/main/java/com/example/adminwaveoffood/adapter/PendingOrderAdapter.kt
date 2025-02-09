package com.example.adminwaveoffood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.PendingOrderItemBinding

class PendingOrderAdapter(
    private val customerNames: ArrayList<String>,
    private val quantities: ArrayList<String>,
    private val foodImageUrls: ArrayList<String>, // Change Int to String for URLs
    private val context: Context
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    // List to store the acceptance state for each item
    private val acceptanceStatus = Array(customerNames.size) { false }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class PendingOrderViewHolder(private val binding: PendingOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                // Set customer name and quantity
                CustomerNameTextView.text = customerNames[position]
                quantitysize.text = quantities[position]

                // Load food image from URL with Glide
                Glide.with(context)
                    .load(foodImageUrls[position])
                    .into(foodImageView)

                // Set the button text based on acceptance status
                AceeptButton.text = if (acceptanceStatus[position]) "Dispatch" else "Accept"

                // Set up click listener to toggle the acceptance status
                AceeptButton.setOnClickListener {
                    if (!acceptanceStatus[position]) {
                        // Order accepted
                        acceptanceStatus[position] = true
                        AceeptButton.text = "Dispatch"
                        showToast("Order Accepted")
                    } else {
                        // Order dispatched and item removed
                        customerNames.removeAt(adapterPosition)
                        quantities.removeAt(adapterPosition)
                        foodImageUrls.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        showToast("Order Dispatched")
                    }
                }
            }
        }
    }

    // Function to display toast messages
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
