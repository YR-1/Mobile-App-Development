package com.nutritrackpro

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Portrait
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.HomeViewModel
import com.nutritrackpro.data.foodIntake.FoodIntakesViewModel
import com.nutritrackpro.data.fruitAPI.FruitAPIRepository
import com.nutritrackpro.data.fruitAPI.FruitModel
import com.nutritrackpro.data.genAI.GenAIViewModel
import com.nutritrackpro.data.genAI.UiState
import com.nutritrackpro.data.nutriCoachTips.NutriCoachTipsViewModel
import com.nutritrackpro.data.patients.Patient
import com.nutritrackpro.data.patients.PatientsViewModel
import com.nutritrackpro.ui.theme.NutriTrackProTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val homeViewModel: HomeViewModel = ViewModelProvider(
                this, HomeViewModel.HomeViewModelFactory(this@HomeActivity)
            )[HomeViewModel::class.java]

            val intakeViewModel: FoodIntakesViewModel = ViewModelProvider(
                this, FoodIntakesViewModel.FoodIntakesViewModelFactory(this@HomeActivity)
            )[FoodIntakesViewModel::class.java]

            val patientViewModel: PatientsViewModel = ViewModelProvider(
                this, PatientsViewModel.PatientsViewModelFactory(this@HomeActivity)
            )[PatientsViewModel::class.java]

            val nutriViewModel: NutriCoachTipsViewModel = ViewModelProvider(
                this, NutriCoachTipsViewModel.NutriCoachTipsViewModelFactory(this@HomeActivity)
            )[NutriCoachTipsViewModel::class.java]
            NutriTrackProTheme {
                val navController: NavHostController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { // defines the bottom navigation bar
                        MyBottomAppBar(navController)
                    }
                ) { innerPadding ->
                    // Use Column to place MyNavHost correctly within Scaffold
                    // The padding is applied to ensure content does not overlap with system UI elements
                    // The innerPadding parameter provides the necessary padding values
                    Column(modifier = Modifier.padding(innerPadding)) {
                        // Calls the MyNavHost composable to define the navigation graph
                        MyNavHost(innerPadding, navController, homeViewModel, intakeViewModel, nutriViewModel)
                    }
                }
            }
        }
    }
}

data class userScore(
    val userId: String,
    val sex: String,
    var totalScore: Float,
    val discretionary: Float,
    val vegetables: Float,
    val fruits: Float,
    val grains: Float,
    val wholeGrains: Float,
    val meat: Float,
    val dairy: Float,
    val sodium: Float,
    val alcohol: Float,
    val water: Float,
    val sugar: Float,
    val saturatedFat: Float,
    val unsaturatedFat: Float
)

// MyNavHost Composable function for navigation within the app
@Composable
fun MyNavHost(innerPadding: PaddingValues, navController: NavHostController, homeViewModel: HomeViewModel, intakeViewModel: FoodIntakesViewModel, nutriViewModel: NutriCoachTipsViewModel) {
    // NavHost composable to define the navigation graph
    NavHost(
        // Use the provided NavHostController
        navController = navController,
        // Set the starting destination to "welcome"
        startDestination = "Home"
    ) {
        // Define the composable for the "home" route
        composable("Home") {
            HomeScreen(innerPadding, navController, homeViewModel)
        }
         //Define the composable for the "insights" route
        composable("Insights") {
            InsightsScreen(innerPadding, navController, homeViewModel)
        }
        // Define the composable for the "nutricoach" route
        composable("NutriCoach") {
            NutriCoachScreen(innerPadding, navController, intakeViewModel, homeViewModel, nutriViewModel)
        }
        // Define the composable for the "settings" route
        composable("Settings") {
            SettingsScreen(innerPadding, navController, homeViewModel)
        }
        composable("ClinicianLogin") {
            ClinicianLoginScreen(innerPadding, navController)
        }
        composable("AdminView") {
            AdminViewScreen(innerPadding, navController, homeViewModel)
        }
    }
}

