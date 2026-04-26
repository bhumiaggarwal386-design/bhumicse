package com.example.bhumicse

import android.app.DatePickerDialog
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

// ─────────────────────────────────────────────────────────────

data class OutfitPlan(
    val date: String,
    val outfitName: String,
    val outfitImage: Int
)

// ── JSON STORAGE ─────────────────────────────────────────────

private const val PLANNER_FILE = "planner_items.json"

private fun savePlans(context: Context, plans: List<OutfitPlan>) {
    val jsonArray = JSONArray()
    plans.forEach {
        val obj = JSONObject().apply {
            put("date", it.date)
            put("name", it.outfitName)
            put("image", it.outfitImage)
        }
        jsonArray.put(obj)
    }
    File(context.filesDir, PLANNER_FILE).writeText(jsonArray.toString())
}

private fun loadPlans(context: Context): List<OutfitPlan> {
    val file = File(context.filesDir, PLANNER_FILE)
    if (!file.exists()) return emptyList()

    return try {
        val jsonArray = JSONArray(file.readText())
        (0 until jsonArray.length()).map {
            val obj = jsonArray.getJSONObject(it)
            OutfitPlan(
                date = obj.getString("date"),
                outfitName = obj.getString("name"),
                outfitImage = obj.getInt("image")
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

// ─────────────────────────────────────────────────────────────

@Composable
fun PlannerScreen1() {

    val context = LocalContext.current

    val outfitOptions = listOf(
        Pair("Casual Outfit", R.drawable.outfit1),
        Pair("Party Outfit", R.drawable.outfit2),
        Pair("Formal Outfit", R.drawable.outfit3)
    )

    // ✅ LOAD SAVED DATA
    val outfitPlans = remember {
        mutableStateListOf<OutfitPlan>().also {
            it.addAll(loadPlans(context))
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedOutfitName by remember { mutableStateOf("") }
    var selectedOutfitImage by remember { mutableStateOf(0) }
    var selectedDate by remember { mutableStateOf("") }

    var showFullImage by remember { mutableStateOf(false) }
    var fullImageRes by remember { mutableStateOf(0) }

    if (showFullImage) {
        BackHandler { showFullImage = false }
    }

    // ── Full image screen ───────────────────────────────────
    if (showFullImage) {
        Scaffold(
            topBar = { AppHeader("Planner") }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Button(onClick = { showFullImage = false }) {
                    Text("← Back")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = fullImageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        return
    }

    // ── Main screen ─────────────────────────────────────────
    Scaffold(
        topBar = { AppHeader("Planner") }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Outfit to Planner")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (outfitPlans.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No outfits planned yet 📅",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(outfitPlans) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = MaterialTheme.shapes.large,
                            onClick = {
                                fullImageRes = item.outfitImage
                                showFullImage = true
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = item.outfitImage),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = item.outfitName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "📅 ${item.date}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        outfitPlans.remove(item)
                                        savePlans(context, outfitPlans) // ✅ SAVE AFTER DELETE
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Add dialog ─────────────────────────────────────────
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Plan an Outfit", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Select Outfit",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    outfitOptions.forEach { (name, image) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                    OutlinedButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, y, m, d -> selectedDate = "$d/${m + 1}/$y" },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedDate.isEmpty()) "📅 Pick a Date" else "📅 $selectedDate")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedDate.isNotEmpty() && selectedOutfitName.isNotEmpty()) {
                            outfitPlans.add(
                                OutfitPlan(
                                    date = selectedDate,
                                    outfitName = selectedOutfitName,
                                    outfitImage = selectedOutfitImage
                                )
                            )
                            savePlans(context, outfitPlans) // ✅ SAVE AFTER ADD
                        }
                        showDialog = false
                    }
                ) {
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
