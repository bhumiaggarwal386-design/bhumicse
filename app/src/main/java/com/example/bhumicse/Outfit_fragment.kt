package com.example.bhumicse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OutfitFragment : Fragment(R.layout.fragment_outfit) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerOutfits)

        val outfitList = listOf(
            "Outfit 1",
            "Outfit 2",
            "Outfit 3",
            "Outfit 4",
            "Outfit 5",
            "Outfit 6"
        )

        val adapter = OutfitAdapter(outfitList)

        recycler.apply {
            layoutManager = GridLayoutManager(context, 2)
            this.adapter = adapter
            setHasFixedSize(true)
        }
    }
    class OutfitAdapter(private val items: List<String>) :
        RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder>() {

        class OutfitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.tvOutfitName)
            val image: ImageView = view.findViewById(R.id.imgOutfit)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_outfit, parent, false)
            return OutfitViewHolder(view)
        }

        override fun onBindViewHolder(holder: OutfitViewHolder, position: Int) {
            holder.name.text = items[position]
            holder.image.setBackgroundColor(0xFFEDEBFF.toInt())
        }

        override fun getItemCount(): Int = items.size
    }
}