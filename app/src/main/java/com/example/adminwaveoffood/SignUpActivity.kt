package com.example.adminwaveoffood

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwaveoffood.databinding.ActivitySignUpBinding
import com.example.adminwaveoffood.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var restaurantName: String
    private lateinit var location: String
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        firestore = FirebaseFirestore.getInstance()

        // Set up location dropdown
        val locationList = arrayOf("Jalandhar", "Delhi", "Lucknow", "Varanasi", "Ghazipur", "Gahmar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView: AutoCompleteTextView = binding.listofLocation
        autoCompleteTextView.setAdapter(adapter)

        // Handle Create Account button click
        binding.CreateAccountButton.setOnClickListener {
            email = binding.EmailOrPhone.text.toString().trim()
            password = binding.Password.text.toString().trim()
            userName = binding.Name.text.toString().trim()
            restaurantName = binding.RestaurantName.text.toString().trim()
            location = binding.listofLocation.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || restaurantName.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        // Handle "Already have an account?" click to navigate to login
        binding.alreadyhaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                saveUserData() // Save user data in the database
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure", task.exception)
            }
        }
    }

    // Save user data to Firebase Database and Firestore
    private fun saveUserData() {
        val userId = auth.currentUser?.uid ?: return
        val user = UserModel(userName = userName, email = email, restaurantName = restaurantName, password = password)

        // Save to Firebase Realtime Database
        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Log.d("Database", "User data saved in Realtime Database")
            }
            .addOnFailureListener { e ->
                Log.d("Database", "Failed to save user data: ${e.message}")
            }

        // Save to Firestore
        firestore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "User data saved in Firestore")
            }
            .addOnFailureListener { e ->
                Log.d("Firestore", "Failed to save user data: ${e.message}")
            }
    }
}
