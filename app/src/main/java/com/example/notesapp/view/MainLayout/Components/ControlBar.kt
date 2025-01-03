package com.example.notesapp.view.MainLayout.Components


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.rounded.FilterAlt

@Composable
fun ControlBar(
    modifier: Modifier = Modifier,
    onTextChange: (String) -> Unit,
    onFilterOpen: () -> Unit,
    onSideBarOpen: () -> Unit
) {
    var search by remember { mutableStateOf("") }


    Row {
        Button(onClick = { onSideBarOpen() }) {
            Icon(
                Icons.Rounded.Menu,
                contentDescription = "openMenu"
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                onTextChange(it)
            },
            modifier = Modifier
                .weight(1f),
            singleLine = true,
            placeholder = { Text("Serch for Note") }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { onFilterOpen() }) {
            Icon(
                Icons.Rounded.FilterAlt,
                contentDescription = "set Filter"
            )
        }
    }
}