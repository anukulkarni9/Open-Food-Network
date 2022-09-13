package ca.uwaterloo.ofn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uwaterloo.ofn.viewmodel.*
import ca.uwaterloo.ofn.Title
import ca.uwaterloo.ofn.model.Sales
import ca.uwaterloo.ofn.ui.theme.OpenFoodNetworkTheme

@Composable
fun StatisticsPage(navController: NavController, salesList: MutableList<Sales>) {
    OpenFoodNetworkTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp), horizontalArrangement = Arrangement.Start) {
                    Button(
                        onClick = {
                            //your onclick code
                            navController.navigate("sellersPage")
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                149,
                                176,
                                135
                            )
                        ),
                        modifier = Modifier
                            .width(70.dp)
                            .height(35.dp),
                        elevation = null,
                        shape = RoundedCornerShape(10.dp)
                    )
                    {
                        Text(text = "Back", color = Color.White, fontSize = 13.sp)
                    }
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Title("Statistics")
                    GraphView(salesList)
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp))
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp),
                        text = "Your Produce Sales",
                        fontSize = 18.sp
                    )
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                        textAlign = TextAlign.Center,
                        text = "The graph above compares total sales across various platforms, the X-axis represents all of the available sellers and the Y-axis represents the total revenue from each seller.",
                        fontSize = 16.sp
                    )

                }
            }
        }

    }
}
