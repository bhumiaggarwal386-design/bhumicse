package com.example.bhumicse

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryItemsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_items)

        // Get category from previous screen
        val category = intent.getStringExtra("category") ?: ""

        // Title
        val title = findViewById<TextView>(R.id.tv_category_title)
        title.text = category

        // ---------------- SAMPLE DATA (TEMP - DB LATER) ----------------
        val allItems = listOf(
            ClothingItem(name = "Red Top", category = "Topwear", color = "Red"),
            ClothingItem(name = "Black T-Shirt", category = "Topwear", color = "Black"),

            ClothingItem(name = "Blue Jeans", category = "Bottomwear", color = "Blue"),
            ClothingItem(name = "Skirt", category = "Bottomwear", color = "Pink"),

            ClothingItem(name = "Watch", category = "Accessories", color = "Black"),
            ClothingItem(name = "Cap", category = "Accessories", color = "White")
        )

        // Filter items
        val filteredItems = allItems.filter {
            it.category.equals(category, ignoreCase = true)
        }

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_category_items)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Button
        val btnNext = findViewById<Button>(R.id.btn_next)
        btnNext.visibility = View.GONE

        // Selected item
        var selectedItem: ClothingItem? = null

        // Adapter
        val adapter = CategoryAdapter(filteredItems) { item ->
            selectedItem = item
            btnNext.visibility = View.VISIBLE
        }

        recyclerView.adapter = adapter

        // Next click
        btnNext.setOnClickListener {
            selectedItem?.let {

                val intent = Intent(this, EditorActivity::class.java)

                intent.putExtra("item_name", it.name)
                intent.putExtra("item_color", it.color)

                // 🔥 THIS WAS MISSING (MAIN BUG)
                intent.putExtra("item_image", it.imageUri)

                startActivity(intent)
            }
        }
    }
}