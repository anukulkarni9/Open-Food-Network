package ca.uwaterloo.ofn

import android.content.Context
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.compose.ui.Modifier
import ca.uwaterloo.ofn.ui.theme.OpenFoodNetworkTheme
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.rememberCoroutineScope
import ca.uwaterloo.ofn.viewmodel.*
import ca.uwaterloo.ofn.model.*
import ca.uwaterloo.ofn.ui.components.actionButtons
import ca.uwaterloo.ofn.ui.screens.*
import ca.uwaterloo.ofn.ui.theme.DrawerBody
import ca.uwaterloo.ofn.ui.theme.DrawerHeader
import ca.uwaterloo.ofn.ui.theme.MenuBar
import ca.uwaterloo.ofn.ui.theme.OpenFoodNetworkTheme
import coil.compose.rememberImagePainter
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import java.util.*

import kotlinx.coroutines.launch
import me.bytebeats.views.charts.bar.BarChart
import me.bytebeats.views.charts.bar.BarChartData
import me.bytebeats.views.charts.bar.render.bar.SimpleBarDrawer
import me.bytebeats.views.charts.bar.render.label.SimpleLabelDrawer
import me.bytebeats.views.charts.bar.render.xaxis.SimpleXAxisDrawer
import me.bytebeats.views.charts.bar.render.yaxis.SimpleYAxisDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import java.util.logging.Level.INFO


val TAG = "MainActivity"
var userLoggedIn: MutableState<Boolean> = mutableStateOf(false)
var userLogout: MutableState<Boolean> = mutableStateOf(false)

class MainActivity : ComponentActivity() {

    companion object {
        lateinit var firestoreDB: FirebaseFirestore
        lateinit var firestoreListener: ListenerRegistration
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.FacebookBuilder().build())
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
    }

    private fun deleteUser() {
        AuthUI.getInstance().delete(this)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
//        val response = result.idpResponse
//        if (result.resultCode == RESULT_OK) {
//            val user = FirebaseAuth.getInstance().currentUser
//            updateUI()
//        } else {
//            response?.getError()?.getErrorCode().let { Log.d("login_error", it.toString()) }
//        }
        updateUI()
    }

    private fun updateUI() {
        var sel = Seller("lol", "bpi", "kek")
        var list = mutableListOf<Seller>(sel)

        var salesList = mutableListOf<Sales>()
        getSales(object: SalesCallback {
            override fun onCallback(value: MutableList<Sales>) {
                salesList = value
            }
        })
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "sellersPage") {
                composable("sellersPage") { SellersPage(navController, list, this@MainActivity) }
                composable("producePage/{sellerId}",
                    arguments = listOf(navArgument("sellerId") {
                        type = NavType.StringType
                    })) { ProducePage(navController, it.arguments?.getString("sellerId") ?: "") }
                composable("addProducePage/{sellerId}",
                    arguments = listOf(navArgument("sellerId") {
                        type = NavType.StringType
                    })) { AddProduce(navController, it.arguments?.getString("sellerId") ?: "") }
                composable("viewProduce/{produceId}",
                    arguments = listOf(navArgument("produceId") {
                        type = NavType.StringType
                    })) { ViewProduce(navController, it.arguments?.getString("produceId") ?: "") }
                composable("editProduce/{produceId}",
                    arguments = listOf(navArgument("produceId") {
                        type = NavType.StringType
                    })) { EditProduce(navController, it.arguments?.getString("produceId") ?: "", it.arguments?.getString("sellerId") ?: "") }
                composable("statisticsPage") { StatisticsPage(navController, salesList) }
                composable("landingPage") { LandingPage(userLoggedIn)}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestoreDB = FirebaseFirestore.getInstance()
        setContent {
            OpenFoodNetworkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (userLogout.value) {
                        Log.i(TAG, "HELLO TRY TO LOGOUT")
                        deleteUser()
                    }
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        updateUI()
                    } else {
                        if (!userLoggedIn.value) {
                            LandingPage(userLoggedIn)
                        }
                        if (userLoggedIn.value) {
                            Log.i(TAG, "Log before creating sign in intent")
                            createSignInIntent()
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun Title(title: String) {
    Column {
        Text(
            text = "$title",
            modifier = Modifier.padding(vertical = 25.dp),
            color = Color(63, 115, 36),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

