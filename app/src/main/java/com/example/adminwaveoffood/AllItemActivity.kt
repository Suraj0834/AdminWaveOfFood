package com.example.adminwaveoffood

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.AddItemAdapter
import com.example.adminwaveoffood.databinding.ActivityAllItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllItemActivity : AppCompatActivity() { // Remove <MenuItem> here

    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: AddItemAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val userId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    private val menuItems = ArrayList<MenuItem>() // Declare as ArrayList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imageButton.setOnClickListener {
            finish()
        }

        // Initialize the RecyclerView with updated adapter
        adapter = AddItemAdapter(menuItems) { position -> removeItemFromFirestore(position) }
        binding.RecyclerView.layoutManager = LinearLayoutManager(this)
        binding.RecyclerView.adapter = adapter

        loadItemsFromFirestore()
    }

    private fun loadItemsFromFirestore() {
        firestore.collection("users").document(userId).collection("menuItems")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val item = document.toObject(MenuItem::class.java) // Use the MenuItem class directly
                    menuItems.add(item)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading items: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeItemFromFirestore(position: Int) {
        val itemName = menuItems[position].name // Access name property from MenuItem
        firestore.collection("users").document(userId).collection("menuItems")
            .whereEqualTo("name", itemName)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.delete().addOnSuccessListener {
                        menuItems.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, menuItems.size)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error removing item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
