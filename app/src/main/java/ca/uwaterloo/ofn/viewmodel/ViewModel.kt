package ca.uwaterloo.ofn.viewmodel

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import ca.uwaterloo.ofn.MainActivity
import ca.uwaterloo.ofn.TAG
import ca.uwaterloo.ofn.model.Produce
import ca.uwaterloo.ofn.model.Sales
import ca.uwaterloo.ofn.model.SalesCallback
import ca.uwaterloo.ofn.model.Seller
import ca.uwaterloo.ofn.ui.theme.OpenFoodNetworkTheme
import coil.compose.rememberImagePainter
import com.google.firebase.storage.FirebaseStorage
import me.bytebeats.views.charts.bar.BarChart
import me.bytebeats.views.charts.bar.BarChartData
import me.bytebeats.views.charts.bar.render.bar.SimpleBarDrawer
import me.bytebeats.views.charts.bar.render.label.SimpleLabelDrawer
import me.bytebeats.views.charts.bar.render.xaxis.SimpleXAxisDrawer
import me.bytebeats.views.charts.bar.render.yaxis.SimpleYAxisDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import java.util.*
import com.google.firebase.firestore.EventListener
import ca.uwaterloo.ofn.R


fun getSales(salesCallback: SalesCallback) {
    val salesList = mutableListOf<Sales>()
    MainActivity.firestoreDB!!.collection("sales")
        .addSnapshotListener(EventListener { documentSnapshots, e ->
            if (e != null) {
//                Log.e(TAG, "Listen failed!", e)
                return@EventListener
            }
            if (documentSnapshots != null) {
                for (doc in documentSnapshots) {
                    val sale = doc.toObject(Sales::class.java)
                    sale.id = doc.id
                    salesList.add(sale)
                }
                salesCallback.onCallback(salesList)
            }
        })
}


