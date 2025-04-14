package com.techlad.smartdairy.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.techlad.smartdairy.R
import com.techlad.smartdairy.data.InseminatedCow
import com.techlad.smartdairy.databinding.FragmentAddInseminatedCowBinding
import java.util.Calendar

class AddInseminatedCowFragment : DialogFragment() {

    private lateinit var binding: FragmentAddInseminatedCowBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddInseminatedCowBinding.inflate(inflater, container, false)


        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("current_user_id", null)

        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("breeding_records").child(userId!!) // Correct ref
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            Log.e("AddInseminatedCowDialogFragment", "User ID is null")
            dismiss()
        }

        binding.inseminationDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.inseminationcancelButton.setOnClickListener {
            dismiss()
        }

        binding.inseminationsaveButton.setOnClickListener {
            saveInseminatedCowData()
        }

        return binding.root

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1.00).toInt(),
            (resources.displayMetrics.heightPixels * 0.50).toInt()
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.inseminationDateEditText.setText(formattedDate)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun saveInseminatedCowData() {
        val cowName = binding.inseminatedCowNameEditText.text.toString().trim()
        val cowBreed = binding.inseminatedCowBreedEditText.text.toString().trim()
        val bullBreed = binding.bullBreedEditText.text.toString().trim()
        val inseminationDate = binding.inseminationDateEditText.text.toString().trim()

        if (cowName.isNotEmpty() && cowBreed.isNotEmpty() && bullBreed.isNotEmpty() && inseminationDate.isNotEmpty()) {
            val userId = userId // Use the userId from shared preferences
            val cowId = databaseReference.push().key
            val inseminatedCow = InseminatedCow(
                id = cowId,
                cowName = cowName,
                cowBreed = cowBreed,
                bullBreed = bullBreed,
                inseminationDate = inseminationDate,
                userId = userId
            )

            if (cowId != null) {
                databaseReference.child(cowId).setValue(inseminatedCow).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dismiss()
                        Toast.makeText(requireContext(), "Inseminated cow added successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add inseminated cow", Toast.LENGTH_SHORT).show()
                        Log.e(
                            "AddInseminatedCowDialogFragment",
                            "Error saving data: ${task.exception?.message}"
                        )
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }


}