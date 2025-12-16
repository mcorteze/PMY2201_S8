package com.example.veterinaria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.veterinaria.data.model.Dueño
import com.example.veterinaria.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuenoFormScreen(viewModel: MainViewModel, navController: NavController, duenoId: String?) {
    val isEditing = duenoId != null
    val dueno = if (isEditing) viewModel.duenos.value.find { it.id == duenoId } else null
    val mascotasDelDueno = if (isEditing) viewModel.getMascotasByDueno(duenoId!!) else emptyList()

    var id by remember { mutableStateOf(dueno?.id ?: "") }
    var nombre by remember { mutableStateOf(dueno?.nombre ?: "") }
    var telefono by remember { mutableStateOf(dueno?.telefono ?: "") }
    var email by remember { mutableStateOf(dueno?.email ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Dueño" else "Nuevo Dueño") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Column {
                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it },
                        label = { Text("ID (RUT)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isEditing
                    )
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                    Button(
                        onClick = {
                            val duenoToSave = Dueño(id, nombre, telefono, email)
                            if (isEditing) {
                                viewModel.updateDueno(duenoToSave)
                            } else {
                                viewModel.addDueno(duenoToSave)
                            }
                            if (!isEditing) {
                                // Solo vuelve atrás si es un nuevo dueño,
                                // si está editando, permanece para que pueda agregar mascotas.
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("Guardar Dueño")
                    }
                }
            }

            if (isEditing) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mascotas", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                        IconButton(onClick = { navController.navigate("mascotaForm/0?duenoId=$duenoId") }) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar Mascota")
                        }
                    }
                }
                items(mascotasDelDueno) { mascota ->
                    PetCard(
                        mascota = mascota,
                        onEdit = { navController.navigate("mascotaForm/${it.id}") },
                        onDelete = { viewModel.deleteMascota(it) }
                    )
                }
            }
        }
    }
}
