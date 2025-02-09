package com.example.adminwaveoffood

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.PendingOrderAdapter
import com.example.adminwaveoffood.databinding.ActivityPendingOrderBinding
import com.google.firebase.firestore.FirebaseFirestore

class PendingOrderActivity : AppCompatActivity() {

    // Correct binding for the main layout
    private val binding: ActivityPendingOrderBinding by lazy {
        ActivityPendingOrderBinding.inflate(layoutInflater)
    }
    private val firestore = FirebaseFirestore.getInstance()
    private val customerNameList = arrayListOf<String>()
    private val quantitySizeList = arrayListOf<String>()
    private val foodImageList = arrayListOf<String>() // Use URL strings from Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Set up RecyclerView layout manager
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch pending orders from Firestore
        fetchPendingOrders()

        // Handle back button click
        binding.imageButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchPendingOrders() {
        firestore.collection("pendingOrders")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val customerName = document.getString("customerName") ?: "Unknown"
                    val quantitySize = document.getString("quantitySize") ?: "0"
                    val foodImageUrl = document.getString("foodImageUrl") ?: ""

                    customerNameList.add(customerName)
                    quantitySizeList.add(quantitySize)
                    foodImageList.add(foodImageUrl)
                }

                // Initialize the adapter with the data from Firestore
                val adapter = PendingOrderAdapter(customerNameList, quantitySizeList, foodImageList, this)
                binding.pendingOrderRecyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load pending orders.", Toast.LENGTH_SHORT).show()
                Log.e("PendingOrderActivity", "Firestore error: ${e.message}", e)
            }
    }
}
