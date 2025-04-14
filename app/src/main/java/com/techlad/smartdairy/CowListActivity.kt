package com.techlad.smartdairy

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techlad.smartdairy.adapters.CowAdapter
import com.techlad.smartdairy.data.CowData
import com.techlad.smartdairy.fragments.EditCowDialogFragment

class CowListActivity : AppCompatActivity() {

    private lateinit var cowRecyclerView: RecyclerView
    private lateinit var cowAdapter: CowAdapter
    private val cowList = mutableListOf<CowData>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null
    private var filterStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cow_list)

        // Initialize RecyclerView and Adapter
        cowRecyclerView = findViewById(R.id.allCowsListRecyclerView)
        cowRecyclerView.layoutManager = LinearLayoutManager(this)
        cowAdapter = CowAdapter(this, cowList)
        cowRecyclerView.adapter = cowAdapter

        // Get the User ID from shared preferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("current_user_id", null)

        // Initialize Firebase Database Reference
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("cows").child(userId!!)
            filterStatus = intent.getStringExtra("filterStatus")
            fetchCows()

        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            Log.e("CowListActivity", "User ID is null")
        }

    }

    private fun fetchCows() {
        // Add Value Event Listener to get data from Firebase
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cowList.clear() // Clear the list before adding new data
                if (snapshot.exists()) { //check if snapshot has children.
                    for (cowSnapshot in snapshot.children) {
                        try {
                            // Use getValue with the Cow::class.java
                            val cow = cowSnapshot.getValue(CowData::class.java)
                            if (cow != null && (filterStatus == null || cow.cowStatus == filterStatus))
                                this@CowListActivity.cowList.add(cow)

                        } catch (e: DatabaseException) {
                            Log.e("CowListActivity", "Error parsing data: ${e.message}")
                            Toast.makeText(
                                this@CowListActivity,
                                "Error fetching cow data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    cowRecyclerView.adapter = CowAdapter(this@CowListActivity, cowList)
                } else {
                    cowAdapter.updateList(emptyList())
                    Toast.makeText(
                        this@CowListActivity,
                        "No cows found for this user.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle errors, e.g., show a toast
                Log.e("CowListActivity", "Database error: ${error.message}")
                Toast.makeText(this@CowListActivity, "Failed to fetch cow data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteCow(cowId: String?) {
        if (cowId != null) {
            // Get the reference to the specific cow
            val cowRef = databaseReference.child(cowId)

            // 1. Delete the cow data from the Realtime Database
            cowRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Cow deleted successfully", Toast.LENGTH_SHORT).show()
                    // Remove the item from the RecyclerView
                    val index = cowList.indexOfFirst { it.id == cowId }
                    if (index != -1) {
                        cowList.removeAt(index)
                        cowAdapter.notifyItemRemoved(index)
                    }
                }
                else {
                    // Handle errors during data deletion
                    Toast.makeText(this, "Failed to delete cow", Toast.LENGTH_SHORT).show()
                    Log.e("CowListActivity", "Error deleting cow: ${task.exception?.message}")
                }
            }
        }
    }

    fun showEditCowDialog(cow: CowData) {
        val dialogFragment = EditCowDialogFragment.newInstance(cow) // Pass the cow object
        dialogFragment.show(supportFragmentManager, "edit_cow_dialog")
    }

    fun updateCowData(cowId: String, updatedCow: CowData) {
        databaseReference.child(cowId).setValue(updatedCow).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Cow updated successfully", Toast.LENGTH_SHORT).show()
                // Update the item in the RecyclerView
                val index = cowList.indexOfFirst { it.id == cowId }
                if (index != -1) {
                    cowList[index] = updatedCow
                    cowAdapter.notifyItemChanged(index)
                }
            } else {
                Toast.makeText(this, "Failed to update cow", Toast.LENGTH_SHORT).show()
                Log.e("CowListActivity", "Error updating cow: ${task.exception?.message}")
            }
        }
    }


}