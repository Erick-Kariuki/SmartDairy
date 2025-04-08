package com.techlad.smartdairy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techlad.smartdairy.data.CowData
import com.techlad.smartdairy.databinding.ActivityMainBinding
import com.techlad.smartdairy.fragments.AddCowDialogFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var valueEventListener: ValueEventListener
    private var totalCowCount: Int = 0
    private var milkingCowCount: Int = 0
    private var dryCowsCount: Int = 0
    private var heiferCowsCount: Int = 0
    private var calvesCowsCount: Int =0
    private var bullCowCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val farmName = sharedPreferences.getString("current_farm_name", "Unknown Farm")
        binding.farmNameTextView.text = farmName



        binding.addCowButton.setOnClickListener {
            val dialog = AddCowDialogFragment()
            dialog.show(supportFragmentManager, "AddCowDialog")
        }

        binding.viewCowsButton.setOnClickListener {
            val intent = Intent(this, CowListActivity::class.java)
            startActivity(intent)
        }

        binding.totalCowsLayout.setOnClickListener {
            val intent = Intent(this, CowListActivity::class.java)
            startActivity(intent)
        }

        binding.milkingCowsLayout.setOnClickListener {
            val intent = Intent(this, CowListActivity::class.java)
            intent.putExtra("filterStatus", "milking")
            startActivity(intent)
            if (milkingCowCount == 0) {
                Toast.makeText(this, "You have no milking cows.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.dryCowsLayout.setOnClickListener {
            val intent = Intent(this, CowListActivity::class.java)
            intent.putExtra("filterStatus", "dry")
            startActivity(intent)
            if (dryCowsCount == 0) {
                Toast.makeText(this, "You have no dry cows.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.heifersCowsLayout.setOnClickListener {
            val intent = Intent(this, CowListActivity::class.java)
            intent.putExtra("filterStatus", "heifer")
            startActivity(intent)
            if (heiferCowsCount == 0) {
                Toast.makeText(this, "You have no heifers.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.calvesCowsLayout.setOnClickListener {
            val intent = Intent(this, CowListActivity::class.java)
            intent.putExtra("filterStatus", "calf")
            startActivity(intent)
            if (calvesCowsCount == 0) {
                Toast.makeText(this, "You have no Calves.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bullsCowsLayout.setOnClickListener {
            val intent = Intent(this, CowListActivity::class.java)
            intent.putExtra("filterStatus", "bull")
            startActivity(intent)
            if (bullCowCount == 0) {
                Toast.makeText(this, "You have no bulls.", Toast.LENGTH_SHORT).show()
            }
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("cows")

        countMilkingCows()

        databaseReference = FirebaseDatabase.getInstance().getReference("cows")

        countDryCows()

        databaseReference = FirebaseDatabase.getInstance().getReference("cows")
        countHeiferCows()

        databaseReference = FirebaseDatabase.getInstance().getReference("cows")
        countCalvesCows()

        databaseReference = FirebaseDatabase.getInstance().getReference("cows")
        countAllCows()

        databaseReference = FirebaseDatabase.getInstance().getReference("cows")
        countBullsCows()

    }

    private fun countAllCows() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalCowCount = 0
                if (snapshot.hasChildren()) {
                    for (userSnapshot in snapshot.children) { // Iterate through users
                        for (cowSnapshot in userSnapshot.children) {  // Iterate through cows for each user
                            try {
                                val cow = cowSnapshot.getValue(CowData::class.java)
                                if (cow != null) {
                                    totalCowCount++
                                }
                            } catch (e: DatabaseException) {
                                Log.e("MainActivity", "Error parsing cow data: ${e.message}")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching cow data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.totalNum.text = totalCowCount.toString()
                } else {
                    binding.totalNum.text = "0"
                    Toast.makeText(
                        this@MainActivity,
                        "No cows found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch cow data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }


    private fun countMilkingCows() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                milkingCowCount = 0
                if (snapshot.hasChildren()) {
                    for (userSnapshot in snapshot.children) { // Iterate through users
                        for (cowSnapshot in userSnapshot.children) {  // Iterate through cows for each user
                            try {
                                val cow = cowSnapshot.getValue(CowData::class.java)
                                if (cow != null && cow.cowStatus == "milking") {
                                    milkingCowCount++
                                }
                            } catch (e: DatabaseException) {
                                Log.e("MainActivity", "Error parsing cow data: ${e.message}")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching cow data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.milkingNum.text = milkingCowCount.toString()
                } else {
                    binding.milkingNum.text = "0"
                    Toast.makeText(
                        this@MainActivity,
                        "No cows found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch cow data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }




    private fun countDryCows() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dryCowsCount = 0
                if (snapshot.hasChildren()) {
                    for (userSnapshot in snapshot.children) { // Iterate through users
                        for (cowSnapshot in userSnapshot.children) {  // Iterate through cows for each user
                            try {
                                val cow = cowSnapshot.getValue(CowData::class.java)
                                if (cow != null && cow.cowStatus == "dry") {
                                    dryCowsCount++
                                }
                            } catch (e: DatabaseException) {
                                Log.e("MainActivity", "Error parsing cow data: ${e.message}")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching cow data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.dryNum.text = dryCowsCount.toString()
                } else {
                    binding.dryNum.text = "0"
                    Toast.makeText(
                        this@MainActivity,
                        "No cows found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch cow data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }




    private fun countHeiferCows() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                heiferCowsCount = 0
                if (snapshot.hasChildren()) {
                    for (userSnapshot in snapshot.children) { // Iterate through users
                        for (cowSnapshot in userSnapshot.children) {  // Iterate through cows for each user
                            try {
                                val cow = cowSnapshot.getValue(CowData::class.java)
                                if (cow != null && cow.cowStatus == "heifer") {
                                    heiferCowsCount++
                                }
                            } catch (e: DatabaseException) {
                                Log.e("MainActivity", "Error parsing cow data: ${e.message}")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching cow data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.heiferNum.text = heiferCowsCount.toString()
                } else {
                    binding.heiferNum.text = "0"
                    Toast.makeText(
                        this@MainActivity,
                        "No cows found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch cow data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }


    private fun countCalvesCows() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                calvesCowsCount = 0
                if (snapshot.hasChildren()) {
                    for (userSnapshot in snapshot.children) { // Iterate through users
                        for (cowSnapshot in userSnapshot.children) {  // Iterate through cows for each user
                            try {
                                val cow = cowSnapshot.getValue(CowData::class.java)
                                if (cow != null && cow.cowStatus == "calf") {
                                    calvesCowsCount++
                                }
                            } catch (e: DatabaseException) {
                                Log.e("MainActivity", "Error parsing cow data: ${e.message}")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching cow data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.calfNum.text = calvesCowsCount.toString()
                } else {
                    binding.calfNum.text = "0"
                    Toast.makeText(
                        this@MainActivity,
                        "No cows found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch cow data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }


    private fun countBullsCows() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bullCowCount = 0
                if (snapshot.hasChildren()) {
                    for (userSnapshot in snapshot.children) { // Iterate through users
                        for (cowSnapshot in userSnapshot.children) {  // Iterate through cows for each user
                            try {
                                val cow = cowSnapshot.getValue(CowData::class.java)
                                if (cow != null && cow.cowStatus == "bull") {
                                    bullCowCount++
                                }
                            } catch (e: DatabaseException) {
                                Log.e("MainActivity", "Error parsing cow data: ${e.message}")
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching cow data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    binding.bullNum.text = bullCowCount.toString()
                } else {
                    binding.bullNum.text = "0"
                    Toast.makeText(
                        this@MainActivity,
                        "No cows found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to fetch cow data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }

}
