package com.example.adminwaveoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.MenuItem // Import MenuItem data class
import com.example.adminwaveoffood.databinding.ItemItemBinding

class AddItemAdapter(
    private val menuItems: List<MenuItem>, // Change to List<MenuItem>
    private val onDelete: (Int) -> Unit // Lambda to handle deletion
) : RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size // Return size of menuItems list

    inner class AddItemViewHolder(private val binding: ItemItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var quantity = 1 // Default quantity

        fun bind(position: Int) {
            val item = menuItems[position] // Get MenuItem for the current position

            binding.apply {
                FoodNameTextView.text = item.name // Use MenuItem properties
                PriceTextView.text = item.price

                // Use Glide to load the image from the URL
                Glide.with(binding.root.context)
                    .load(item.imageUrl) // Load the image from the URL
                    .into(foodImageView)

                QuantityTextView.text = quantity.toString() // Set initial quantity

                // Handle minus button click
                minusButton.setOnClickListener {
                    if (quantity > 1) {
                        quantity-- // Decrease quantity
                        QuantityTextView.text = quantity.toString() // Update display
                    }
                }

                // Handle plus button click
                plusbutton.setOnClickListener {
                    quantity++ // Increase quantity
                    QuantityTextView.text = quantity.toString() // Update display
                }

                // Handle delete button click
                deleteButton.setOnClickListener {
                    onDelete(adapterPosition) // Call delete function with the position
                }
            }
        }
    }
}
