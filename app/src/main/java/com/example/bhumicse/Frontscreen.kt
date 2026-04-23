package com.example.bhumicse

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class FrontActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Test:Backend team is ready

        super.onCreate(savedInstanceState)
        //  enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "front"
            ) {
                composable("front") {
                    Frontscreen(navController)
                }

                composable("second") {
                    SecondScreen()
                }
            }
        }
    }
}
@Composable
fun Frontscreen(navController: NavController){
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0x55000000))
    ) {

        // 🖼️ Background Image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop

        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, bottom = 40.dp),
//                    .background(Color(0x66FFF0F5)), // light pink
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WARDROBE",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 4.sp,
            color = Color(0xFF424242)
        )
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(50.dp)
                .height(2.dp)
                .background(Color(0xFFD81B60))
        )

        Text(
            text = "WIZARD",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 4.sp,
            color = Color(0xFF424242)
        )
        Spacer(modifier = Modifier.height(50.dp))

        //Image
        Image(
            painter = painterResource(id = R.drawable.fashion),
            contentDescription = "Fashion Image",
            modifier = Modifier.size(250.dp).clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )
//                Spacer(modifier = Modifier.height(20.dp))


        // App Name
//                Text(
//                    text = "Wardrobe Wizard 👗",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFFD81B60)
//                )

        Spacer(modifier = Modifier.height(20.dp))

        // Tagline
        Text(
            text = "Style your outfits effortlessly",
//            fontstyle = FontStyle.Italic,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(100.dp))

        // Button
        Button(
            modifier = Modifier
                .width(200.dp)
                .height(60.dp), 
            onClick = { navController.navigate("second") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD81B60)
            ),
            shape = RoundedCornerShape(40.dp)
        ) {
            Text("Get Started",
                color = Color.White,
                        fontSize = 20.sp   )

        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    val navController = rememberNavController()
    Frontscreen(navController)
}
//fun Frontscreen(navController: NavController) {
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        Text("Welcome")
//
//        Button(
//            onClick = {
//                navController.navigate("second")   // 👈 go to second screen
//            }
//        ) {
//            Text("Get Started")
//        }
//    }
//}
