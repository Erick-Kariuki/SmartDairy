package com.techlad.smartdairy.LoginandSignup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techlad.smartdairy.R
import com.techlad.smartdairy.data.UserData
import com.techlad.smartdairy.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.buttonSignup.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()

            if (signupUsername.isNotEmpty() && signupPassword.isNotEmpty()){
                signupUser(username = signupUsername, password = signupPassword)
            }else{
                Toast.makeText(this@SignUpActivity,
                    "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signupUser(username: String, password: String){
        databaseReference.orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    if (!datasnapshot.exists()){
                        val id = databaseReference.push().key
                        val userData = UserData(id,username,password)
                        databaseReference.child(id!!).setValue(userData)
                        Toast.makeText(this@SignUpActivity,
                            "Signup successful",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity,
                            LoginActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@SignUpActivity,
                            "User already exists", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseerror: DatabaseError) {
                    Toast.makeText(this@SignUpActivity,
                        "Database error:${databaseerror.message}",
                        Toast.LENGTH_SHORT).show()
                }

            })
    }
}