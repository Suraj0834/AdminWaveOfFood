package com.example.adminwaveoffood

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminwaveoffood.databinding.ActivityAdminProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest

class AdminProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminProfileBinding
    private var isEditable = false // Flag to track whether fields are editable or not

    // Firebase instances
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Firestore and Auth
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get the current user's ID
        userId = auth.currentUser?.uid

        // Initialize the binding
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Disable editing initially
        setProfileFieldsEditable(false)

        // Handle "Click here to Edit" TextView click
        binding.textView18.setOnClickListener {
            toggleProfileEditMode()
        }

        // Handle back button click
        binding.imageButton.setOnClickListener {
            finish() // Close the activity
        }

        // Handle save button click
        binding.appCompatButton.setOnClickListener {
            saveProfileInfo()
        }

        // Load the user's profile information from Firebase
        loadProfileInfo()
    }

    // Function to load the user's profile information from Firestore
    private fun loadProfileInfo() {
        userId?.let { uid ->
            val docRef = firestore.collection("users").document(uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: ""
                        val address = document.getString("address") ?: ""
                        val email = document.getString("email") ?: ""
                        val phone = document.getString("phone") ?: ""
                        val password = document.getString("password") ?: ""

                        // Populate the EditText fields with the user's profile data
                        binding.nameEditText.setText(name)
                        binding.addressEditText.setText(address)
                        binding.emailEditText.setText(email)
                        binding.phoneEditText.setText(phone)
                        binding.passwordEditText.setText(password)
                    } else {
                        // No document found, prompt the user to fill in their data
                        Toast.makeText(this, "No profile found, please fill in your details", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to load profile: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } ?: run {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to enable or disable the EditText fields
    private fun setProfileFieldsEditable(editable: Boolean) {
        binding.nameEditText.isEnabled = editable
        binding.addressEditText.isEnabled = editable
        binding.emailEditText.isEnabled = editable
        binding.phoneEditText.isEnabled = editable
        binding.passwordEditText.isEnabled = editable

        // Change the appearance to indicate non-editable mode
        val color = if (editable) R.color.black else R.color.gray
        binding.nameEditText.setTextColor(resources.getColor(color))
        binding.addressEditText.setTextColor(resources.getColor(color))
        binding.emailEditText.setTextColor(resources.getColor(color))
        binding.phoneEditText.setTextColor(resources.getColor(color))
        binding.passwordEditText.setTextColor(resources.getColor(color))

        // Optionally, update the toggle text
        binding.textView18.text = if (editable) "Save" else "Edit"
    }

    // Toggle between editable and non-editable mode
    private fun toggleProfileEditMode() {
        isEditable = !isEditable
        setProfileFieldsEditable(isEditable)
    }

    // Function to hash the password securely
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) } // Convert to hex
    }

    // Function to save profile information
    private fun saveProfileInfo() {
        if (isEditable) {
            // Validate input
            val name = binding.nameEditText.text.toString().trim()
            val address = binding.addressEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
                return
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return
            }

            // Hash the password for security
            val hashedPassword = hashPassword(password)

            // Create a user profile map with necessary fields only
            val userProfile = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone,
                "password" to hashedPassword // Store hashed password
            )

            // Save profile information to Firestore
            userId?.let { uid ->
                firestore.collection("users").document(uid).set(userProfile)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                        setProfileFieldsEditable(false)
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to save profile: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
            }
        } else {
            Toast.makeText(this, "Enable editing first", Toast.LENGTH_SHORT).show()
        }
    }
}
