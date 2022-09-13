package ca.uwaterloo.ofn.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uwaterloo.ofn.MainActivity
import ca.uwaterloo.ofn.model.Produce
import coil.compose.rememberImagePainter
import ca.uwaterloo.ofn.R
import ca.uwaterloo.ofn.Title
import ca.uwaterloo.ofn.model.Seller
import ca.uwaterloo.ofn.ui.components.actionButtons
import ca.uwaterloo.ofn.ui.theme.OpenFoodNetworkTheme
import com.google.firebase.firestore.EventListener

//@Preview(showBackground = true)
@SuppressLint("UnrememberedMutableState")
@Composable
fun ProducePage(navController: NavController, sellerId: String) {
    val produceList = mutableListOf<Produce>()
    val produceState: MutableState<MutableList<Produce>> = mutableStateOf(produceList)
    MainActivity.firestoreDB!!.collection("produce")
        .addSnapshotListener(EventListener { documentSnapshots, e ->
            if (e != null) {
//                Log.e(TAG, "Listen failed!", e)
                return@EventListener
            }
            if (documentSnapshots != null) {
                for (doc in documentSnapshots) {
                    val produce = doc.toObject(Produce::class.java)
                    produce.id = doc.id
                    if (produce.sellerId == sellerId) {
                        produceList.add(produce)
                    }
                }
            }
            // TODO: Remove setContent from here and reactively update data instead
        })

    val sellersName: MutableState<String> = remember { mutableStateOf("") }
    MainActivity.firestoreDB!!.collection("sellers")
        .addSnapshotListener(EventListener { documentSnapshots, e ->
            if (e != null) {
//                Log.e(TAG, "Listen failed!", e)
                return@EventListener
            }
            if (documentSnapshots != null) {
                for (doc in documentSnapshots) {
                    val seller = doc.toObject(Seller::class.java)
                    seller.id = doc.id
                    Log.d("TAG", "seller has name ${seller.name} and id ${seller.id}")
                    if (seller.id == sellerId) {
                        sellersName.value = seller.name.toString()
                    }
                }
            }
        })


    OpenFoodNetworkTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(Modifier.padding(20.dp)) {
                    actionButtons(navController, sellerId)
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Title("${sellersName.value} Daily\r\nProduce")
                    val myList: MutableList<Produce> by remember { produceState }
                    ProduceList(navController, myList)
                }
            }
        }
    }
}

@Composable
fun ProduceItem(navController: NavController, produceId: String, title: String, price: String, stock: String, imageUrlLink: String?, description: String) {
    Box(modifier = Modifier
        .width(285.dp)
        .height(116.dp)
        .clickable { navController.navigate("ViewProduce/${produceId}") }
        .border(1.dp, Color(233, 233, 233), shape = RoundedCornerShape(5.dp))) {
        Row() {
            if(imageUrlLink==null){
                Image(painter = painterResource(id = R.drawable.carrot), contentDescription = null, modifier = Modifier
                    .height(30.dp)
                    .width(30.dp))
            } else {
                Image(
                    painter = rememberImagePainter(
                        data = imageUrlLink,
                        builder = {
                            crossfade(false)
                        }
                    ),
                    contentDescription = null, modifier = Modifier
                        .height(116.dp)
                        .width(100.dp).background(Color(255, 245, 233)))
            }
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp)) {
                Column() {
                    Text(text = "$title", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(bottom = 5.dp))
                    Text(text = "$description", fontSize = 8.sp)
                }
                Row (modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "$$price CAD", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(63, 115, 36))
                    Text(text = "$stock in stock", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(131,131,131))
                }
            }
        }

    }
}

@Composable
fun ProduceList(navController: NavController, produceList: MutableList<Produce>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp), modifier = Modifier.padding(bottom = 30.dp)) {
//        item { ProduceItem("Tomato", "1.40", "10", R.drawable.tomato) }
//        item { ProduceItem("Potato", "1.50", "8", R.drawable.potato) }
//        item { ProduceItem("Lettuce", "0.75", "11", R.drawable.lettuce) }
//        item { ProduceItem("Celery", "1.00", "14", R.drawable.celery) }
        var imageLink: String? = null

        for (produce in produceList) {

            item { if(produce.image==null){
                imageLink = "https://firebasestorage.googleapis.com/v0/b/openfoodnetwork-8d9f4.appspot.com/o/images%2Fveggies.jpg?alt=media&token=14bfae22-429c-43b0-8892-a12a7dc9485f"
            } else {
                imageLink = produce.image.toString()
            }
                ProduceItem(navController, produce.id!!, produce.name!!, produce.price!!, produce.quantity!!, imageLink, produce.description!!) }
        }

    }
}
