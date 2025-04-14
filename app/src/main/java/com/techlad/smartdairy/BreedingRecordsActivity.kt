package com.techlad.smartdairy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techlad.smartdairy.adapters.InseminatedCowAdapter
import com.techlad.smartdairy.data.InseminatedCow
import com.techlad.smartdairy.fragments.AddInseminatedCowFragment

class BreedingRecordsActivity : AppCompatActivity() {

    private lateinit var inseminatedCowRecyclerView: RecyclerView
    private lateinit var inseminatedCowAdapter: InseminatedCowAdapter
    private val inseminatedCowList = mutableListOf<InseminatedCow>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var addInseminatedCowButton: FloatingActionButton
    private var userId: String? = null
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_breeding_records)

        inseminatedCowRecyclerView = findViewById(R.id.inseminatedCowsRecyclerView)
        inseminatedCowRecyclerView.layoutManager = LinearLayoutManager(this)
        inseminatedCowAdapter = InseminatedCowAdapter(this, inseminatedCowList)
        inseminatedCowRecyclerView.adapter = inseminatedCowAdapter

        addInseminatedCowButton = findViewById(R.id.addInseminatedCowButton) // Initialize FAB
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("current_user_id", null)


        // Initialize Firebase Database Reference
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("breeding_records").child(userId!!) // Correct ref
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            Log.e("BreedingRecordsActivity", "User ID is null")
            finish()
            return
        }

        addInseminatedCowButton.setOnClickListener {
            val dialogFragment = AddInseminatedCowFragment()
            dialogFragment.show(supportFragmentManager, "AddInseminatedCowDialog")
        }
        fetchInseminatedCows()

    }

    private fun fetchInseminatedCows() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                inseminatedCowList.clear()
                if (snapshot.hasChildren()) {
                    for (cowSnapshot in snapshot.children) {
                        try {
                            val cow = cowSnapshot.getValue(InseminatedCow::class.java)
                            if (cow != null) {
                                this@BreedingRecordsActivity.inseminatedCowList.add(cow)
                            }
                        } catch (e: DatabaseException) {
                            Log.e("BreedingRecordsActivity", "Error parsing data: ${e.message}")
                            Toast.makeText(
                                this@BreedingRecordsActivity,
                                "Error fetching data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                    inseminatedCowRecyclerView.adapter = InseminatedCowAdapter(this@BreedingRecordsActivity, inseminatedCowList)
                } else {
                    inseminatedCowAdapter.updateList(emptyList())
                    Toast.makeText(
                        this@BreedingRecordsActivity,
                        "No inseminated cows found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BreedingRecordsActivity", "Database error: ${error.message}")
                Toast.makeText(
                    this@BreedingRecordsActivity,
                    "Failed to fetch data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener!!)
        }
    }
}