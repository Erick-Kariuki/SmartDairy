package com.techlad.smartdairy.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.techlad.smartdairy.CowDetailsActivity
import com.techlad.smartdairy.CowListActivity
import com.techlad.smartdairy.R
import com.techlad.smartdairy.data.CowData

class CowAdapter(private val context: Context,
                 private val cowList: MutableList<CowData>
    ): RecyclerView.Adapter<CowAdapter.CowViewHolder>() {

    class CowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cowImageView: ImageView = itemView.findViewById(R.id.cowImageView)
        val cowNameTextView: TextView = itemView.findViewById(R.id.cowNameTextView)
        val cowStatusTextView: TextView = itemView.findViewById(R.id.cowStatusTextView)
        val viewMoreButton: Button = itemView.findViewById(R.id.viewMoreButton)
        val deleteCowButton: Button = itemView.findViewById(R.id.deleteCowButton)
        val editCowButton: Button = itemView.findViewById(R.id.editCowButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CowViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cow_list_item, parent, false)
        return CowViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CowViewHolder, position: Int) {
        val cow = cowList[position]
        //Glide.with(context).load(cow.imageUrl).into(holder.cowImageView)
        holder.cowNameTextView.text = cow.cowName
        holder.cowStatusTextView.text = cow.cowStatus

        // Load image using Picasso
        if (!cow.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(Uri.parse(cow.imageUrl)).into(holder.cowImageView)
        } else {
            holder.cowImageView.setImageResource(R.drawable.cow) // Use your placeholder
        }

        holder.viewMoreButton.setOnClickListener {
            val intent = Intent(context, CowDetailsActivity::class.java)
            intent.putExtra("cowId", cow.id)
            // Pass the entire cow object as a bundle.  Easier than individual fields.
            val bundle = Bundle()
            bundle.putString("cowName", cow.cowName)
            bundle.putString("motherName", cow.motherName)
            bundle.putString("dateOfBirth", cow.dateOfBirth)
            bundle.putString("cowBreed", cow.cowBreed)
            bundle.putString("tagNumber", cow.tagNumber)
            bundle.putString("imageUrl", cow.imageUrl)
            bundle.putString("cowStatus", cow.cowStatus)// Pass the image URL
            intent.putExtras(bundle)

            context.startActivity(intent)
        }

        holder.deleteCowButton.setOnClickListener {
            // Show confirmation dialog
            AlertDialog.Builder(context)
                .setTitle("Delete Cow")
                .setMessage("Are you sure you want to delete this cow?")
                .setPositiveButton("Yes") { dialog, _ ->
                    // Call function to delete the cow
                    (context as? CowListActivity)?.deleteCow(cow.id) // Cast context to activity
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
                .show()
        }
        holder.editCowButton.setOnClickListener {
            //show dialog
            (context as? CowListActivity)?.showEditCowDialog(cow) // Pass the Cow object
        }

    }

    override fun getItemCount() = cowList.size


    fun updateList(newList: List<CowData>) {
        cowList.clear()
        cowList.addAll(newList)
        notifyDataSetChanged()
    }



}