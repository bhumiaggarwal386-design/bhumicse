package com.example.bhumicse

import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.util.*

data class OutfitPlan(
    val date: String,
    val outfitName: String,
    val outfitImage: Int
)

@Composable
fun PlannerScreen() {

    val context = LocalContext.current

    val outfitOptions = listOf(
        Pair("Casual Outfit", R.drawable.outfit1),
        Pair("Party Outfit", R.drawable.outfit2),
        Pair("Formal Outfit", R.drawable.outfit3)
    )

    val outfitPlans = remember { mutableStateListOf<OutfitPlan>() }

    var showDialog by remember { mutableStateOf(false) }
    var selectedOutfitName by remember { mutableStateOf("") }
    var selectedOutfitImage by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf("") }

    var showFullImage by remember { mutableStateOf(false) }
    var fullImageRes by remember { mutableStateOf(0) }

    // Back handling for image screen
    if (showFullImage) {
        BackHandler {
            showFullImage = false
        }
    }

    // ================= FULL IMAGE SCREEN =================
    if (showFullImage) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(16.dp)
        ) {

            Button(onClick = { showFullImage = false }) {
                Text("Back")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = fullImageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        return
    }

    // ================= MAIN PLANNER SCREEN =================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp)
    ) {

        // HEADER ONLY HERE
        AppHeader("Planner")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Outfit")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(outfitPlans) { item ->

                // ✅ ONLY CHANGE: shadow added here
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    ),
                    onClick = {
                        fullImageRes = item.outfitImage
                        showFullImage = true
                    }
                ) {

                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row {

                            Image(
                                painter = painterResource(id = item.outfitImage),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text("Date: ${item.date}")
                                Text("Outfit: ${item.outfitName}")
                            }
                        }

                        TextButton(onClick = {
                            outfitPlans.remove(item)
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    // ================= ADD DIALOG =================
    if (showDialog) {

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Outfit") },
            text = {

                Column {

                    Text("Select Outfit")

                    Spacer(modifier = Modifier.height(8.dp))

                    outfitOptions.forEach { (name, image) ->

                        Row {

                            RadioButton(
                                selected = selectedOutfitName == name,
                                onClick = {
                                    selectedOutfitName = name
                                    selectedOutfitImage = image
                                }
                            )

                            Image(
                                painter = painterResource(id = image),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(name)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = {

                        val cal = Calendar.getInstance()

                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                selectedDate = "$d/${m + 1}/$y"
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()

                    }) {
                        Text(
                            if (selectedDate.isEmpty()) "Pick Date"
                            else selectedDate
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {

                    if (selectedDate.isNotEmpty() && selectedOutfitName.isNotEmpty()) {

                        outfitPlans.add(
                            OutfitPlan(
                                date = selectedDate,
                                outfitName = selectedOutfitName,
                                outfitImage = selectedOutfitImage
                            )
                        )
                    }

                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}