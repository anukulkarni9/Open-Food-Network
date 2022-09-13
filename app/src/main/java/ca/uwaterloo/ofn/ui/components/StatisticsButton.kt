package ca.uwaterloo.ofn.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uwaterloo.ofn.R


@Composable
fun StatsButton(title: String, navController: NavController) {
    BottomNavigation(backgroundColor = Color(237,171,91), modifier = Modifier.height(100.dp)) {
        BottomNavigationItem(
            onClick = {
                //your onclick code
                navController.navigate("statisticsPage")
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color(107, 143, 65)),
            label = {
                Column(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$title",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            selected = true,
            icon = {
                Image(
                    painterResource(id = R.drawable.statistics),contentDescription = "none",
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
                    .padding(bottom = 10.dp))
            }
        )
    }
}
