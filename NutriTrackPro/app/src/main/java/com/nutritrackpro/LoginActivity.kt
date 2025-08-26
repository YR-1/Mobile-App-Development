package com.nutritrackpro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutritrackpro.data.AuthManager
import com.nutritrackpro.data.authentication.LoginViewModel
import com.nutritrackpro.data.authentication.RegisterViewModel
import com.nutritrackpro.data.foodIntake.FoodIntakesViewModel
import com.nutritrackpro.ui.theme.NutriTrackProTheme
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val intakeViewModel: FoodIntakesViewModel = ViewModelProvider(
                this, FoodIntakesViewModel.FoodIntakesViewModelFactory(this@LoginActivity)
            )[FoodIntakesViewModel::class.java]

            val loginViewModel: LoginViewModel = ViewModelProvider(
                this, LoginViewModel.LoginViewModelFactory(this@LoginActivity)
            )[LoginViewModel::class.java]

            val registerViewModel: RegisterViewModel = ViewModelProvider(
                this, RegisterViewModel.RegisterViewModelFactory(this@LoginActivity)
            )[RegisterViewModel::class.java]

            NutriTrackProTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AuthNavHost(innerPadding, intakeViewModel, loginViewModel, registerViewModel)
                }
            }
        }
    }
}


@Composable
fun AuthNavHost(innerPadding: PaddingValues, intakeViewModel: FoodIntakesViewModel, loginViewModel: LoginViewModel, registerViewModel: RegisterViewModel) {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login")
        {
            LoginScreen(innerPadding, navController, intakeViewModel, loginViewModel)
        }
        composable("register")
        {
            RegisterScreen(innerPadding, navController, registerViewModel)
        }
        composable("forgotPassword")
        {
            ForgotPasswordScreen(innerPadding, navController, loginViewModel)
        }
        composable("resetPassword")
        {
            ResetPasswordScreen(innerPadding, navController, loginViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserIdDropdown(userIds: List<String>, error: Boolean, selectedId: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        OutlinedTextField(
            value = userId,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = "My ID (Provided by your Clinician)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold) },
            placeholder = { Text("Select your ID") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            isError = error,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            userIds.forEach { currentId ->
                DropdownMenuItem(
                    text = { Text(currentId) },
                    onClick = {
                        userId = currentId
                        selectedId(currentId)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(innerPadding: PaddingValues, navController: NavHostController, intakeViewModel: FoodIntakesViewModel, viewModel: LoginViewModel) {
    LaunchedEffect(Unit) {
        intakeViewModel.getIntake(AuthManager.getPatientId().toString())
    }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val ids by viewModel.registeredIds.observeAsState(emptyList())
    var selectedUserId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val authError by viewModel.authError.observeAsState()
    val intake by intakeViewModel.intakeLive.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    viewModel.loadUserIds()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Log in",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))

        UserIdDropdown(ids, error) { userId ->
            selectedUserId = userId
            error = false //reset error when selectedUserId changes
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (password.isEmpty() || !password.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "Password",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //capable input phone num
            trailingIcon = {
                //toggle button to hide or show password
                IconButton(onClick = {showPassword = !showPassword}){
                    Icon(
                        if (showPassword) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )

        if (error) {
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "This app is only for pre-registered users. Please enter your ID and password or Register to claim your account on your first visit.",
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.clearAuthError()
                if (selectedUserId.isEmpty() || password.isEmpty()) {
                    errorMsg = "Please fill in both Id and Password"
                    error = true
                }
                else {
                    coroutineScope.launch {
                        val success = viewModel.attemptLogin(selectedUserId, password)
                        if (!success) {
                            errorMsg = authError as String
                            error = true
                        } else {
                            if (intake != null){
                                context.startActivity(Intent(context, HomeActivity::class.java))
                            }else {
                                context.startActivity(Intent(context, QuestionnaireActivity::class.java))
                            }

                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("register")
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Divider(Modifier.padding(vertical = 20.dp))
        Button(
            onClick = {
                navController.navigate("forgotPassword")
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Forgot Password",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RegisterScreen(innerPadding: PaddingValues, navController: NavHostController, viewModel: RegisterViewModel){
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val ids by viewModel.unclaimedUserIds.observeAsState(emptyList())
    var selectedUserId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var showCfmPwd by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val authError by viewModel.authError.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    viewModel.loadUserIds()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Register",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))

        UserIdDropdown(ids, error) { userId ->
            selectedUserId = userId
            error = false //reset error when selectedUserId changes
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                if (phoneNumber.isEmpty() || !phoneNumber.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "Phone Number",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your phone number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), //capable input phone num
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                if (name.isEmpty() || !name.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "Name",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), //capable input phone num
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (password.isEmpty() || !password.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "Password",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //capable input phone num
            trailingIcon = {
                //toggle button to hide or show password
                IconButton(onClick = {showPassword = !showPassword}){
                    Icon(
                        if (showPassword) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (confirmPassword.isEmpty() || !confirmPassword.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "Confirm Password",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your password again") },
            visualTransformation = if (showCfmPwd) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //capable input phone num
            trailingIcon = {
                //toggle button to hide or show password
                IconButton(onClick = {showCfmPwd = !showCfmPwd}){
                    Icon(
                        if (showCfmPwd) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )

        authError?.let{
            error = true
            errorMsg = it
        }

        if (error) {
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "This app is only for pre-registered users. Please enter your ID and password or Register to claim your account on your first visit.",
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.clearAuthError()
                if (selectedUserId.isEmpty() || phoneNumber.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    errorMsg = "Please fill in all the field."
                    error = true
                }else if (password != confirmPassword) {
                    errorMsg = "Password and confirm password does not match."
                    error = true
                }
                else {
                    coroutineScope.launch {
                        val success = viewModel.attemptRegister(selectedUserId, phoneNumber, name, password)
                        if (success){
                            Toast.makeText(context, "Account claimed successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("login")
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Login",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(innerPadding: PaddingValues, navController: NavHostController, viewModel: LoginViewModel){
    val scrollState = rememberScrollState()
    val ids by viewModel.registeredIds.observeAsState(emptyList())
    var selectedUserId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val verifyError by viewModel.verifyError.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    viewModel.loadUserIds()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Forgot Password",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))

        UserIdDropdown(ids, error) { userId ->
            selectedUserId = userId
            error = false //reset error when selectedUserId changes
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                if (phoneNumber.isEmpty() || !phoneNumber.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "Phone Number",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your phone number to verify") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), //capable input phone num
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )

        if (error) {
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.clearVerifyError()
                if (selectedUserId.isEmpty() || phoneNumber.isEmpty()) {
                    errorMsg = "Please fill in both Id and Phone Number"
                    error = true
                }
                else {
                    coroutineScope.launch {
                        val success = viewModel.attemptVerify(selectedUserId, phoneNumber)
                        if (!success) {
                            errorMsg = verifyError as String
                            error = true
                        } else {
                            navController.navigate("resetPassword")
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Verify",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("login")
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Cancel",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ResetPasswordScreen(innerPadding: PaddingValues, navController: NavHostController, viewModel: LoginViewModel){
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val ids by viewModel.registeredIds.observeAsState(emptyList())
    var selectedUserId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var showCfmPwd by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val resetPwdError by viewModel.resetPwdError.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    viewModel.loadUserIds()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(innerPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Reset Password",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))
        UserIdDropdown(ids, error) { userId ->
            selectedUserId = userId
            error = false //reset error when selectedUserId changes
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (password.isEmpty() || !password.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "New Password",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your new password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //capable input phone num
            trailingIcon = {
                //toggle button to hide or show password
                IconButton(onClick = {showPassword = !showPassword}){
                    Icon(
                        if (showPassword) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (confirmPassword.isEmpty() || !confirmPassword.isEmpty()) {
                    error = false
                }
            },
            label = {
                Text(
                    text = "Confirm New Password",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            singleLine = true,
            placeholder = { Text("Enter your new password again") },
            visualTransformation = if (showCfmPwd) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //capable input phone num
            trailingIcon = {
                //toggle button to hide or show password
                IconButton(onClick = {showCfmPwd = !showCfmPwd}){
                    Icon(
                        if (showCfmPwd) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            isError = error,
            modifier = Modifier.fillMaxWidth()
        )

        resetPwdError?.let{
            error = true
            errorMsg = it
        }

        if (error) {
            Text(
                text = errorMsg,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.clearResetError()
                if (selectedUserId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    errorMsg = "Please fill in all the field."
                    error = true
                }else if (password != confirmPassword) {
                    errorMsg = "Password and confirm password does not match."
                    error = true
                }
                else {
                    coroutineScope.launch {
                        val success = viewModel.attemptReset(selectedUserId, password)
                        if (success){
                            Toast.makeText(context, "Password reset successfully!", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Reset Password",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate("login")
            },
            colors = ButtonDefaults.buttonColors(
                Color(0xFF590DE3),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(60.dp)

        ) {
            Text(
                text = "Cancel",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}