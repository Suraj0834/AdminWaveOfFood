package com.example.adminwaveoffood.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.adminwaveoffood.databinding.DeliveryItemBinding

class DeliveryAdapter(
    private val customerNames: ArrayList<String>,
    private val moneyStatus: ArrayList<String>
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeliveryViewHolder {
        // Inflate the layout for each item
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        // Bind the data to the views
        val customerName = customerNames[position]
        val paymentStatus = moneyStatus[position]

        holder.bind(customerName, paymentStatus)
    }

    override fun getItemCount(): Int {
        return customerNames.size // Return the size of the customer names list
    }

    inner class DeliveryViewHolder(private val binding: DeliveryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(customerName: String, paymentStatus: String) {
            // Set the customer name
            binding.CustomerName.text = customerName

            // Set the payment status text, text color and statusCard background tint
            when (paymentStatus) {
                "Received" -> {
                    binding.textView.text = "Received"
                    binding.paymentStatusTextView.setTextColor(Color.GREEN)
                    binding.statusCard.setCardBackgroundColor(Color.GREEN) // Green for Received
                }
                "Not Received" -> {
                    binding.textView.text = "Not Received"
                    binding.paymentStatusTextView.setTextColor(Color.RED)
                    binding.statusCard.setCardBackgroundColor(Color.RED) // Red for Not Received
                }
                "Pending" -> {
                    binding.textView.text = "Pending"
                    binding.paymentStatusTextView.setTextColor(Color.GRAY)
                    binding.statusCard.setCardBackgroundColor(Color.GRAY) // Gray for Pending
                }
                else -> {
                    binding.textView.text = "Unknown"
                    binding.paymentStatusTextView.setTextColor(Color.BLACK)
                    binding.statusCard.setCardBackgroundColor(Color.BLACK) // Black for unknown status
                }
            }
        }
    }
}
