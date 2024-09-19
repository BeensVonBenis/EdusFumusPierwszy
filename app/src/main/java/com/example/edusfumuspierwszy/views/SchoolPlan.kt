package com.example.edusfumuspierwszy.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.edusfumuspierwszy.LekcjeUtils
import com.example.edusfumuspierwszy.composables.SchoolPlanDisplay

@Composable
fun SchoolPlan(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }
    val classesList = remember { mutableStateOf(LekcjeUtils.getClassesList()) }
    val selectedClass = remember { mutableStateOf("4PI Tech-p") }
    val plan = remember { mutableStateOf(LekcjeUtils.bufferedSchoolPlan) }
    LaunchedEffect(Unit) {
        plan.value = LekcjeUtils.getSchoolPlan(context, "-114", true)
        classesList.value = LekcjeUtils.getClassesList();
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Button(onClick = { expanded.value = !expanded.value }) {
                Row {
                    Text(text = "Wybierz klasę")
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "More"
                    )
                }
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                classesList.value.forEach { el ->
                    DropdownMenuItem(text = { Text(text = el.name) }, onClick = {
                        expanded.value = false;
                        selectedClass.value = el.name
                        plan.value = LekcjeUtils.getSchoolPlan(context, el.id, true)
                    })
                }
            }
        }
        Box(modifier = Modifier.padding(8.dp)) {
            Text(text = "Plan lekcji dla klasy ${selectedClass.value}")
        }
        if (!plan.value.isNullOrEmpty()) {
            SchoolPlanDisplay(plan = plan.value)
        }

    }
}