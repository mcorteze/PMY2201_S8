package com.example.veterinaria.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.veterinaria.data.model.Consulta
import com.example.veterinaria.viewmodel.MainViewModel

@Composable
fun ConsultaCard(consulta: Consulta, viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val dueno = viewModel.getDuenoById(consulta.duenoId)
    val mascota = viewModel.getMascotaById(consulta.mascotaId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.MedicalServices,
                    contentDescription = "Consulta Icon",
                    modifier = Modifier.size(40.dp).padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(consulta.descripcion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Costo: $${consulta.costoBase}", style = MaterialTheme.typography.bodySmall)
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("Dueño: ${dueno?.nombre ?: "No encontrado"}")
                    Text("Mascota: ${mascota?.nombre ?: "No encontrada"}")
                }
            }
        }
    }
}
