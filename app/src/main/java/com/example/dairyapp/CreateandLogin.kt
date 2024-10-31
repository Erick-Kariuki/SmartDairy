package com.example.dairyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dairyapp.databinding.ActivityCreateandLoginBinding

class CreateandLogin : AppCompatActivity() {

    private val databaseHelper = DatabaseHelper(this)
    private lateinit var binding: ActivityCreateandLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateandLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCreateListner()
        btnLoginListner()


    }

    private fun btnCreateListner(){

        val txtfarmname = binding.txtFarmName.text.toString().trim()
        val txtpassword = binding.txtFarmName.text.toString().trim()

        binding.CreateButton.setOnClickListener {

            if (!databaseHelper.checkUser(txtfarmname)){
                val user = User()
                user.farmname = txtfarmname
                user.password = txtpassword
                databaseHelper.addFarm(user)
                Toast.makeText(this,"Farm created succesfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"Farm already exists", Toast.LENGTH_SHORT).show()
            }
        }
        }

    private fun btnLoginListner(){
        binding.LoginButton.setOnClickListener {
            if (databaseHelper.checkUser(binding.txtFarmName.text.toString().trim(),
                    binding.txtFarmPassword.text.toString().trim())){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this,"Login successful",Toast.LENGTH_SHORT).show()

            }
            else{
                Toast.makeText(this,"Incorrect farmname or password",Toast.LENGTH_SHORT).show()
            }
        }
    }
    }
