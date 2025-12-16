package com.example.veterinaria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.veterinaria.data.model.Mascota
import com.example.veterinaria.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MascotaFormScreen(viewModel: MainViewModel, navController: NavController, mascotaId: Int?, initialDuenoId: String?) {
    val isEditing = mascotaId != null
    val mascota = if (isEditing) viewModel.mascotas.value.find { it.id == mascotaId } else null
    val duenos by viewModel.duenos.collectAsState()

    var nombre by remember { mutableStateOf(mascota?.nombre ?: "") }
    var especie by remember { mutableStateOf(mascota?.especie ?: viewModel.especies.first()) }
    var edad by remember { mutableStateOf(mascota?.edad?.toString() ?: "") }
    var peso by remember { mutableStateOf(mascota?.peso?.toString() ?: "") }
    var duenoId by remember { mutableStateOf(initialDuenoId ?: mascota?.duenoId ?: duenos.firstOrNull()?.id ?: "") }
    var expandedEspecie by remember { mutableStateOf(false) }
    var expandedDueno by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Mascota" else "Nueva Mascota") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown para Especie
            ExposedDropdownMenuBox(
                expanded = expandedEspecie,
                onExpandedChange = { expandedEspecie = !expandedEspecie },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = especie,
                    onValueChange = {},
                    label = { Text("Especie") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspecie) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedEspecie,
                    onDismissRequest = { expandedEspecie = false }
                ) {
                    viewModel.especies.forEach { selectedEspecie ->
                        DropdownMenuItem(
                            text = { Text(selectedEspecie) },
                            onClick = {
                                especie = selectedEspecie
                                expandedEspecie = false
                            }
                        )
                    }
                }
            }

            // Dropdown para Dueño
            ExposedDropdownMenuBox(
                expanded = expandedDueno,
                onExpandedChange = { if (!isEditing) expandedDueno = !expandedDueno }, // Desactivado si está editando
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = duenos.find { it.id == duenoId }?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Dueño") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDueno) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    enabled = !isEditing // El campo de texto también se desactiva
                )
                ExposedDropdownMenu(
                    expanded = expandedDueno,
                    onDismissRequest = { expandedDueno = false }
                ) {
                    duenos.forEach { selectedDueno ->
                        DropdownMenuItem(
                            text = { Text(selectedDueno.nombre) },
                            onClick = {
                                duenoId = selectedDueno.id
                                expandedDueno = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Guardar")
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar Cambios") },
            text = { Text("¿Estás seguro de que quieres guardar los cambios?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val mascotaToSave = Mascota(
                            id = mascotaId ?: 0,
                            duenoId = duenoId,
                            nombre = nombre,
                            especie = especie,
                            edad = edad.toIntOrNull() ?: 0,
                            peso = peso.toDoubleOrNull() ?: 0.0
                        )
                        if (isEditing) {
                            viewModel.updateMascota(mascotaToSave)
                        } else {
                            viewModel.addMascota(mascotaToSave)
                        }
                        showConfirmationDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
