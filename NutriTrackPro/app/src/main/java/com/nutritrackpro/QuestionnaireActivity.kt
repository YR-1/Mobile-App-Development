package com.nutritrackpro

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutritrackpro.ui.theme.NutriTrackProTheme
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.foodIntake.FoodIntakesViewModel
import com.nutritrackpro.data.patients.PatientsViewModel

class QuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Initialize the PatientsViewModel using ViewModelProvider with a factory pattern
            // This allows the ViewModel to survive configuration changes and maintain state
            val foodIntakesViewModel: FoodIntakesViewModel = ViewModelProvider(
                this, FoodIntakesViewModel.FoodIntakesViewModelFactory(this@QuestionnaireActivity)
            )[FoodIntakesViewModel::class.java]
            NutriTrackProTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FoodIntakeQuestionnaireScreen(innerPadding, foodIntakesViewModel)
                }
            }
        }
    }
}


data class Persona(
    var title: String,
    val description: String,
    val imageId: Int
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodIntakeQuestionnaireScreen(innerPadding: PaddingValues, foodIntakesViewModel: FoodIntakesViewModel) {
    val scrollState = rememberScrollState()
    // rememberTopAppBarState() is a composable function that creates a TopAppBarState that is remembered across compositions.
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry", "Fish", "Eggs", "Nuts/Seeds")
    var error by remember { mutableStateOf(false) }
    val personas = remember {
        mutableListOf(
            Persona("Health Devotee", "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.", R.drawable.persona_1),
            Persona("Mindful Eater", "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.", R.drawable.persona_2),
            Persona("Wellness Striver", "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.", R.drawable.persona_3),
            Persona("Balance Seeker", "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.", R.drawable.persona_4),
            Persona("Health Procrastinator", "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.", R.drawable.persona_5),
            Persona("Food Carefree", "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat.", R.drawable.persona_6)
        )
    }

    var isExpanded by remember { mutableStateOf(false) }

    val mContext = LocalContext.current

    LaunchedEffect(Unit) {
        foodIntakesViewModel.loadQuestionnaire(AuthManager.getPatientId().toString())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                // Title displayed in the center of the app bar
                title = {
                    Text(
                        "Food Intake Questionnaire",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,//maximum num of line show at one time
                        // The Ellipsis property is used to truncate the text if it exceeds the available space.
                        overflow = TextOverflow.Ellipsis
                    )
                },
                // Navigation icon (back button) with appropriate behavior
                navigationIcon = {
                    IconButton(onClick = {
                        mContext.startActivity(Intent(mContext, LoginActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(10.dp),
        ) {
            Text(
                text = "Tick all the food categories you can eat",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), //3 columns
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(-10.dp),
                modifier = Modifier.padding(0.dp)
            ) {
                items(foodCategories) { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 1.dp).clickable {
                            foodIntakesViewModel.updateSelection(category,
                                !foodIntakesViewModel.selectedCategories.contains(category))
                        }
                    ) {
                        Checkbox(
                            checked = foodIntakesViewModel.selectedCategories.contains(category),
                            onCheckedChange = { foodIntakesViewModel.updateSelection(category, it)
                                              },
                            modifier = Modifier.padding(0.dp)
                        )
                        Text(text = category, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            //persona
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Your Persona",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = "People can be broadly classified into 6 different types based on their eating preferences. " +
                        "Click on each button below to find out the different types, and select the type that best fits you!",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp).padding(0.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
            ) {
                items(personas) { persona ->
                    ShowButtonAndModal(persona)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Which persona best fits you?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),

                onExpandedChange = { isExpanded = it }
            ) {
                OutlinedTextField(
                    value = foodIntakesViewModel.bestPersona.value,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select option", modifier = Modifier.padding(0.dp))},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(0.dp),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    shape = RoundedCornerShape(16.dp)
                )

                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    personas.forEach { persona ->
                        DropdownMenuItem(
                            text = { Text(persona.title) },
                            onClick = {
                                foodIntakesViewModel.bestPersona.value = persona.title
                                isExpanded = false
                            }
                        )
                    }
                }

            }
            //timing
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Timings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            TimePicker(
                time = foodIntakesViewModel.mealTime,
                label = "What time of day approx. Do you normally eat your biggest meal?"
            )
            TimePicker(
                time = foodIntakesViewModel.sleepTime,
                label = "What time of day approx. Do you go to sleep at night?"
            )
            TimePicker(
                time = foodIntakesViewModel.wakeTime,
                label = "What time of day approx. Do you wake up in the morning?"
            )
            if(error) {
                if (foodIntakesViewModel.mealTime.value == foodIntakesViewModel.sleepTime.value) {
                    Text(
                        text = "Biggest Meal Time and Sleep Time cannot be same.",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 2.dp, start = 16.dp)
                    )
                }
                else if (foodIntakesViewModel.sleepTime.value == foodIntakesViewModel.wakeTime.value) {
                    Text(
                        text = "Sleep Time and Wake Time cannot be same.",
                        fontSize = 14.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 2.dp, start = 16.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 0.dp)
            ){
                Button(
                    onClick = {
                        if (foodIntakesViewModel.sleepTime.value == foodIntakesViewModel.wakeTime.value || foodIntakesViewModel.mealTime.value == foodIntakesViewModel.sleepTime.value){
                            error = true
                        }else{
                            foodIntakesViewModel.saveQuestionnaire(AuthManager.getPatientId().toString())
                            mContext.startActivity(Intent(mContext, HomeActivity::class.java))
                        }
                              },
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFF590DE3),
                        contentColor = Color.White),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_save_24),
                        contentDescription = null, // decorative icon
                        modifier = Modifier.size(24.dp).padding(horizontal = 4.dp)
                    )
                    Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

        }
    }
}

@Composable
fun TimePicker(time: MutableState<String>, label: String) {
    var showDialog = TimePickerFun(time)
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp)) {
        Column(
            modifier = Modifier.padding(0.dp).height(45.dp).width(240.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(
            modifier = Modifier.padding(4.dp).height(52.dp).width(120.dp)
        ) {
            OutlinedTextField(
                value = time.value,
                onValueChange = {}, //event request value to change
                readOnly = true,
                leadingIcon = {
                    IconButton(onClick = { showDialog.show() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_access_time_24),
                            contentDescription = "Time picker"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
                    .clickable { showDialog.show() },
                shape = RoundedCornerShape(8.dp),
            )
        }
    }
}


@Composable
fun TimePickerFun(time: MutableState<String>): TimePickerDialog {
    // Get the current context
    val mContext = LocalContext.current
    // Get a calendar instance
    val mCalendar = Calendar.getInstance()
    // Get the current hour and minute
    val mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = mCalendar.get(Calendar.MINUTE)
    // Set the calendar's time to the current time
    mCalendar.time = Calendar.getInstance().time
    // Return a TimePickerDialog
    return TimePickerDialog(
        // Context
        // Listener to be invoked when the time is set
        // Initial hour and minute
        // Whether to use 24-hour format
        mContext,
        { _, mHour: Int, mMinute: Int ->
            time.value = String.format("%d:%02d", mHour, mMinute)
        }, mHour, mMinute, false
    )
}


@Composable
fun ShowButtonAndModal(persona: Persona) {
    var showPersonaModal by remember { mutableStateOf(false) }
    var showPersona by remember { mutableStateOf(Persona("", "", 0)) }
    val scrollState = rememberScrollState()
    OutlinedButton(
        colors = ButtonDefaults.buttonColors(
            Color(0xFF590DE3),
            contentColor = Color.White
        ),
        modifier = Modifier
            .height(38.dp)
            .wrapContentWidth()
            .widthIn(min = 100.dp, max = 600.dp)
            .padding(2.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp),
        onClick = {
            showPersonaModal = true
            showPersona = persona
        })
    {
        Text(
            text = persona.title,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(4.dp)
        )
    }

    if (showPersonaModal) {
        AlertDialog(
            // switch the visibility of the dialog to false when the user dismisses it
            onDismissRequest = { showPersonaModal = false },
            title = { },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        painter = painterResource(persona.imageId),
                        contentDescription = persona.title,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                    Text(
                        persona.title,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                    Text(
                        text = persona.description,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 0.dp)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                // if the user clicks on the dismiss button,
                // close the dialog
                Column(
                    modifier = Modifier.fillMaxWidth().padding(2.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { showPersonaModal = false },
                        colors = ButtonDefaults.buttonColors(
                            Color(0xFF590DE3),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.padding(0.dp),

                        ) {
                        Text("Dismiss")
                    }
                }
            }
        )
    }
}
