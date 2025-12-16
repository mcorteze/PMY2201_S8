package com.example.veterinaria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.veterinaria.viewmodel.MainViewModel
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarioScreen(viewModel: MainViewModel) {
    val consultas by viewModel.consultas.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
            }
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente")
            }
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            val consultasDelMes = consultas.filter { it.fecha.year == currentMonth.year && it.fecha.month == currentMonth.month }
            val groupedConsultas = consultasDelMes.groupBy { it.fecha }

            if (groupedConsultas.isNotEmpty()) {
                groupedConsultas.entries.sortedBy { it.key }.forEach { (fecha, consultasDelDia) ->
                    item {
                        Text(
                            text = "${fecha.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${fecha.dayOfMonth} de ${fecha.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Divider()
                    }
                    items(consultasDelDia) { consulta ->
                        ConsultaCard(consulta = consulta, viewModel = viewModel)
                    }
                }
            } else {
                item {
                    Text("No hay atenciones para este mes.")
                }
            }
        }
    }
}
