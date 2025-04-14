package com.techlad.smartdairy.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techlad.smartdairy.R
import com.techlad.smartdairy.data.InseminatedCow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class InseminatedCowAdapter(private val context: Context,
    private val inseminatedCowList: MutableList<InseminatedCow>,
    ): RecyclerView.Adapter<InseminatedCowAdapter.InseminatedCowViewHolder>() {

        class InseminatedCowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            val cowNameTextView: TextView = itemView.findViewById(R.id.inseminatedcowNameTextView)
            val bullBreedTextView : TextView = itemView.findViewById(R.id.bullBreedTextView)
            val pregnancyDurationTextView : TextView = itemView.findViewById(R.id.pregnanyDurationTextView)

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InseminatedCowViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.inseminated_cow_list_item, parent, false) // Create new layout
        return InseminatedCowViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return inseminatedCowList.size
    }

    fun updateList(newList: List<InseminatedCow>) {
        inseminatedCowList.clear()
        inseminatedCowList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun calculatePregnancyDuration(inseminationDate: String?): String {
        if (inseminationDate == null) return "Unknown"
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val inseminationDateParsed = sdf.parse(inseminationDate) ?: return "Invalid Date"
            val currentDate = Date()
            val diff = currentDate.time - inseminationDateParsed.time
            val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
            val months = days / 30 // Approximate months
            return "$days days, $months months"
        } catch (e: Exception) {
            Log.e("InseminatedCowAdapter", "Error calculating duration: ${e.message}")
            return "Error"
        }
    }

    override fun onBindViewHolder(holder: InseminatedCowViewHolder, position: Int) {
        val cow = inseminatedCowList[position]
        holder.cowNameTextView.text = cow.cowName
        holder.bullBreedTextView.text = cow.bullBreed

        // Calculate pregnancy duration
        val duration = calculatePregnancyDuration(cow.inseminationDate)
        holder.pregnancyDurationTextView.text = duration
    }
}