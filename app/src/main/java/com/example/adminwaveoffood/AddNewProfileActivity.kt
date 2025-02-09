package com.example.adminwaveoffood

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwaveoffood.databinding.ActivityAddNewProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddNewProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding, Firebase Auth, and Firestore
        binding = ActivityAddNewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Handle back button click
        binding.imageButton.setOnClickListener {
            finish() // Close the activity
        }

        // Handle "Create New User" button click
        binding.createNewUserButton.setOnClickListener {
            createNewUser()
        }
    }

    // Function to create a new user with Firebase Authentication
    private fun createNewUser() {
        val name = binding.linearLayout2.findViewById<EditText>(R.id.nameEditText).text.toString()
        val emailOrPhone = binding.linearLayout2.findViewById<EditText>(R.id.emailOrPhoneEditText).text.toString()
        val password = binding.linearLayout2.findViewById<EditText>(R.id.passwordEditText).text.toString()

        if (name.isNotEmpty() && emailOrPhone.isNotEmpty() && password.isNotEmpty()) {
            // Create a new user in Firebase Authentication
            auth.createUserWithEmailAndPassword(emailOrPhone, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = task.result?.user?.uid

                        // Store user details in Firestore
                        userId?.let {
                            saveUserDetailsToFirestore(it, name, emailOrPhone)
                        }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("AddNewProfileActivity", "Authentication failed: ${task.exception}")
                    }
                }
        } else {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to save user details in Firestore
    private fun saveUserDetailsToFirestore(userId: String, name: String, emailOrPhone: String) {
        val userMap = hashMapOf(
            "userId" to userId,
            "name" to name,
            "emailOrPhone" to emailOrPhone
        )

        firestore.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user details: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddNewProfileActivity", "Firestore error: ${e.message}", e)
            }
    }
}
