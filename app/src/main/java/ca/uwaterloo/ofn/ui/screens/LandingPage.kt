package ca.uwaterloo.ofn.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.uwaterloo.ofn.ui.theme.OpenFoodNetworkTheme
import ca.uwaterloo.ofn.R


@Composable
fun LandingTitle(title: String) {
    Column {
        Text(
            text = "$title",
            modifier = Modifier.padding(vertical = 0.dp),
            color = Color(63, 115, 36),
            fontWeight = FontWeight.Bold,
            fontSize = 70.sp,
            textAlign = TextAlign.Left
        )
    }
}


@Composable
fun LandingPage(userLoggedIn: MutableState<Boolean>) {
    OpenFoodNetworkTheme {
        Column(
            Modifier
                .background(color = Color(255, 245, 233))
                .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally)
        {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(20.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Row() {
                    LandingTitle("Stalk")
                    Image(painter = painterResource(id = R.drawable.basket), contentDescription = null, modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                        .offset(x = 50.dp))
                }
                LandingTitle("Market")
                Row(){
                    Text(
                        text = "Your produce all in one place",
                        modifier = Modifier.padding(vertical = 0.dp),
                        color = Color(176, 187, 171),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Left
                    )
                    Image(painter = painterResource(id = R.drawable.carrot), contentDescription = null, modifier = Modifier
                        .height(30.dp)
                        .width(30.dp))
                }
            }
            Row(modifier = Modifier.weight(1f, false)) {
                Row() {
                    Button(
                        onClick = {
                            userLoggedIn.value = true
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(149,176,135)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                            .weight(1f),
                        elevation = null,
                        shape = RoundedCornerShape(0.dp)
                    )
                    {
                        Text(text = "Sign In with Meta", color = Color.White, modifier = Modifier.padding(horizontal = 25.dp, vertical = 20.dp), fontSize = 25.sp)
                    }
                }
            }
        }
    }
}
