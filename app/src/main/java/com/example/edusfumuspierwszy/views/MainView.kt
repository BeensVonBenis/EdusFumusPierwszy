package com.example.edusfumuspierwszy.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusfumuspierwszy.ClassTile
import com.example.edusfumuspierwszy.LekcjeUtils

@Composable
fun MainView(modifier: Modifier = Modifier) {
    val page = remember { mutableStateOf("schoolPlan") }
    val loading = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        loading.value = true;
        LekcjeUtils.fetchData();
        loading.value = false;
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { page.value = "schoolPlan" },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home, // Replace with an appropriate icon
                    contentDescription = "Plan klasy",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = { page.value = "teacherPlan" },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Face, // Replace with an appropriate icon
                    contentDescription = "Plan klasy",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = { page.value = "options" },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings, // Replace with an appropriate icon
                    contentDescription = "Plan klasy",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh, // Replace with an appropriate icon
                    contentDescription = "Plan klasy",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(color = Color(100, 100, 150, 50))
        )
        Box(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Ja, Edus Fumus Pierwszy witam was w moim interfejsie graficznym!",
                fontSize = 22.sp
            )
        }
        Box(modifier = Modifier.padding(top = 8.dp)) {
            if (!loading.value) {
                when (page.value) {
                    "schoolPlan" -> {
                        SchoolPlan()
                    }

                    "teacherPlan" -> {
                        TeacherPlan()
                    }

                    else -> {
                        Options()
                    }
                }
            } else {
                Text(text = "Wykradanie danych z API...")
            }

        }

    }

//    val context = LocalContext.current
//    val classesList = remember { mutableStateOf(LekcjeUtils.getClassesList()) }
//    val expanded = remember { mutableStateOf(false) }
//    val selectedItem = remember { mutableStateOf(ClassTile(id = "0", name = "Nie pobrano klas")) }
//
//    val teachersList = remember { mutableStateOf(LekcjeUtils.getTeachersList()) }
//    val expanded2 = remember { mutableStateOf(false) }
//    val selectedItem2 = remember { mutableStateOf(ClassTile(id = "0", name = "Nie pobrano klas")) }
//
//    val plan = remember { mutableStateOf(LekcjeUtils.bufferedSchoolPlan) }
//    val loading = remember { mutableStateOf(false) }
//
//    LaunchedEffect(Unit) {
//        loading.value = true;
//        LekcjeUtils.fetchData();
//        plan.value = LekcjeUtils.getSchoolPlan(context, "-114", true)
//        classesList.value = LekcjeUtils.getClassesList();
//        teachersList.value = LekcjeUtils.getTeachersList();
//        loading.value = false;
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Box(modifier = Modifier.padding(8.dp)) {
//            Text(
//                text = "Ja, Edus Fumus Pierwszy witam was w moim interfejsie graficznym!",
//                fontSize = 22.sp
//            )
//        }
//
//        Row {
//            Column {
//                Button(onClick = { expanded.value = !expanded.value }) {
//                    Text(text = "Wybierz klasę")
//                }
//
//                if (expanded.value) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .heightIn(max = 300.dp) // Set a height limit
//                    ) {
//                        items(classesList.value) { item ->
//                            Button(onClick = {
//                                selectedItem.value = item
//                                plan.value = LekcjeUtils.getSchoolPlan(context, item.id, true)
//                                expanded.value = false
//                            }) {
//                                Text(text = item.name)
//                            }
//                        }
//                    }
//                }
//            }
//
//            Column {
//                Button(onClick = { expanded2.value = !expanded2.value }) {
//                    Text(text = "wybierz nauczyciela")
//                }
//                if (expanded2.value) {
//                    LazyColumn(
//                        modifier = Modifier
//                            .heightIn(max = 300.dp) // Set a height limit
//                    ) {
//                        items(teachersList.value) { item ->
//                            Button(onClick = {
//                                selectedItem.value = item
//                                plan.value = LekcjeUtils.getTeacherPlan(item.id)
//                                expanded2.value = false
//                            }) {
//                                Text(text = item.name)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        SchoolPlan(name = "test")
//
//    }

}