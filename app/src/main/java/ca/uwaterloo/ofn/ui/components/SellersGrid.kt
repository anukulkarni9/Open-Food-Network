package ca.uwaterloo.ofn.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uwaterloo.ofn.model.Seller
import coil.compose.rememberImagePainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextGrid(navController: NavController, sellers: MutableList<Seller>) {
//    val sellers = listOf("St Jacobs Market","Whole Foods","Bailey's Local Foods","Spruce Ridge Farms","Country Farm","Traditional Market")
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.padding(bottom = 50.dp)
    ) {

        itemsIndexed(sellers) { indexed,item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Button(
                    elevation = null,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                    border = BorderStroke(1.dp, Color(233, 233, 233)),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {navController.navigate("producePage/${sellers[indexed].id}")},
                    modifier = Modifier
                        .size(135.dp)
                ){
                    if (item.image.toString().isNotEmpty()) {
                        Log.d("TAG", "image is ${item.image.toString()}")
                        Image(
                            painter = rememberImagePainter(
                                data = item.image.toString(),
                                builder = {
                                    crossfade(false)
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .height(181.dp)
                                .width(300.dp)
                                .padding(top = 0.dp)
                                .background(color = Color(255, 255, 255))
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(vertical = 10.dp),
                    text = "${item.name}",
                    fontSize = 13.sp
                )
            }
        }
    }

}

