package com.example.adminwaveoffood

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.DeliveryAdapter
import com.example.adminwaveoffood.databinding.ActivityOutForDeliveryBinding
import com.google.firebase.firestore.FirebaseFirestore

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val customerNames = arrayListOf<String>()
    private val moneyStatus = arrayListOf<String>()
    private lateinit var adapter: DeliveryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupRecyclerView()
        fetchDataFromFirestore()

        // Add back button functionality
        binding.imageButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = DeliveryAdapter(customerNames, moneyStatus)
        binding.RecyclerViewa.adapter = adapter
        binding.RecyclerViewa.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchDataFromFirestore() {
        firestore.collection("outForDelivery") // Firestore collection path
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("customerName") ?: "Unknown"
                    val status = document.getString("paymentStatus") ?: "Unknown"
                    customerNames.add(name)
                    moneyStatus.add(status)
                }
                adapter.notifyDataSetChanged() // Notify adapter that data has changed
            }
            .addOnFailureListener { e ->
                Log.e("OutForDeliveryActivity", "Error fetching data", e)
                Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
