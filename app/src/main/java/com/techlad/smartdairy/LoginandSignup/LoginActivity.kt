package com.techlad.smartdairy.LoginandSignup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
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
import com.techlad.smartdairy.MainActivity
import com.techlad.smartdairy.R
import com.techlad.smartdairy.data.UserData
import com.techlad.smartdairy.databinding.ActivityLoginBinding
import androidx.core.content.edit


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")




        binding.buttonLogin.setOnClickListener {
            val loginusername = binding.loginUsername.text.toString()
            val loginpassword = binding.loginPassword.text.toString()

            if (loginusername.isNotEmpty() && loginpassword.isNotEmpty()){
                loginUser(username = loginusername, password = loginpassword)
            }else{
                Toast.makeText(this@LoginActivity, "All fields are required",
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirect.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }
    }

     private fun loginUser(username: String, password: String){
        databaseReference.orderByChild("username")
            .equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    if (datasnapshot.exists()){
                        for (userSnapshot in datasnapshot.children){
                            val userData = userSnapshot.getValue(UserData::class.java)
                            val userSnapshot = datasnapshot.children.first()
                            val userId = userSnapshot.child("id").getValue(String::class.java)
                            val farmName = userSnapshot.child("username").getValue(String::class.java)

                            if (userData != null && userData.password == password){


                                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                sharedPreferences.edit() {
                                    putString("current_user_id", userId)
                                    putString("current_farm_name", farmName)
                                }
                                Toast.makeText(this@LoginActivity,"Login Successful",
                                    Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                                return
                            }
                        }
                    }
                        Toast.makeText(this@LoginActivity,"Incorrect Username or Password!",
                            Toast.LENGTH_SHORT).show()

                }

                override fun onCancelled(dataerror: DatabaseError) {
                    Toast.makeText(this@LoginActivity,
                        "Database Error: ${dataerror.message}",Toast.LENGTH_SHORT).show()
                }

            })
    }



}