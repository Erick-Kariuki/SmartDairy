package com.example.dairyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddCowDialogFragment : DialogFragment() {

    lateinit var edtCowName : EditText
    lateinit var edtCowBreed : EditText
    lateinit var edtCowDOB : EditText
    lateinit var edtMotherName : EditText
    lateinit var edtCowStatus : EditText
    lateinit var btnSave : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_add_cow, container, false)

        edtCowName = view.findViewById(R.id.edtCowName)
        edtCowBreed = view.findViewById(R.id.edtCowBreed)
        edtCowDOB = view.findViewById(R.id.edtCowDOB)
        edtMotherName = view.findViewById(R.id.edtMotherName)
        edtCowStatus = view.findViewById(R.id.edtCowStatus)
        btnSave = view.findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            saveCow()
        }
        return view
    }

    fun saveCow(){
        val name = edtCowName.text.toString()
        val breed = edtCowBreed.text.toString()
        val dob = edtCowDOB.text.toString()
        val motherName = edtMotherName.text.toString()
        val status = edtCowStatus.text.toString()

        val cow = CowEntity(name = name, breed = breed, dateOfBirth = dob, motherName = motherName, status = status)

        lifecycleScope.launch {
            CowDatabase.database.cowDao().insertCow(cow)
            dismiss()

        }
    }
}