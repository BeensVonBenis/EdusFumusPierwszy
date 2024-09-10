package com.example.edusfumuspierwszy


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusfumuspierwszy.ui.theme.EdusFumusPierwszyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EdusFumusPierwszyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("test")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val classesList = remember { mutableStateOf(LekcjeUtils.getClassesList()) }
    val expanded = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf(ClassTile(id = "0", name = "Nie pobrano klas")) }

    val teachersList = remember { mutableStateOf(LekcjeUtils.getTeachersList()) }
    val expanded2 = remember { mutableStateOf(false) }
    val selectedItem2 = remember { mutableStateOf(ClassTile(id = "0", name = "Nie pobrano klas")) }

    val plan = remember { mutableStateOf(LekcjeUtils.bufferedSchoolPlan) }
    val loading = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loading.value = true;
        LekcjeUtils.fetchData();
        plan.value = LekcjeUtils.getSchoolPlan("-114")
        classesList.value = LekcjeUtils.getClassesList();
        teachersList.value = LekcjeUtils.getTeachersList();
        loading.value = false;
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(text = "Ja, Edus Fumus Pierwszy witam was w moim interfejsie graficznym!")
        Row {
            Column {
                Button(onClick = { expanded.value = !expanded.value }) {
                    Text(text = "Wybierz klasę")
                }

                if (expanded.value) {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp) // Set a height limit
                    ) {
                        items(classesList.value) { item ->
                            Button(onClick = {
                                selectedItem.value = item
                                plan.value = LekcjeUtils.getSchoolPlan(item.id)
                                expanded.value = false
                            }) {
                                Text(text = item.name)
                            }
                        }
                    }
                }
            }

            Column {
                Button(onClick = { expanded2.value = !expanded2.value }) {
                    Text(text = "wybierz nauczyciela")
                }
                if (expanded2.value) {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp) // Set a height limit
                    ) {
                        items(teachersList.value) { item ->
                            Button(onClick = {
                                selectedItem.value = item
                                plan.value = LekcjeUtils.getTeacherPlan(item.id)
                                expanded2.value = false
                            }) {
                                Text(text = item.name)
                            }
                        }
                    }
                }
            }
        }

        if (loading.value) {
            Text(text = "pobieranie danych z API")
        } else {
            LazyColumn {
                items(1) {
                    Row(modifier = Modifier.padding(4.dp, 4.dp)) {
                        plan.value?.forEach { element ->
                            Column {
                                Text(text = "d${element[0].day}")
                                element.sortedBy { tile -> tile.period }.forEach { tile ->
                                    Column(
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Row {
                                            Text(
                                                text = "${tile.period.toString()} ",
                                                fontSize = 12.sp
                                            )
                                            Text(text = "${tile.classroom} ", fontSize = 12.sp)
                                            Text(text = "${tile.group} ", fontSize = 12.sp)
                                        }
                                        Row {
                                            Text(text = "${tile.subject} ", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

        }

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EdusFumusPierwszyTheme {
        Greeting("Android")
    }
}