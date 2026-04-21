package com.example.bhumicse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val items: List<ClothingItem>,
    private val onItemSelected: (ClothingItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_item_label)
        val check: ImageView = view.findViewById(R.id.img_selected_badge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.name

        holder.check.visibility =
            if (position == selectedPosition) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()

            // ✅ IMPORTANT: pass FULL item including imageUri
            onItemSelected(
                ClothingItem(
                    id = item.id,
                    name = item.name,
                    category = item.category,
                    color = item.color,
                    imageUri = item.imageUri   // 🔥 THIS IS IMPORTANT
                )
            )
        }
    }

    override fun getItemCount(): Int = items.size
}