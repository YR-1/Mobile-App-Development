package com.nutritrackpro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.navigation
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.foodIntake.FoodIntakesViewModel
import com.nutritrackpro.data.patients.PatientsViewModel
import com.nutritrackpro.ui.theme.NutriTrackProTheme

class MainActivity : ComponentActivity() {
    /**
     * ViewModel instance that handles data operations and UI state
     * Using by viewModels() delegates the lifecycle management to the activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("nutri_sp", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val userId = prefs.getString("currentUserId", null)
        if (isLoggedIn && userId != null){
            AuthManager.login(applicationContext, userId)
        }

        enableEdgeToEdge()
        setContent {
            // Initialize the PatientsViewModel using ViewModelProvider with a factory pattern
            // This allows the ViewModel to survive configuration changes and maintain state
            val patientsViewModel: PatientsViewModel = ViewModelProvider(
                this, PatientsViewModel.PatientsViewModelFactory(this@MainActivity)
            )[PatientsViewModel::class.java]

            val intakeViewModel: FoodIntakesViewModel = ViewModelProvider(
                this, FoodIntakesViewModel.FoodIntakesViewModelFactory(this@MainActivity)
            )[FoodIntakesViewModel::class.java]

            NutriTrackProTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(innerPadding, intakeViewModel)
                }
            }
        }
    }
}


@Composable
fun WelcomeScreen(innerPadding: PaddingValues, intakeViewModel: FoodIntakesViewModel) {
    LaunchedEffect(Unit) {
        intakeViewModel.getIntake(AuthManager.getPatientId().toString())
    }
    val context = LocalContext.current
    val intake by intakeViewModel.intakeLive.collectAsState()
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(75.dp))
        //app logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NutriTrack",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(35.dp))
        }
        Column(
            modifier = Modifier
                .width(335.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //disclaimer text
            Text(
                text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen.\n" +
                        "Use this app at your own risk.\n" +
                        "If you’d like to an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            val uriHandler = LocalUriHandler.current
            Text(
                text = "https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    uriHandler.openUri("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition")
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        //login button
        Button(
            onClick = {
                if(AuthManager.isLoggedIn()){
                    if (intake != null){
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }else {
                        context.startActivity(Intent(context, QuestionnaireActivity::class.java))
                    }
                }else{
                    context.startActivity(Intent(context, LoginActivity::class.java))
                }
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Designed with \uD83E\uDE77 by Liew Yun Ru (34267824)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

