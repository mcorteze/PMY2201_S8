package com.example.veterinaria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.veterinaria.viewmodel.MainViewModel

@Composable
fun AgendaSelectionScreen(viewModel: MainViewModel, navController: NavController) {
    var duenoId by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Agendar Consulta", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
        OutlinedTextField(
            value = duenoId,
            onValueChange = { duenoId = it; showError = false },
            label = { Text("Ingrese el RUT del Dueño") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError
        )
        if (showError) {
            Text("El RUT ingresado no existe.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = {
                val duenoExists = viewModel.duenos.value.any { it.id == duenoId }
                if (duenoExists) {
                    navController.navigate("consultaForm/0?duenoId=$duenoId")
                } else {
                    showError = true
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Continuar")
        }
    }
}