// Composable function for creating the bottom navigation bar
@Composable
fun MyBottomAppBar(navController: NavHostController) {
    // State to track the currently selected item in the bottom navigation bar
    var selectedItem by remember { mutableStateOf(0) }

    // List of navigation items: "home", "insights", "nutricoach", "settings
    val items = listOf("Home", "Insights", "NutriCoach", "Settings")

    // NavigationBar composable to define the bottom navigation bar
    NavigationBar {
        // Iterate through each item in the 'items' List along with its index
        items.forEachIndexed { index, item ->
            // NavigationBarItem for each item in the List
            NavigationBarItem(
                // Define the icon based on the item's name
                icon = {
                    when (item) {
                        // If the item is "home", show the Home icon
                        "Home" -> Icon(Icons.Outlined.Home, contentDescription = "Home", tint = if (selectedItem == index) Color(0xFF590DE3) else Color.Gray)
                        // If the item is "insights", show the Email icon
                        "Insights" -> Icon(painter = painterResource(R.drawable.outline_insights_24), contentDescription = "Insights", tint = if (selectedItem == index) Color(0xFF590DE3) else Color.Gray)
                        // If the item is "nutricoach", show the Email icon
                        "NutriCoach" -> Icon(Icons.Outlined.Person, contentDescription = "NutriCoach", tint = if (selectedItem == index) Color(0xFF590DE3) else Color.Gray)
                        // If the item is "settings", show the Settings icon
                        "Settings" -> Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = if (selectedItem == index) Color(0xFF590DE3) else Color.Gray)
                    }
                },
                // Display the item's name as the label
                label = { Text(item) },
                // Determine if this item is currently selected
                selected = selectedItem == index,
                // Actions to perform when this item is clicked
                onClick = {
                    // Update the selectedItem state to the current index
                    selectedItem = index
                    // Navigate to the corresponding screen based on the item's name
                    navController.navigate(item)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF590DE3),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF590DE3),
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun HomeScreen(innerPadding: PaddingValues, navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val patient by viewModel.patient.collectAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        viewModel.getPatient(AuthManager.getPatientId().toString())
    }

    if (patient == null) {
        CircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
    //        verticalArrangement = Arrangement.Center,
    //        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hello,",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
            )
            patient!!.name?.let {
                Text(
                    text = it,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            // top row, with edit button
            Row(modifier = Modifier.padding(vertical = 1.dp)) {
                Column(
                    modifier = Modifier.padding(0.dp).height(45.dp).width(300.dp)
                ) {
                    Text(
                        text = "You've already filled in your Food Intake Questionnaire, but you can change details here:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(
                    modifier = Modifier.padding(0.dp).height(45.dp).width(120.dp)
                ) {
                    Button(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context,
                                    QuestionnaireActivity::class.java
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            Color(0xFF590DE3),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = null, // decorative icon
                            modifier = Modifier.size(22.dp).padding(start = 6.dp, end = 1.dp)
                        )
                        Text(
                            "Edit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 1.dp, end = 6.dp)
                        )
                    }

                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(0.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.nutri),
                    contentDescription = "",
                    modifier = Modifier.size(250.dp).padding(0.dp),
                    alignment = Alignment.Center
                )
            }
            //show button navigate to insights
            Row(
                modifier = Modifier.fillMaxWidth().padding(0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(0.dp)
                )
                TextButton(onClick = { navController.navigate("Insights") }) {
                    Text(
                        "See all scores ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }
            }
            // show food quality score
            Row(modifier = Modifier.padding(vertical = 1.dp)) {
                Icon(
                    painter = painterResource(R.drawable.outline_arrow_upward_24),
                    contentDescription = null,
                    tint = Color(0xFF595B62),
                    modifier = Modifier.background(
                        shape = CircleShape,
                        color = Color(0xFFF1F2F4)
                    ).padding(6.dp)
                )
                Text(
                    "Your Food Quality score",
                    fontSize = 16.sp,
                    //fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 18.dp, end = 42.dp)
                )
                Text(
                    text = "${patient!!.totalScore}/100",
                    fontSize = 16.sp,
                    color = Color(0xFF638E69),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(1.dp)
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            Text(
                "What is the Food Quality Score?",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                //modifier = Modifier.padding(start = 18.dp, end = 42.dp)
            )
            Text(
                "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                "This personalized measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// function for displaying the insights screen
@Composable
fun InsightsScreen(innerPadding: PaddingValues, navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val patient by viewModel.patient.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getPatient(AuthManager.getPatientId().toString())
    }
    val insightItems = remember {
        mutableListOf(
            InsightItem("Vegetables", patient!!.vegetablesScore, 10f),
            InsightItem("Fruits", patient!!.fruitsScore, 10f),
            InsightItem("Grains & Cereals", patient!!.grainsScore, 5f),
            InsightItem("Whole Grains", patient!!.wholeGrainsScore, 5f),
            InsightItem("Meat & Alternatives", patient!!.meatScore, 10f),
            InsightItem("Dairy", patient!!.dairyScore, 10f),
            InsightItem("Water", patient!!.waterScore, 5f),
            InsightItem("Unsaturated Fats", patient!!.unsaturatedFatScore, 5f),
            InsightItem("Saturated Fats", patient!!.saturatedFatScore, 5f),
            InsightItem("Sodium", patient!!.sodiumScore, 10f),
            InsightItem("Sugar", patient!!.sugarScore, 10f),
            InsightItem("Alcohol", patient!!.alcoholScore, 5f),
            InsightItem("Discretionary Foods", patient!!.discretionaryScore, 10f),
        )
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            //.verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Insights: Food Score",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(30.dp))
        LazyColumn (
            modifier = Modifier.fillMaxWidth()
        ) {
            items(insightItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(133.dp).padding(end = 8.dp)
                    )
                    Slider(
                        value = item.score,
                        onValueChange = { item.score = it },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF590DE3),
                            inactiveTrackColor = Color(0xFFC9AEF3)
                        ),
                        steps = 10,
                        enabled = true,
                        valueRange = 0f..item.max,
                        modifier = Modifier.width(170.dp).height(30.dp)
                    )
                    Text(
                        text = "${item.score}/${item.max.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(60.dp).padding(start = 8.dp, end = 0.dp),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Total Food Quality Score",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Start
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp, horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Slider(
                value = patient!!.totalScore,
                onValueChange = { },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF590DE3),
                    inactiveTrackColor = Color(0xFFC9AEF3)
                ),
                steps = 10,
                enabled = true,
                valueRange = 0f..100f,
                modifier = Modifier.width(280.dp).height(45.dp)
            )
            Text(
                text = "${patient!!.totalScore}/100",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(80.dp).padding(start = 8.dp, end = 0.dp),
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        //share button
        Row(
            horizontalArrangement = Arrangement.Center,
            //modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ){
            Button(
                onClick = {// Create a intent to share the text
                    val shareText = "Hi, I just got a HEIFA score of ${patient!!.totalScore.toInt()} ~"
                    val shareIntent = Intent(ACTION_SEND)
                    // Set the type of data to share
                    shareIntent.type = "text/plain"
                    // Set the data to share, in this case, the text
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                    // Start the activity to share the text, with a chooser to select the app
                    context.startActivity(Intent.createChooser(shareIntent, "Share text via"))

                },
                colors = ButtonDefaults.buttonColors(
                    Color(0xFF590DE3),
                    contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_share_24),
                    contentDescription = null, // decorative icon
                    modifier = Modifier.size(24.dp).padding(horizontal = 4.dp)
                )
                Text("Share with someone", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        //improve diet, nutricoach button
        Row(
            horizontalArrangement = Arrangement.Center,
            //modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ){
            Button(
                onClick = {
                    navController.navigate("NutriCoach")
                },
                colors = ButtonDefaults.buttonColors(
                    Color(0xFF590DE3),
                    contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_rocket_launch_24),
                    contentDescription = null, // decorative icon
                    modifier = Modifier.size(24.dp).padding(horizontal = 4.dp)
                )
                Text("Improve my diet!", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class InsightItem(
    val category: String,
    var score: Float,
    val max: Float
)

@Composable
fun SettingsScreen(innerPadding: PaddingValues, navController: NavHostController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val patient by viewModel.patient.collectAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        viewModel.getPatient(AuthManager.getPatientId().toString())
    }
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ACCOUNT",
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        SettingsItem(
            icon = { Icon(
                Icons.Outlined.Portrait,
                contentDescription = null)
                   },
            text = patient!!.name.toString(),
            onClick = {}
        )
        SettingsItem(
            icon = { Icon(
                Icons.Outlined.Phone,
                contentDescription = null)
            },
            text = patient!!.phoneNumber,
            onClick = {}
        )
        SettingsItem(
            icon = { Icon(
                Icons.Outlined.Badge,
                contentDescription = null)
            },
            text = patient!!.userId,
            onClick = {}
        )
        Divider(Modifier.padding(vertical = 24.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "OTHER SETTINGS",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Left
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        SettingsItem(
            icon = { Icon(Icons.Outlined.Logout, contentDescription = null) },
            text = "Logout",
            trailing = {
                Icon(
                    Icons.Outlined.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            onClick = {
                AuthManager.logout(context)
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        )
        SettingsItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            text = "Clinician Login",
            trailing = {
                Icon(
                    Icons.Outlined.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            onClick = {
                if(AuthManager.isClinicianLoggedIn()) {
                    navController.navigate("AdminView")
                }else{
                    navController.navigate("ClinicianLogin")
                }
            }
        )

    }
}

@Composable
private fun SettingsItem(icon: @Composable () -> Unit,
    text: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        icon()
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
        )
        trailing?.invoke()
    }
}

@Composable
fun NutriCoachScreen(innerPadding: PaddingValues, navController: NavHostController, intakeViewModel: FoodIntakesViewModel, homeViewModel: HomeViewModel, nutriViewModel: NutriCoachTipsViewModel, genAiViewModel: GenAIViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var repository: FruitAPIRepository = FruitAPIRepository()
    var fruitName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var fruitDetail by remember { mutableStateOf<FruitModel?>(null) }
    var showCircular by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    val allTips by nutriViewModel.allTips.collectAsState()
    var result by rememberSaveable { mutableStateOf("(Message will appear here)") }
    val uiState by genAiViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        intakeViewModel.getIntake(AuthManager.getPatientId().toString())
    }
    LaunchedEffect(Unit) {
        homeViewModel.getPatient(AuthManager.getPatientId().toString())
    }
    LaunchedEffect(Unit) {
        nutriViewModel.getAllTips(AuthManager.getPatientId().toString())
    }
    val intake by intakeViewModel.intakeLive.collectAsState()
    val patient by homeViewModel.patient.collectAsState()
    var prompt = """
        Patient Profile:
        - Name: ${patient?.name}
        - Sex: ${patient?.sex}
        - Total HEIFA Score: ${"%.2f".format(patient?.totalScore)} / 100
        - HEIFA Discretionary Score: ${"%.2f".format(patient?.discretionaryScore)} / 10
        - HEIFA Vegetables Score: ${"%.2f".format(patient?.vegetablesScore)} / 10
        - HEIFA Fruits Score: ${"%.2f".format(patient?.fruitsScore)} / 10
        - HEIFA Grains Score: ${"%.2f".format(patient?.grainsScore)} / 5
        - HEIFA WholeGrains Score: ${"%.2f".format(patient?.wholeGrainsScore)} / 5
        - HEIFA Meat Score: ${"%.2f".format(patient?.meatScore)} / 10
        - HEIFA Dairy Score: ${"%.2f".format(patient?.dairyScore)} / 10
        - HEIFA Sodium Score: ${"%.2f".format(patient?.sodiumScore)} / 10
        - HEIFA Alcohol Score: ${"%.2f".format(patient?.alcoholScore)} / 5
        - HEIFA Water Score: ${"%.2f".format(patient?.waterScore)} / 5
        - HEIFA Sugar Score: ${"%.2f".format(patient?.sugarScore)} / 10
        - HEIFA Saturated Fat Score: ${"%.2f".format(patient?.saturatedFatScore)} / 5
        - HEIFA Unsaturated Fat Score: ${"%.2f".format(patient?.unsaturatedFatScore)} / 5
       
       and the patient's food intake:
       - Whether the patient can consumes the food:
            - Vegetables: ${intake?.vegetables}
            - Grains: ${intake?.grains}
            - RedMeat: ${intake?.redMeat}
            - Seafood: ${intake?.seafood}
            - Poultry: ${intake?.poultry}
            - Fish: ${intake?.fish}
            - Eggs: ${intake?.eggs}
            - Nuts/Seeds: ${intake?.nutsSeeds}
       - The persona best fits the patient: ${intake?.bestPersona}
       - The biggest meal time of the patient: ${intake?.mealTime}
       - The sleep time of the patient: ${intake?.sleepTime}
       - The wake time of the patient: ${intake?.wakeTime}
       )
       
        Based on these results, generate a short (2–3 sentence) encouraging message to help someone improve their fruit intake.
      """.trimIndent()

    if (patient == null) {
        CircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(scrollState)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NutriCoach",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (patient!!.fruitServeSize < 2 || patient!!.fruitVariationsScore < 5) {
                Column(
                    modifier = Modifier
                        .height(340.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)
                    ) {
                        Text(
                            text = "Fruit Name",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 1.dp, bottom = 10.dp)
                    ) {
                        OutlinedTextField(
                            value = fruitName,
                            onValueChange = { fruitName = it },
                            singleLine = true,
                            placeholder = { Text("Enter a fruit name") },
                            modifier = Modifier.width(230.dp)//.height(20.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(
                            onClick = {
                                fruitDetail = null
                                coroutineScope.launch {
                                    var fruit = repository.getDetails(fruitName)
                                    fruitDetail = fruit
                                }
                                if (fruitDetail == null) {
                                    showCircular = true
                                } else {
                                    showCircular = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                Color(0xFF590DE3),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(13.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = null, // decorative icon
                                modifier = Modifier.size(25.dp)//.padding(start = 8.dp, end = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Details",
                                fontSize = 18.sp
                                //modifier = Modifier.padding(start = 3.dp, end = 8.dp)
                            )
                        }
                    }

                    if (showCircular && fruitDetail == null) {
                        CircularProgressIndicator()
                    }
                    if (fruitDetail != null) {
                        Column(
//                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                        ) {
                            FruitInfo(
                                text = "Family",
                                data = fruitDetail?.family.toString()
                            )
                            FruitInfo(
                                text = "Calories",
                                data = "${fruitDetail?.nutritions?.get("calories")}"
                            )
                            FruitInfo(
                                text = "Fat",
                                data = "${fruitDetail?.nutritions?.get("fat")}"
                            )
                            FruitInfo(
                                text = "Sugar",
                                data = "${fruitDetail?.nutritions?.get("sugar")}"
                            )
                            FruitInfo(
                                text = "Carbohydrates",
                                data = "${fruitDetail?.nutritions?.get("carbohydrates")}"
                            )
                            FruitInfo(
                                text = "Protein",
                                data = "${fruitDetail?.nutritions?.get("protein")}"
                            )
                        }
                    }
                }
            } else {
                AsyncImage(
                    model = "https://picsum.photos/300",
                    contentDescription = "Random image",
                    modifier = Modifier
                        .height(340.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
//                    //contentScale = ContentScale.Crop,
//                    placeholder = painterResource(R.drawable.placeholder),
//                    error = painterResource(R.drawable.error)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Divider(Modifier.padding(vertical = 2.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                modifier = Modifier.height(270.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        genAiViewModel.sendPrompt(prompt)
                    },
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFF590DE3),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(13.dp),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null, // decorative icon
                        modifier = Modifier.size(25.dp)//.padding(start = 8.dp, end = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Motivational Message (AI)",
                        fontSize = 18.sp
                        //modifier = Modifier.padding(start = 3.dp, end = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (uiState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    var textColor = MaterialTheme.colorScheme.onSurface
                    if (uiState is UiState.Error) {
                        textColor = MaterialTheme.colorScheme.error
                        result = (uiState as UiState.Error).errorMessage
                    } else if (uiState is UiState.Success) {
                        textColor = MaterialTheme.colorScheme.onSurface
                        result = (uiState as UiState.Success).outputText
                        LaunchedEffect(result) {
                            result.let {
                                nutriViewModel.saveTip(
                                    AuthManager.getPatientId().toString(),
                                    it
                                )
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        Text(
                            text = result,
                            textAlign = TextAlign.Start,
                            color = textColor,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 0.dp)
            ) {
                Button(
                    onClick = {
                        showModal = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFF590DE3),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 0.dp).align(Alignment.End),
                    contentPadding = PaddingValues(13.dp)
                ) {
                    Text(
                        "Show All Tips",
                        fontSize = 18.sp
                        //modifier = Modifier.padding()
                    )
                }
                if (showModal) {
                    AlertDialog(
                        // switch the visibility of the dialog to false when the user dismisses it
                        onDismissRequest = { showModal = false },
                        title = { },
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "AI Tips",
                                    textAlign = TextAlign.Left,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(6.dp)
                                )
                                LazyColumn {
                                    items(allTips) { tip ->
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp, horizontal = 2.dp),
                                            color = Color.White,
                                            shape = RoundedCornerShape(4.dp),
                                            tonalElevation = 2.dp,
                                            border = BorderStroke(2.dp, Color.Gray)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 5.dp)
                                            ) {
                                                Text(
                                                    text = tip.tip,
                                                    fontSize = 15.sp,
                                                    modifier = Modifier.weight(1f)
                                                        .padding(vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            // if the user clicks on the dismiss button,
                            // close the dialog
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(2.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End
                            ) {
                                Button(
                                    onClick = { showModal = false },
                                    colors = ButtonDefaults.buttonColors(
                                        Color(0xFF590DE3),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.padding(0.dp),

                                    ) {
                                    Text("Done")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FruitInfo(
    text: String,
    data: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 2.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f).padding(vertical = 4.dp)
            )
            Text(
                text = ":",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = data,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f).padding(vertical = 4.dp)
            )
        }
    }
}


@Composable
fun ClinicianLoginScreen(innerPadding: PaddingValues, navController: NavHostController) {
    val context = LocalContext.current
    val key = "dollar-entry-apples"
    var clinicianKey by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Clinician Login",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = clinicianKey,
            onValueChange = {
                clinicianKey = it
                if (clinicianKey.isEmpty() || !clinicianKey.isEmpty()) {
                    showError = false
                }
            },
            label = {
                Text(
                    text = "Clinician Key",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your clinician key") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = showError,
            modifier = Modifier.fillMaxWidth()
        )

        if (showError) {
            Text(
                text = "Invalid clinician key",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                if (clinicianKey.trim() == key) {
                    AuthManager.clinicianLogin()
                    navController.navigate("AdminView")
                } else {
                    showError = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            //shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(13.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Outlined.Login,
                contentDescription = null, // decorative icon
                modifier = Modifier.size(25.dp)//.padding(start = 8.dp, end = 1.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Clinician Login",
                fontSize = 18.sp
                //modifier = Modifier.padding(start = 3.dp, end = 8.dp)
            )
        }
    }
}

@Composable
fun AdminViewScreen(innerPadding: PaddingValues, navController: NavHostController, homeViewModel: HomeViewModel, genAiViewModel: GenAIViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val femaleAverage by homeViewModel.femaleAvg
    val maleAverage by homeViewModel.maleAvg
    var showCircular by remember { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf("") }
    val uiState by genAiViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.getAverageHeifa()
        homeViewModel.getPatients()
    }
    val allPatients by homeViewModel.allPatients
    var prompt = buildString {
        appendLine("Below is a list of patients with their HEIFA scores:")
        appendLine()
        allPatients.forEachIndexed { index, patient ->
            appendLine("- Patient ${index + 1}:")
            appendLine("    - Total HEIFA Score: ${"%.2f".format(patient.totalScore)} / 100")
            appendLine("    - HEIFA Discretionary Score: ${"%.2f".format(patient.discretionaryScore)} / 10")
            appendLine("    - HEIFA Vegetables Score: ${"%.2f".format(patient.vegetablesScore)} / 10")
            appendLine("    - HEIFA Fruits Score: ${"%.2f".format(patient.fruitsScore)} / 10")
            appendLine("    - HEIFA Grains Score: ${"%.2f".format(patient.grainsScore)} / 5")
            appendLine("    - HEIFA WholeGrains Score: ${"%.2f".format(patient.wholeGrainsScore)} / 5")
            appendLine("    - HEIFA Meat Score: ${"%.2f".format(patient.meatScore)} / 10")
            appendLine("    - HEIFA Dairy Score: ${"%.2f".format(patient.dairyScore)} / 10")
            appendLine("    - HEIFA Sodium Score: ${"%.2f".format(patient.sodiumScore)} / 10")
            appendLine("    - HEIFA Alcohol Score: ${"%.2f".format(patient.alcoholScore)} / 5")
            appendLine("    - HEIFA Water Score: ${"%.2f".format(patient.waterScore)} / 5")
            appendLine("    - HEIFA Sugar Score: ${"%.2f".format(patient.sugarScore)} / 10")
            appendLine("    - HEIFA Saturated Fat Score: ${"%.2f".format(patient.saturatedFatScore)} / 5")
            appendLine("    - HEIFA Unsaturated Fat Score: ${"%.2f".format(patient.unsaturatedFatScore)} / 5")
            appendLine()
        }
        appendLine("""
            - Please generate exactly three short, clear insight (1–2 sentences). Each describe an interesting pattern you observe across these patients’ scores.
            - Format as bullet point:
               - [Main Finding] ([Supporting Metric])
            Example:
            - Variable Water Intake: Consumption of water varies greatly
            among the users in this dataset, with scores ranging from 0 to
            100. There isn't a clear, immediate correlation in this small sample
            between water intake score and the overall HEIFA score, though
            some high scorers did have high water intake.
        """.trimIndent())
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Clinician Dashboard",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))
        femaleAverage?.let { AvgInfo("Average HEIFA (Female)", it) }
        Spacer(modifier = Modifier.height(10.dp))
        maleAverage?.let { AvgInfo("Average HEIFA (Male)", it) }
        Spacer(modifier = Modifier.height(15.dp))
        Divider(Modifier.padding(vertical = 2.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier.height(505.dp).verticalScroll(scrollState)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    genAiViewModel.sendPrompt(prompt)
                },
                colors = ButtonDefaults.buttonColors(
                    Color(0xFF590DE3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(13.dp)
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null, // decorative icon
                    modifier = Modifier.size(25.dp)//.padding(start = 8.dp, end = 1.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Find Data Pattern",
                    fontSize = 18.sp
                    //modifier = Modifier.padding(start = 3.dp, end = 8.dp)
                )
            }
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                var textColor = MaterialTheme.colorScheme.onSurface
                if (uiState is UiState.Error) {
                    textColor = MaterialTheme.colorScheme.error
                    result = (uiState as UiState.Error).errorMessage
                } else if (uiState is UiState.Success) {
                    textColor = MaterialTheme.colorScheme.onSurface
                    result = (uiState as UiState.Success).outputText
                }
                val scrollState = rememberScrollState()
                val patterns = fetchDataPatterns(result)
                Spacer(modifier = Modifier.height(10.dp))
                patterns.forEach { pattern ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 3.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(4.dp),
                        tonalElevation = 1.dp,
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                        ) {
                            Text(
                                text = pattern.replace("**", "").replace("* ", ""),
                                textAlign = TextAlign.Start,
                                color = textColor,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }

                }
            }
        }
        Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 0.dp)
                )
        {
            Button(
                onClick = {
                    AuthManager.clinicianLogout()
                    navController.navigate("ClinicianLogin")
                },
                colors = ButtonDefaults.buttonColors(
                    Color(0xFF590DE3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 0.dp).align(Alignment.End),
                contentPadding = PaddingValues(13.dp)
            ) {
                Text(
                    "Done",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun AvgInfo(
    text: String,
    data: Float
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp, horizontal = 0.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(200.dp).padding(vertical = 4.dp)
            )
            Text(
                text = ":",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = String.format("%.1f", data),
                fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun fetchDataPatterns(result: String): List<String> {
    return result.lines()
            .map { it.trim().removePrefix("-").removePrefix("•") }
            .filter { it.isNotBlank() }
            .take(3)
}
