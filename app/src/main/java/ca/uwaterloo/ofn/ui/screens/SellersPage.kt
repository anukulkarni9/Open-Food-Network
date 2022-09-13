package ca.uwaterloo.ofn.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.uwaterloo.ofn.*
import ca.uwaterloo.ofn.model.MenuItem
import ca.uwaterloo.ofn.model.Seller
import ca.uwaterloo.ofn.ui.components.OptionButtons
import ca.uwaterloo.ofn.ui.components.TextGrid
import ca.uwaterloo.ofn.ui.theme.DrawerBody
import ca.uwaterloo.ofn.ui.theme.DrawerHeader
import ca.uwaterloo.ofn.ui.theme.MenuBar
import ca.uwaterloo.ofn.ui.theme.OpenFoodNetworkTheme
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.google.firebase.firestore.EventListener

@Composable
fun SellersPage(navController: NavController, sellers: MutableList<Seller>, context: Context) {
    OpenFoodNetworkTheme {
        Box(
            Modifier
            .fillMaxSize()) {
            Column(
                Modifier
                .fillMaxSize())
            {
                Row(Modifier.padding(20.dp)) {
                    Menu(navController, context)
                }
                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                    Title("Sellers Information")
                    TextGrid(navController, sellers)
                    OptionButtons("View Sellers", "Statistics", navController)
                }
            }
        }

    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Menu(navController: NavController, context: Context) {
    val sellerList = mutableListOf<Seller>()
    val sellerState: MutableState<MutableList<Seller>> = mutableStateOf(sellerList)
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
                    sellerList.add(seller)
                }
            }
            // TODO: Remove setContent from here and reactively update data instead
        })

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MenuBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerElevation = 0.dp,
        drawerContent = {
            DrawerHeader(FirebaseAuth.getInstance().currentUser?.displayName, FirebaseAuth.getInstance().currentUser?.email, FirebaseAuth.getInstance().currentUser?.photoUrl)
            DrawerBody(
                items = listOf(
//                    MenuItem(
//                        id = "home",
//                        title = "Home",
//                        contentDescription = "Go to home screen",
//                        icon = Icons.Default.Home
//                    ),
                    MenuItem(
                        id = "logout",
                        title = "Logout",
                        contentDescription = "Logout from the application",
                        icon = Icons.Default.Refresh
                    ),
                ),

                onItemClick = {
                    userLoggedIn.value = false
                    userLogout.value = true
                    AuthUI.getInstance().delete(context)
                    navController.navigate("landingPage")
                    println("Clicked on ${it.title}")
                }
            )
        },
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Title("Sellers Information")
                val myList: MutableList<Seller> by remember { sellerState }
                TextGrid(navController, myList)
            }
        },
        bottomBar = { OptionButtons("Sellers Information", "Statistics",navController) }
    )
}
