package com.example.adminwaveoffood

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminwaveoffood.databinding.ActivityAddItemBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    private var selectedImageUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val userId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.cardView8.findViewById<ImageView>(R.id.imageView).setImageURI(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle back button click
        binding.imageButton.setOnClickListener {
            finish()
        }

        // Handle image selection
        binding.textView14.setOnClickListener {
            pickImageFromGallery()
        }

        // Handle add item button click
        binding.buttonAddItem.setOnClickListener {
            addItem()
        }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun addItem() {
        val foodName = binding.FoodName.text.toString().trim()
        val foodPrice = binding.FoodPrice.text.toString().trim()
        val description = binding.DescriptionTextView.text.toString().trim()
        val ingredients = binding.IngredientsEditText.text.toString().trim()

        if (foodName.isEmpty() || foodPrice.isEmpty() || description.isEmpty() || ingredients.isEmpty() || selectedImageUri == null) {
            Snackbar.make(binding.root, "Please fill in all fields and select an image.", Snackbar.LENGTH_SHORT).show()
            return
        }

        val imageRef = storage.reference.child("images/${System.currentTimeMillis()}.jpg")
        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Creating a map for the item data
                    val itemData = hashMapOf(
                        "name" to foodName,
                        "price" to foodPrice,
                        "imageUrl" to downloadUrl.toString(),
                        "description" to description,
                        "ingredients" to ingredients
                    )

                    // Save item to Firestore
                    firestore.collection("users").document(userId).collection("menuItems")
                        .add(itemData)
                        .addOnSuccessListener {
                            Snackbar.make(binding.root, "$foodName was added.", Snackbar.LENGTH_SHORT).show()
                            startActivity(Intent(this, AllItemActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Snackbar.make(binding.root, "Error adding item: ${e.message}", Snackbar.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Error uploading image: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
    }
}