@Composable
fun GraphView(salesList: MutableList<Sales>) {
    Column(
        Modifier
            .height(450.dp)
            .padding(20.dp)
    ) {
        val barsForAmountSold : MutableList<BarChartData.Bar> = mutableListOf()
        for (sale in salesList) {
            barsForAmountSold.add(
                BarChartData.Bar(
                label = sale.name!!,
                value = sale.revenue!!.toFloat(),
                color = Color(149,176,135)
            ))
        }

        BarChart(
            barChartData = BarChartData(
                bars = barsForAmountSold
            ),
            // Optional properties.
            modifier = Modifier.fillMaxSize(),
            animation = simpleChartAnimation(),
            barDrawer = SimpleBarDrawer(),
            xAxisDrawer = SimpleXAxisDrawer(),
            yAxisDrawer = SimpleYAxisDrawer(),
            labelDrawer = SimpleLabelDrawer(SimpleLabelDrawer.DrawLocation.XAxis)
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewProduce(navController: NavController, produceId: String) {

    val produceState: MutableState<Produce> = remember { mutableStateOf(Produce()) }
    val sellerListState: MutableState<MutableList<String>> = remember { mutableStateOf(mutableListOf()) }

    MainActivity.firestoreDB!!.collection("produce")
        .addSnapshotListener(EventListener { documentSnapshots, e ->
            if (e != null) {
                return@EventListener
            }
            if (documentSnapshots != null) {
                for (doc in documentSnapshots) {
                    val produce = doc.toObject(Produce::class.java)
                    produce.id = doc.id
                    if (doc.id == produceId) {
                        produceState.value = produce
                    }
                    if (produceState.value.name == produce.name) {
                        Log.d("TAG","added to sellers state")
                        sellerListState.value.add(produce.sellerId.toString())
                    }
                }
            }
        })

    val sellerNamesState: MutableState<MutableList<String>> = remember { mutableStateOf(mutableListOf()) }
    MainActivity.firestoreDB!!.collection("sellers")
        .addSnapshotListener(EventListener { documentSnapshots, e ->
            if (e != null) {
                return@EventListener
            }
            if (documentSnapshots != null) {
                for (doc in documentSnapshots) {
                    val seller = doc.toObject(Seller::class.java)
                    seller.id = doc.id
                    if(sellerListState.value.isNotEmpty()) {
                        if (sellerListState.value.contains(seller.id)) {
                            sellerNamesState.value.add(seller.name.toString())
                        }
                    }
                }
            }
        })

    OpenFoodNetworkTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 15.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick = {
                                    navController.navigate("sellersPage")
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(40.dp),
                                elevation = null,
                            )
                            {
                                Text(text = "X", color = Color(63, 115, 36), fontSize = 15.sp)
                            }
                            Button(
                                onClick = {
                                    navController.navigate("editProduce/${produceId}")
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(149,176,135)),
                                modifier = Modifier
                                    .width(75.dp)
                                    .height(35.dp),
                                elevation = null,
                                shape = RoundedCornerShape(10.dp)
                            )
                            {
                                Text(text = "Edit", color = Color.White, fontSize = 13.sp)
                            }
                        }
                        Column(modifier = Modifier.padding(horizontal = 65.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                modifier = Modifier.padding(top = 0.dp),
                                text = produceState.value.name.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(63, 115, 36)
                            )
                            if(produceState.value.image.toString()==null){
                                Image(painter = painterResource(id = R.drawable.carrot), contentDescription = null, modifier = Modifier
                                    .height(30.dp)
                                    .width(30.dp))
                            } else {
                                Image(
                                    painter = rememberImagePainter(
                                        data = produceState.value.image.toString(),
                                        builder = {
                                            crossfade(false)
                                        }
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(181.dp)
                                        .width(276.dp)
                                        .padding(top = 25.dp)
                                        .background(color = Color(255, 245, 233))
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .padding(top = 15.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${produceState.value.quantity.toString()} in stock",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(131, 131, 131)
                                )
                                Text(
                                    text = "$${produceState.value.price.toString()} CAD",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(63, 115, 36)
                                )
                            }
                            Text(
                                text = produceState.value.description.toString(),
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(top = 40.dp, bottom = 35.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .border(
                                        BorderStroke(1.dp, Color(149, 176, 135)),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .height(145.dp)
                                    .width(276.dp)
                                    .padding(horizontal = 10.dp),
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                ) {

                                    var sellers = sellerNamesState.value.toMutableSet()
                                    var sellersList = sellers.toMutableList()

                                    Text(
                                        text = "Availability",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        modifier = Modifier.padding(bottom = 15.dp, top = 15.dp)
                                    )
                                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                                        itemsIndexed(sellersList) { indexed, item ->
                                            Text(
                                                text = item,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = Color.Black,
                                                modifier = Modifier.padding(bottom = 5.dp)
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditProduce(navController: NavController, produceId: String, sellerId: String) {
    val sellers = listOf("St Jacobs Market","Whole Foods","Bailey's Local Foods","Spruce Ridge Farms","Country Farm","Traditional Market")

    var seller by remember {
        mutableStateOf("")
    }

    var expanded by remember { mutableStateOf(false) }

    val produceState: MutableState<Produce> = remember { mutableStateOf(Produce()) }

    MainActivity.firestoreDB!!.collection("produce")
        .addSnapshotListener(EventListener { documentSnapshots, e ->
            if (e != null) {
                return@EventListener
            }
            if (documentSnapshots != null) {
                Log.d("TAG","FOUND STUFF ${documentSnapshots.size()}")
                for (doc in documentSnapshots) {
                    if (doc.id != produceId) {
                        continue
                    }
                    val produce = doc.toObject(Produce::class.java)
                    produce.id = doc.id
                    Log.d("TAG","produce id is ${produce.id}")
                    produceState.value = produce
                }
            }
        })

    var name: TextFieldValue = TextFieldValue(produceState.value.name.toString())
    var description: TextFieldValue = TextFieldValue(produceState.value.description.toString())
    var price: TextFieldValue = TextFieldValue(produceState.value.price.toString())
    var stock: TextFieldValue = TextFieldValue(produceState.value.quantity.toString())
    var imageUrlLink: TextFieldValue = TextFieldValue(produceState.value.image.toString())

    var nameValue by remember { mutableStateOf(TextFieldValue("")) }
    var descriptionValue by remember { mutableStateOf(TextFieldValue("")) }
    var priceValue by remember { mutableStateOf(TextFieldValue("")) }
    var stockValue by remember { mutableStateOf(TextFieldValue("")) }

    OpenFoodNetworkTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("sellersPage")
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(40.dp),
                                elevation = null,
                            )
                            {
                                Text(
                                    text = "X",
                                    color = Color(63, 115, 36),
                                    fontSize = 15.sp
                                )
                            }
                            Button(
                                onClick = {
                                    val updates = hashMapOf<String, Any>()

                                    if (nameValue.text.isNotEmpty()) {
                                        updates["name"] = nameValue.text
                                    }

                                    if (priceValue.text.isNotEmpty()) {
                                        updates["price"] = priceValue.text
                                    }

                                    if (stockValue.text.isNotEmpty()) {
                                        updates["quantity"] = stockValue.text
                                    }

                                    if (descriptionValue.text.isNotEmpty()) {
                                        updates["description"] = descriptionValue.text
                                    }

                                    MainActivity.firestoreDB.collection("produce").document(produceId).update(updates)
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
                                    .width(75.dp)
                                    .height(35.dp),
                                elevation = null,
                                shape = RoundedCornerShape(10.dp)
                            )
                            {
                                Text(text = "Save", color = Color.White, fontSize = 13.sp)
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 65.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 0.dp)
                                    .fillMaxWidth(),
                                text = "Edit ${name.text}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(63, 115, 36),
                                textAlign = TextAlign.Center
                            )
                            if(produceState.value.image.toString()==null){
                                Image(painter = painterResource(id = R.drawable.carrot), contentDescription = null, modifier = Modifier
                                    .height(30.dp)
                                    .width(30.dp))
                            } else {
                                Image(
                                    painter = rememberImagePainter(
                                        data = produceState.value.image.toString(),
                                        builder = {
                                            crossfade(false)
                                        }
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(181.dp)
                                        .width(276.dp)
                                        .padding(top = 25.dp)
                                        .background(color = Color(255, 245, 233))
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .padding(top = 0.dp)
                                    .fillMaxWidth(),
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                                elevation = null
                            ) {
                                Text(text = "Remove Image", fontSize = 12.sp)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 15.dp)
                            ) {
                                Text(
                                    text = "Name",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        )
                                        .padding(0.dp),
                                    value = nameValue,
                                    onValueChange = {
                                        nameValue = it
                                    },
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    ),
                                    label = null,
                                    placeholder = {
                                        Text(
                                            text = name.text,
                                            fontSize = 10.sp
                                        )
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                )
                            }
                            Column(
                                modifier = Modifier.padding(bottom = 15.dp)
                            ) {
                                Text(
                                    text = "Description",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(75.dp)
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        ),
                                    value = descriptionValue,
                                    onValueChange = {
                                        descriptionValue = it
                                    },
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    ),
                                    label = null,
                                    placeholder = {
                                        Text(
                                            text = description.text,
                                            fontSize = 10.sp
                                        )
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 15.dp)
                            ) {
                                Text(
                                    text = "Price (CAD)",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(70.dp)
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        ),
                                    value = priceValue,
                                    onValueChange = {
                                        priceValue = it
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    ),
                                    label = null,
                                    placeholder = { Text(text = "$${price.text}", fontSize = 10.sp) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Items in Stock",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(50.dp)
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        ),
                                    value = stockValue,
                                    onValueChange = {
                                        stockValue = it
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle.Default.copy(fontSize = 10.sp, color = Color.Black),
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent),
                                    shape = RoundedCornerShape(10.dp),
                                    label = null,
                                    placeholder = { Text(text = stock.text, fontSize = 10.sp) },

                                    )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 30.dp)) {
                                Button(
                                    elevation = null,
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(209,67,67)),
                                    shape = RoundedCornerShape(10.dp),
                                    onClick = {
                                        MainActivity.firestoreDB.collection("produce").document(produceId)
                                            .delete()
                                        navController.navigate("sellersPage")
                                    },
                                    modifier = Modifier
                                        .height(40.dp)
                                        .width(100.dp)
                                ){
                                    Text(
                                    text = "Remove",
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

var imageUri: Uri? = null
var imageUrl: String? = null

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Synchronized
fun AddProduce(navController: NavController, sellerId: String) {
    val sellers = listOf("St Jacobs Market","Whole Foods","Bailey's Local Foods","Spruce Ridge Farms","Country Farm","Traditional Market")
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var image by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var price by remember { mutableStateOf(TextFieldValue("")) }
    var stock by remember { mutableStateOf(TextFieldValue("")) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? -> imageUri = uri}
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

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

    val openDialog = remember { mutableStateOf(false)  }

    if(openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = "Error")
            },
            text = {
                Text("Please complete all required fields")
            },
            confirmButton = {
                null
            },
            dismissButton = {
                Button(
                    onClick = {
                        openDialog.value = false
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(107,143,65))) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    OpenFoodNetworkTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate("sellersPage")
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(40.dp),
                                elevation = null,
                            )
                            {
                                Text(
                                    text = "X",
                                    color = Color(63, 115, 36),
                                    fontSize = 15.sp
                                )
                            }
                            Button(
                                onClick = {
                                    if((name.text.toString() != "") and (price.text.toString() != "") and (stock.text.toString() != "") and (description.text.toString()!= "")) {
                                        if (imageUri != null) {
                                            val filename = UUID.randomUUID().toString()
                                            var imageref =
                                                FirebaseStorage.getInstance().reference.child("/images/$filename")
                                            imageref.putFile(imageUri!!)
                                                .addOnSuccessListener {
                                                    //Log.d("TAG", "Insideloop")
                                                    var result = it.metadata!!.reference!!.downloadUrl;
                                                    // Log.d("TAG", "Insideloopfetchedresult$result")
                                                    result.addOnSuccessListener {
                                                        var imageLink = it.toString()
                                                        Log.d("TAG", "url2$imageLink")
                                                        imageUrl = imageLink

                                                        val produceItem = Produce(
                                                            "1",
                                                            name.text,
                                                            imageUrl,
                                                            price.text,
                                                            stock.text,
                                                            description.text,
                                                            sellerId
                                                        )
                                                        MainActivity.firestoreDB.collection("produce")
                                                            .add(produceItem)
                                                            .addOnSuccessListener { documentReference ->
                                                                Log.d(
                                                                    TAG,
                                                                    "DocumentSnapshot written with ID: ${documentReference.id}"
                                                                )

                                                                imageUri = null
                                                                imageUrl = null
                                                            }
                                                        navController.navigate("sellersPage")
                                                    }
                                                }
                                        }
                                    }
                                    else {
                                        openDialog.value = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(
                                        149,
                                        176,
                                        135
                                    )
                                ),
                                modifier = Modifier
                                    .width(75.dp)
                                    .height(35.dp),
                                elevation = null,
                                shape = RoundedCornerShape(10.dp)
                            )
                            {
                                Text(text = "Save", color = Color.White, fontSize = 13.sp)
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 65.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 0.dp)
                                    .fillMaxWidth(),
                                text = "Add a New Produce",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(63, 115, 36),
                                textAlign = TextAlign.Center
                            )

                            if (imageUri == null) {
                                Box(
                                    modifier = Modifier
                                        .height(181.dp)
                                        .width(276.dp)
                                        .padding(top = 25.dp)
                                        .background(color = Color(217, 217, 217))
                                )
                            } else {
                                imageUri?.let {
                                    if (Build.VERSION.SDK_INT < 28) {
                                        bitmap.value = MediaStore.Images
                                            .Media.getBitmap(context.contentResolver, it)
                                    } else {
                                        val source = ImageDecoder.createSource(context.contentResolver, it)
                                        bitmap.value = ImageDecoder.decodeBitmap(source)
                                    }

                                    bitmap.value?.let { btm ->
                                        Image(
                                            bitmap = btm.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(200.dp)
                                                .padding(20.dp)
                                        )
                                    }
                                }
                            }

                            Button(
                                modifier = Modifier
                                    .padding(top = 0.dp)
                                    .fillMaxWidth(),
                                onClick = {launcher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                                elevation = null
                            ) {
                                Text(text = "Upload an Image", fontSize = 12.sp)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 15.dp)
                            ) {
                                Text(
                                    text = "Name",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        )
                                        .padding(0.dp),
                                    value = name,
                                    onValueChange = {
                                        name = it
                                    },
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    ),
                                    label = null,
                                    placeholder = {
                                        Text(
                                            text = "Enter a produce name",
                                            fontSize = 10.sp
                                        )
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                )
                            }
                            Column(
                                modifier = Modifier.padding(bottom = 15.dp)
                            ) {
                                Text(
                                    text = "Description",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(75.dp)
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        ),
                                    value = description,
                                    onValueChange = {
                                        description = it
                                    },
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    ),
                                    label = null,
                                    placeholder = {
                                        Text(
                                            text = "Enter a produce description",
                                            fontSize = 10.sp
                                        )
                                    },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 15.dp)
                            ) {
                                Text(
                                    text = "Price (CAD)",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(70.dp)
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        ),
                                    value = price,
                                    onValueChange = {
                                        price = it
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle.Default.copy(
                                        fontSize = 10.sp,
                                        color = Color.Black
                                    ),
                                    label = null,
                                    placeholder = { Text(text = "$", fontSize = 10.sp) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.padding(bottom = 30.dp)) {
                                Text(
                                    text = "Items in Stock",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                TextField(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .width(100.dp)
                                        .border(
                                            BorderStroke(1.dp, color = Color.LightGray)
                                        ),
                                    value = stock,
                                    onValueChange = {
                                        stock = it
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle.Default.copy(fontSize = 10.sp, color = Color.Black),
                                    colors = TextFieldDefaults.textFieldColors(
                                        backgroundColor = Color.Transparent),
                                    shape = RoundedCornerShape(10.dp),
                                    label = null,
                                    placeholder = { Text(text = "Stock", fontSize = 10.sp) },

                                    )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Seller Market Platform",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(63,115,36),
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                Text(
                                    text = "${sellersName.value.toString()}",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

private fun uploadImageToFirebaseStorage() {
    if (imageUri == null) return

    if(imageUri!=null) {
        val filename= UUID.randomUUID().toString()
        var imageref = FirebaseStorage.getInstance().reference.child("/images/$filename")
        imageref.putFile(imageUri!!)
            .addOnSuccessListener {
                //Log.d("TAG", "Insideloop")
                var result = it.metadata!!.reference!!.downloadUrl;
                // Log.d("TAG", "Insideloopfetchedresult$result")
                result.addOnSuccessListener {
                    var imageLink = it.toString()
                    Log.d("TAG", "url2$imageLink")
                    imageUrl = imageLink
                }
            }
    }
}