package com.techlad.smartdairy

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso

class CowDetailsActivity : AppCompatActivity() {

    private lateinit var cowDetailsImageView: ImageView
    private lateinit var cowDetailsNameTextView: TextView
    private lateinit var cowDetailsMotherTextView: TextView
    private lateinit var cowDetailsDobTextView: TextView
    private lateinit var cowDetailsBreedTextView: TextView
    private lateinit var cowDetailsTagTextView: TextView
    private lateinit var cowDetailsStatusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cow_details)

        // Initialize UI elements
        cowDetailsImageView = findViewById(R.id.cowDetailsImageView)
        cowDetailsNameTextView = findViewById(R.id.cowDetailsNameTextView)
        cowDetailsMotherTextView = findViewById(R.id.cowDetailsMotherTextView)
        cowDetailsDobTextView = findViewById(R.id.cowDetailsDobTextView)
        cowDetailsBreedTextView = findViewById(R.id.cowDetailsBreedTextView)
        cowDetailsTagTextView = findViewById(R.id.cowDetailsTagTextView)
        cowDetailsStatusTextView = findViewById(R.id.cowDetailsStatusTextView)

        // Get data from the intent
        val bundle = intent.extras
        val cowName = bundle?.getString("cowName")
        val motherName = bundle?.getString("motherName")
        val dateOfBirth = bundle?.getString("dateOfBirth")
        val breed = bundle?.getString("cowBreed")
        val tagNumber = bundle?.getString("tagNumber")
        val imageUrl = bundle?.getString("imageUrl")
        val cowStatus = bundle?.getString("cowStatus")

        // Set the data to the views
        cowDetailsNameTextView.text = cowName
        cowDetailsMotherTextView.text = motherName
        cowDetailsDobTextView.text = dateOfBirth
        cowDetailsBreedTextView.text = breed
        cowDetailsTagTextView.text = tagNumber
        cowDetailsStatusTextView.text = cowStatus

        // Load the image
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            Picasso.get().load(Uri.parse(imageUrl)).into(cowDetailsImageView)
        } else {
            cowDetailsImageView.setImageResource(R.drawable.cow2) // Use placeholder
        }
    }
}