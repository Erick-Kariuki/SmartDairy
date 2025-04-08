package com.techlad.smartdairy.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.techlad.smartdairy.data.CowData
import com.techlad.smartdairy.databinding.FragmentAddCowDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditCowDialogFragment : DialogFragment() {


    private lateinit var binding: FragmentAddCowDialogBinding
    private lateinit var databaseReference: DatabaseReference
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var storageReference: StorageReference
    private lateinit var cowStatusAdapter: ArrayAdapter<String>
    private lateinit var cowBreedAdapter: ArrayAdapter<String>
    private var cowId: String? = null
    private var existingImageUrl: String? = null

    companion object {
        fun newInstance(cow: CowData): EditCowDialogFragment {
            val fragment = EditCowDialogFragment()
            val args = Bundle()
            args.putString("cowId", cow.id)
            args.putString("cowName", cow.cowName)
            args.putString("motherName", cow.motherName)
            args.putString("dateOfBirth", cow.dateOfBirth)
            args.putString("cowBreed", cow.cowBreed)
            args.putString("tagNumber", cow.tagNumber)
            args.putString("imageUrl", cow.imageUrl)
            args.putString("status", cow.cowStatus)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddCowDialogBinding.inflate(inflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("cows")
        storageReference = FirebaseStorage.getInstance().reference


        val cowBreedOptions = arrayOf("Select Cow Breed","Friesian", "Holstein", "Jersey",
            "Angus", "Sahiwal", "Gir", "Nellore", "Indigenous") // Define your breeds
        cowBreedAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cowBreedOptions
        )
        cowBreedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cowBreedSpinner.adapter = cowBreedAdapter

        val cowStatusOptions = arrayOf("Select Cow Status", "Milking", "Dry", "Calf", "Heifer","Bull") // Define your statuses
        cowStatusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cowStatusOptions
        )
        cowStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cowStatusSpinner.adapter = cowStatusAdapter

        cowId = arguments?.getString("cowId")
        binding.cowNameEditText.setText(arguments?.getString("cowName"))
        binding.motherNameEditText.setText(arguments?.getString("motherName"))
        binding.dateOfBirthEditText.setText(arguments?.getString("dateOfBirth"))
        binding.tagNumberEditText.setText(arguments?.getString("tagNumber"))
        existingImageUrl = arguments?.getString("imageUrl")

        val breed = arguments?.getString("cowBreed")
        val breedPosition = cowBreedAdapter.getPosition(breed)
        binding.cowBreedSpinner.setSelection(breedPosition)

        val status = arguments?.getString("status")
        val statusPosition = cowStatusAdapter.getPosition(status)
        binding.cowStatusSpinner.setSelection(statusPosition)


        if (existingImageUrl != null && existingImageUrl!!.isNotEmpty()) {
            Glide.with(requireContext())
                .load(existingImageUrl)
                .into(binding.cowImageView)
        }


        binding.cowImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        val calenderBox = Calendar.getInstance()
        val dateBox = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calenderBox.set(Calendar.YEAR, year)
            calenderBox.set(Calendar.MONTH, month)
            calenderBox.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }

        binding.dateOfBirthEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(), dateBox,
                calenderBox.get(Calendar.YEAR),
                calenderBox.get(Calendar.MONTH),
                calenderBox.get(Calendar.DAY_OF_MONTH)
            ).show()
            updateDateInView(calenderBox)
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            saveCowToDatabase()
        }


        return binding.root

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1.00).toInt(),
            (resources.displayMetrics.heightPixels * 0.82).toInt()
        )
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.cowImageView.setImageURI(imageUri)
        }
    }


    private fun saveCowToDatabase() {
        val cowName = binding.cowNameEditText.text.toString()
        val motherName = binding.motherNameEditText.text.toString()
        val dateOfBirth = binding.dateOfBirthEditText.text.toString()
        val tagNumber = binding.tagNumberEditText.text.toString()
        val cowBreed = binding.cowBreedSpinner.selectedItem.toString().lowercase()
        val cowStatus = binding.cowStatusSpinner.selectedItem.toString().lowercase()
        val imageUri = imageUri



        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("current_user_id", null)

        if (cowName.isNotEmpty() && dateOfBirth.isNotEmpty() && cowBreed.isNotEmpty()
            && tagNumber.isNotEmpty() && cowStatus.isNotEmpty() && imageUri != null) {

            if (userId != null) {
                val cowId = databaseReference.child(userId).push().key
                val cow = CowData(
                    id = cowId,
                    cowName = cowName,
                    motherName = motherName,
                    dateOfBirth = dateOfBirth,
                    cowBreed = cowBreed,
                    cowStatus = cowStatus,
                    tagNumber = tagNumber,
                    imageUrl = imageUri.toString())

                if (cowId != null) {
                    databaseReference.child(userId).child(cowId).setValue(cow).addOnCompleteListener {
                        if (it.isSuccessful) {
                            dismiss()
                            Toast.makeText(requireContext(), "Cow added successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to add cow", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDateInView(calender: Calendar) {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.dateOfBirthEditText.setText(sdf.format(calender.getTime()))
    }
}