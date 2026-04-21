package com.example.bhumicse

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class OutfitFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_outfit, container, false)

        val btnAddOutfit = view.findViewById<Button>(R.id.btnAddOutfit)

        btnAddOutfit.setOnClickListener {
            val intent = Intent(requireContext(), EditorActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}