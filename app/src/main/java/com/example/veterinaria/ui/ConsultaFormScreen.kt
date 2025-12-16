package com.example.veterinaria.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.veterinaria.data.model.Consulta
import com.example.veterinaria.viewmodel.MainViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaFormScreen(viewModel: MainViewModel, navController: NavController, consultaId: Int?, duenoId: String) {
    val isEditing = consultaId != null
    val consulta = if (isEditing) viewModel.consultas.value.find { it.id == consultaId } else null
    val mascotasDelDueno = viewModel.getMascotasByDueno(duenoId)

    var mascotaId by remember { mutableStateOf(consulta?.mascotaId ?: mascotasDelDueno.firstOrNull()?.id ?: 0) }
    var descripcion by remember { mutableStateOf(consulta?.descripcion ?: "") }
    var costoBase by remember { mutableStateOf(consulta?.costoBase?.toString() ?: "") }
    var fecha by remember { mutableStateOf(consulta?.fecha ?: LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedMascota by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Consulta" else "Nueva Consulta") },
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
            // Dropdown para Mascota
            ExposedDropdownMenuBox(
                expanded = expandedMascota,
                onExpandedChange = { expandedMascota = !expandedMascota },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = mascotasDelDueno.find { it.id == mascotaId }?.nombre ?: "",
                    onValueChange = {},
                    label = { Text("Mascota") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMascota) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedMascota,
                    onDismissRequest = { expandedMascota = false }
                ) {
                    mascotasDelDueno.forEach { selectedMascota ->
                        DropdownMenuItem(
                            text = { Text(selectedMascota.nombre) },
                            onClick = {
                                mascotaId = selectedMascota.id
                                expandedMascota = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = costoBase,
                onValueChange = { costoBase = it },
                label = { Text("Costo Base") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = fecha.toString(),
                onValueChange = {},
                label = { Text("Fecha") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fecha.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                fecha = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                            }
                            showDatePicker = false
                        }) {
                            Text("Aceptar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Button(
                onClick = {
                    val consultaToSave = Consulta(
                        id = consultaId ?: 0,
                        mascotaId = mascotaId,
                        duenoId = duenoId,
                        descripcion = descripcion,
                        costoBase = costoBase.toDoubleOrNull() ?: 0.0,
                        fecha = fecha
                    )
                    if (isEditing) {
                        viewModel.updateConsulta(consultaToSave)
                    } else {
                        viewModel.addConsulta(consultaToSave)
                    }
                    navController.navigate("agenda") { popUpTo("home") }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}
