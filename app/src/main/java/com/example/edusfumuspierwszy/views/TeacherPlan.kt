package com.example.edusfumuspierwszy.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusfumuspierwszy.ClassTile
import com.example.edusfumuspierwszy.LekcjeUtils
import com.example.edusfumuspierwszy.composables.SchoolPlanDisplay

@Composable
fun TeacherPlan(modifier: Modifier = Modifier) {
    val expanded = remember { mutableStateOf(false) }
    val teachersList = remember { mutableStateOf(LekcjeUtils.getTeachersList()) }
    val selectedTeacher = remember { mutableStateOf("Sylwia Reng") }
    val plan = remember { mutableStateOf(LekcjeUtils.bufferedSchoolPlan) }
    LaunchedEffect(Unit) {
        plan.value = LekcjeUtils.getTeacherPlan("-114")
        teachersList.value = LekcjeUtils.getTeachersList();
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Button(onClick = { expanded.value = !expanded.value }) {
                Row {
                    Text(text = "Wybierz nauczyciela")
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
                teachersList.value.forEach { el ->
                    DropdownMenuItem(text = { Text(text = el.name) }, onClick = {
                        expanded.value = false;
                        selectedTeacher.value = el.name
                        plan.value = LekcjeUtils.getTeacherPlan(el.id)
                    })
                }
            }
        }
        Box(modifier = Modifier.padding(8.dp)) {
            Text(text = "Plan lekcji dla ${selectedTeacher.value}")
        }
        if (!plan.value.isNullOrEmpty()) {
            SchoolPlanDisplay(plan = plan.value)
        }
    }
}
