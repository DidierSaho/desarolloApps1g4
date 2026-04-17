package com.example.clase3

import android.app.Activity // Importante para poder cerrar la app
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
import androidx.compose.foundation.shape.RoundedCornerShape
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

// 1. PANTALLA DE BIENVENIDA (SPLASH)
@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000) 
        navController.navigate("user_list") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DarkBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "UADE", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Grupo 4 Viernes TM", fontSize = 20.sp, color = White)
        }
    }
}

// 2. PANTALLA DE LISTA DE USUARIOS
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(navController: NavController, viewModel: UserViewModel = viewModel()) {
    val listaUsuarios by viewModel.allUsers.collectAsState(initial = emptyList())
    val context = LocalContext.current // Necesitamos el context para cerrar la app
    
    // Estado para controlar si el menú de opciones está abierto o cerrado
    var mostrarMenu by remember { mutableStateOf(false) }
    var mostrarModalIntegrantes by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", color = White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue),
                // "actions" son los botones que aparecen a la derecha en la barra superior
                actions = {
                    // Botón de los tres puntitos
                    IconButton(onClick = { mostrarMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = White)
                    }
                    
                    // Menú que se despliega
                    DropdownMenu(
                        expanded = mostrarMenu,
                        onDismissRequest = { mostrarMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Salir de la App") },
                            onClick = { 
                                mostrarMenu = false
                                // Cerramos la actividad actual para salir de la app
                                (context as? Activity)?.finish()
                            },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Integrantes del equipo") },
                            onClick = { mostrarMenu = false
                                      mostrarModalIntegrantes= true},
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_edit_user/-1") },
                containerColor = DarkBlue,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        if (listaUsuarios.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay usuarios registrados", color = DarkBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaUsuarios) { usuario ->
                    UserItem(
                        user = usuario,
                        onDetail = { navController.navigate("user_detail/${usuario.id}") },
                        onEdit = { navController.navigate("add_edit_user/${usuario.id}") },
                        onDelete = { viewModel.delete(usuario) }
                    )
                }
            }
        }


    }
    if (mostrarModalIntegrantes) {
        val integrantes = listOf("Garcia Lautaro", "Romero Matias", "Sahonero Didier", "Valentini Tiago")

        AlertDialog(
            onDismissRequest = { mostrarModalIntegrantes = false }, // Se cierra si tocan afuera
            title = { Text("Quienes somos") },
            text = {
                // Usamos una Column para poner un nombre debajo del otro
                Column {
                    for (nombre in integrantes) {
                        Text(text = nombre, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarModalIntegrantes = false }) {
                    Text("Cerrar", color = DarkBlue)
                }
            }
        )
    }
}



@Composable
fun UserItem(user: User, onDetail: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.imageUri,
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.firstName + " " + user.lastName, fontWeight = FontWeight.Bold)
                Text(text = "DNI: " + user.dni, fontSize = 12.sp)
            }
            IconButton(onClick = onDetail) { Icon(Icons.Default.Info, null, tint = DarkBlue) }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = DarkBlue) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
        }
    }
}

// 3. PANTALLA PARA AGREGAR O EDITAR
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditUserScreen(navController: NavController, userId: Int, viewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current
    
    // Variables para guardar lo que el usuario escribe
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var legajo by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<String?>(null) }

    // --- NUEVAS VARIABLES PARA EL MODAL DE ERROR ---
    var mostrarModalError by remember { mutableStateOf(false) }
    var mensajeDeError by remember { mutableStateOf("") }

    val selectorFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            fotoUri = guardarFotoEnApp(context, uri)
        }
    }

    LaunchedEffect(userId) {
        if (userId != -1) {
            val usuarioExistente = viewModel.getUserById(userId)
            if (usuarioExistente != null) {
                nombre = usuarioExistente.firstName
                apellido = usuarioExistente.lastName
                legajo = usuarioExistente.fileNumber
                dni = usuarioExistente.dni
                fotoUri = usuarioExistente.imageUri
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
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Click en el círculo para cambiar foto
            Box(
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray)
                    .clickable { selectorFotos.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (fotoUri != null) {
                    AsyncImage(model = fotoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.CameraAlt, null, tint = DarkBlue)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = legajo, onValueChange = { legajo = it }, label = { Text("Legajo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
            
            Spacer(modifier = Modifier.height(20.dp))

            // Botón de Guardar con validaciones
            Button(
                onClick = {
                    // --- LÓGICA DE VALIDACIÓN ---
                    if (nombre.isBlank() && legajo.isBlank()) {
                        mensajeDeError = "Faltan completar el nombre y el legajo."
                        mostrarModalError = true
                    } else if (nombre.isBlank()) {
                        mensajeDeError = "Te falta completar el nombre."
                        mostrarModalError = true
                    } else if (legajo.isBlank()) {
                        mensajeDeError = "Te falta completar el legajo."
                        mostrarModalError = true
                    } else {
                        // Si todo está bien, guardamos
                        val nuevoUsuario = User(
                            id = if (userId == -1) 0 else userId,
                            firstName = nombre,
                            lastName = apellido,
                            fileNumber = legajo,
                            dni = dni,
                            imageUri = fotoUri
                        )
                        
                        if (userId == -1) {
                            viewModel.insert(nuevoUsuario)
                        } else {
                            viewModel.update(nuevoUsuario)
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text("GUARDAR DATOS", color = White)
            }
        }
    }

    // --- EL MODAL (DIÁLOGO) DE ERROR ---
    if (mostrarModalError) {
        AlertDialog(
            onDismissRequest = { mostrarModalError = false },
            title = { Text("Datos incompletos") },
            text = { Text(mensajeDeError) },
            confirmButton = {
                Button(
                    onClick = { mostrarModalError = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}

// 4. PANTALLA DE DETALLE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(navController: NavController, userId: Int, viewModel: UserViewModel = viewModel()) {
    var usuario by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        usuario = viewModel.getUserById(userId)
    }

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
        }
    ) { padding ->
        val u = usuario
        if (u != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = u.imageUri,
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                RenglonDetalle("Nombre completo:", u.firstName + " " + u.lastName)
                RenglonDetalle("Legajo personal:", u.fileNumber)
                RenglonDetalle("DNI:", u.dni)
            }
        }
    }
}

@Composable
fun RenglonDetalle(titulo: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = titulo, fontSize = 12.sp, color = Color.Gray)
        Text(text = valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = Color.LightGray)
    }
}

fun guardarFotoEnApp(context: Context, uriFoto: Uri): String {
    val streamEntrada = context.contentResolver.openInputStream(uriFoto)
    val nombreArchivo = "foto_" + System.currentTimeMillis() + ".jpg"
    val archivoDestino = File(context.filesDir, nombreArchivo)
    val streamSalida = FileOutputStream(archivoDestino)
    
    streamEntrada?.use { entrada ->
        streamSalida.use { salida ->
            entrada.copyTo(salida)
        }
    }
    return archivoDestino.absolutePath
}
