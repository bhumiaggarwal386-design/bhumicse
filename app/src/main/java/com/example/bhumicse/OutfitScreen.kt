package com.example.bhumicse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun OutfitScreen(
    onAddClick: () -> Unit,
    onEditClick: (Outfit) -> Unit
) {

    val outfits = remember {
        mutableStateListOf<Outfit>().apply { addAll(SampleData.outfits) }
    }

    Scaffold(
        topBar = {
            AppHeader("My Wardrobe")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Outfit")
            }
        }
    ) { padding ->

        if (outfits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No outfits yet 👀")
            }
        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp)
            ) {
                items(outfits) { outfit ->
                    OutfitCard(
                        outfit = outfit,

                        onDelete = {
                            val index = outfits.indexOf(outfit)
                            if (index != -1) {
                                outfits.removeAt(index)
                            }
                        },

                        onEdit = {
                            onEditClick(outfit)
                        },

                        onRatingChange = { newRating: Int ->
                            val index = outfits.indexOf(outfit)
                            if (index != -1) {
                                outfits[index] = outfit.copy(rating = newRating)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OutfitCard(
    outfit: Outfit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onRatingChange: (Int) -> Unit
) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {

            if (outfit.items.isNotEmpty()) {

                val firstItem = outfit.items.first()
                val imageRes = SampleData.items
                    .firstOrNull { it.id == firstItem.itemId }
                    ?.imageRes

                if (imageRes != null) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Image Missing")
                    }
                }

            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = outfit.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row {
                for (i in 1..5) {

                    Icon(
                        imageVector = if (i <= outfit.rating)
                            Icons.Filled.Star
                        else
                            Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onRatingChange(i)
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}