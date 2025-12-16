package com.example.veterinaria.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onNavigateTo: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("Veterinaria App") },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menú")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Inicio") },
                    onClick = { expanded = false; onNavigateTo("home") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Mascotas") },
                    onClick = { expanded = false; onNavigateTo("mascotas") },
                    leadingIcon = { Icon(Icons.Default.Pets, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Dueños") },
                    onClick = { expanded = false; onNavigateTo("duenos") },
                    leadingIcon = { Icon(Icons.Default.People, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Veterinarios") },
                    onClick = { expanded = false; onNavigateTo("veterinarios") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Agendar") },
                    onClick = { expanded = false; onNavigateTo("agenda") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Calendario") },
                    onClick = { expanded = false; onNavigateTo("calendario") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) }
                )
            }
        }
    )
}
