package com.example.clase3

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.clase3.ui.theme.DarkBlue
import com.example.clase3.ui.theme.White
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.navigate("user_list") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "UADE", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Grupo 4 Viernes TM", fontSize = 20.sp, color = White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(navController: NavController, viewModel: UserViewModel = viewModel()) {
    val users by viewModel.allUsers.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_edit_user/-1") },
                containerColor = DarkBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Usuario")
            }
        },
        containerColor = White
    ) { padding ->
        if (users.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay usuarios registrados", color = DarkBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 80.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users, key = { it.id }) { user ->
                    UserItem(
                        user = user,
                        onDetail = { navController.navigate("user_detail/${user.id}") },
                        onEdit = { navController.navigate("add_edit_user/${user.id}") },
                        onDelete = { viewModel.delete(user) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onDetail: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White, contentColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, DarkBlue.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.imageUri,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${user.firstName} ${user.lastName}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Legajo: ${user.fileNumber}", fontSize = 14.sp)
            }
            Row {
                IconButton(onClick = onDetail) { Icon(Icons.Default.Info, null, tint = DarkBlue) }
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = DarkBlue) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditUserScreen(navController: NavController, userId: Int, viewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var fileNumber by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { imageUri = saveImageToInternalStorage(context, it) }
        }
    )

    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.getUserById(userId)?.let {
                firstName = it.firstName
                lastName = it.lastName
                fileNumber = it.fileNumber
                dni = it.dni
                imageUri = it.imageUri
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userId == -1) "Nuevo Usuario" else "Editar Usuario", color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.LightGray)
                    .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(48.dp), tint = DarkBlue)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = fileNumber, onValueChange = { fileNumber = it }, label = { Text("Legajo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val u = User(if (userId == -1) 0 else userId, firstName, lastName, fileNumber, dni, imageUri)
                    if (userId == -1) viewModel.insert(u) else viewModel.update(u)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = White)
            ) { Text("Guardar Usuario") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(navController: NavController, userId: Int, viewModel: UserViewModel = viewModel()) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) { user = viewModel.getUserById(userId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Usuario", color = White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = White
    ) { padding ->
        user?.let { u ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto circular grande
                AsyncImage(
                    model = u.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Card con la información detallada
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, DarkBlue.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        DetailRow(label = "Nombre", value = u.firstName)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)
                        DetailRow(label = "Apellido", value = u.lastName)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)
                        DetailRow(label = "Legajo", value = u.fileNumber)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)
                        DetailRow(label = "DNI", value = u.dni)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 20.sp,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.filesDir, "user_${System.currentTimeMillis()}.jpg")
    inputStream?.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
    return file.absolutePath
}
