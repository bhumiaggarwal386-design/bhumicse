package com.example.bhumicse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView

class EditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // ---------------- CANVAS ----------------
        val canvas = findViewById<FrameLayout>(R.id.layout_canvas)

        // ---------------- GET DATA ----------------
        val name = intent.getStringExtra("item_name") ?: "Item"
        val imageUri = intent.getStringExtra("item_image")
        println("DEBUG imageUri = $imageUri")

        // ---------------- ADD IMAGE TO CANVAS ----------------
        if (imageUri != null) {

            val imageView = AppCompatImageView(this)

            val size = 400
            val params = FrameLayout.LayoutParams(size, size)
            imageView.layoutParams = params

            imageView.setImageURI(Uri.parse(imageUri))

            // basic placement (center-ish start)
            imageView.x = 200f
            imageView.y = 200f

            canvas.addView(imageView)
        }

        // ---------------- CATEGORY BUTTONS (UNCHANGED) ----------------
        val btnTopwear = findViewById<Button>(R.id.btn_topwear)
        val btnBottomwear = findViewById<Button>(R.id.btn_bottomwear)
        val btnAccessories = findViewById<Button>(R.id.btn_accessories)

        btnTopwear.setOnClickListener {
            val intent = Intent(this, CategoryItemsActivity::class.java)
            intent.putExtra("category", "Topwear")
            startActivity(intent)
        }

        btnBottomwear.setOnClickListener {
            val intent = Intent(this, CategoryItemsActivity::class.java)
            intent.putExtra("category", "Bottomwear")
            startActivity(intent)
        }

        btnAccessories.setOnClickListener {
            val intent = Intent(this, CategoryItemsActivity::class.java)
            intent.putExtra("category", "Accessories")
            startActivity(intent)
        }
    }
}