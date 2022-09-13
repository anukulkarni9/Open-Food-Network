package ca.uwaterloo.ofn.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uwaterloo.ofn.viewmodel.*
import ca.uwaterloo.ofn.model.Sales
import ca.uwaterloo.ofn.model.SalesCallback

@Composable
fun OptionButtons(title1: String, title2: String, navController: NavController) {
    BottomNavigation(backgroundColor = Color(237,171,91), modifier = Modifier.height(50.dp)) {
        BottomNavigationItem(
            onClick = {
                //your onclick code
                navController.navigate("sellersPage")
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color(237, 171, 91))
                .offset(y = (-6).dp),
            label = { Text(text = "$title1", color = Color.White, fontSize = 15.sp,fontWeight = FontWeight.Bold) },
            selected = true,
//            icon = {Image(painterResource(id = R.drawable.produce),contentDescription = "none",
//                modifier = Modifier
//                    .height(40.dp)
//                    .width(40.dp))}
            icon={null}
        )
        BottomNavigationItem(
            onClick = {
                //your onclick code
                var salesList = mutableListOf<Sales>()
                getSales(object: SalesCallback {
                    override fun onCallback(value: MutableList<Sales>) {
                        Log.d("TAG", "OnCallback $value")
                        salesList = value
                        Log.d("TAG", "OnCallback the value of sales list $value")
                    }
                })
                navController.navigate("statisticsPage")
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = Color(107, 143, 65))
                .offset(y = (-6).dp),
            label = { Text(text = "$title2", color = Color.White,fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            selected = true,
//            icon = {Image(painterResource(id = R.drawable.statistics),contentDescription = "none",
//                modifier = Modifier
//                    .height(30.dp)
//                    .width(30.dp)
//                    .padding(bottom = 10.dp))}
            icon={null}
        )
    }
}
