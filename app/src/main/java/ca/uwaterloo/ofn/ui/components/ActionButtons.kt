package ca.uwaterloo.ofn.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun actionButtons(navController: NavController, sellerId: String) {
    Row(modifier = Modifier
        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(
            onClick = {
                //your onclick code
                navController.navigate("sellersPage")
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(149,176,135)),
            modifier = Modifier
                .width(70.dp)
                .height(35.dp),
            elevation = null,
            shape = RoundedCornerShape(10.dp)
        )
        {
            Text(text = "Back", color = Color.White, fontSize = 13.sp)
        }
        Button(
            onClick = {
                navController.navigate("addProducePage/${sellerId}")
                //your onclick code
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(149,176,135)),
            modifier = Modifier
                .width(200.dp)
                .height(35.dp),
            elevation = null,
            shape = RoundedCornerShape(10.dp)
        )
        {
            Text(text = "+ Add a New Produce", color = Color.White, fontSize = 13.sp)
        }
    }
}


