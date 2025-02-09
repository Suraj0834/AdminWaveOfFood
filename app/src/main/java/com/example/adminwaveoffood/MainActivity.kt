package com.example.adminwaveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwaveoffood.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Retrieve data from Firebase
        loadOrderData()

        // Set up click listeners for existing buttons
        binding.addMenu.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }

        binding.allItemMenu.setOnClickListener {
            startActivity(Intent(this, AllItemActivity::class.java))
        }

        binding.oderDispatch.setOnClickListener {
            startActivity(Intent(this, OutForDeliveryActivity::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this, AdminProfileActivity::class.java))
        }

        binding.AddNewButton.setOnClickListener {
            startActivity(Intent(this, AddNewProfileActivity::class.java))
        }

        binding.PendingOrderImage.setOnClickListener {
            startActivity(Intent(this, PendingOrderActivity::class.java))
        }

        binding.pendingOrderText.setOnClickListener {
            startActivity(Intent(this, PendingOrderActivity::class.java))
        }

        // Add click listener for Logout button
        binding.LogOut.setOnClickListener {
            auth.signOut()  // Logs out the user
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadOrderData() {
        // Assume we have "orders" node in Firebase where each order has a status and amount
        val ordersRef = database.child("orders")

        // Listen for changes in the database
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var pendingOrders = 0
                var completedOrders = 0
                var totalEarnings = 0.0

                // Loop through each order and check its status and amount
                for (orderSnapshot in snapshot.children) {
                    val status = orderSnapshot.child("status").getValue(String::class.java)
                    val amount = orderSnapshot.child("amount").getValue(Double::class.java) ?: 0.0

                    when (status) {
                        "pending" -> pendingOrders++
                        "completed" -> {
                            completedOrders++
                            totalEarnings += amount
                        }
                    }
                }

                // Update the UI with values from Firebase
                binding.PendingOrderCount.text = pendingOrders.toString()
                binding.textView5.text = completedOrders.toString()
                binding.textView7.text = "$${totalEarnings.toInt()}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Optionally, you can override onResume to reload data in case it's refreshed in the background
    override fun onResume() {
        super.onResume()
        loadOrderData() // Reload data when the activity is resumed
    }
}
