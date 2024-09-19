package com.example.edusfumuspierwszy.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusfumuspierwszy.SchoolPlanTile
import java.time.LocalDateTime
import kotlin.math.abs

fun intToWeekName(day: Int): String {
    when (day) {
        0 -> {
            return "poniedziałek"
        }

        1 -> {
            return "wtorek"
        }

        2 -> {
            return "środa"
        }

        3 -> {
            return "czwartek"
        }

        4 -> {
            return "piątek"
        }

        else -> {
            return "Że co?"
        }
    }
}

@Composable
fun SchoolPlanDisplay(plan: List<List<SchoolPlanTile>>?) {
    val selectedDay = remember { mutableIntStateOf((LocalDateTime.now().dayOfMonth - 1 + 5) % 5) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = {
                selectedDay.intValue = abs((selectedDay.intValue - 1 + 5)) % 5
            }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous day")
            }
            Text(
                text = intToWeekName(selectedDay.intValue),
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
            IconButton(onClick = { selectedDay.intValue = (selectedDay.intValue + 1) % 5 }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next day")
            }
        }
        plan?.let { safePlan ->
            if (safePlan.isNotEmpty()) {
                if (safePlan.size != 5) {
                    val selectedPlan = safePlan.find { it[0].day == selectedDay.intValue }
                    if (!selectedPlan.isNullOrEmpty()) {
                        SchoolPlanPage(thisPlan = selectedPlan)
                    } else {
                        Text(text = "Brak lekcji w ten dzień")
                    }
                } else {
                    SchoolPlanPage(
                        thisPlan = safePlan.getOrNull(selectedDay.intValue) ?: emptyList()
                    )
                }
            }
        } ?: Text(text = "Plan is null")

    }
}

@Composable
fun SchoolPlanPage(thisPlan: List<SchoolPlanTile>) {
    LazyColumn {
        items(1) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    // Group the lessons by period and iterate through them
                    thisPlan.sortedBy { it.period }.groupBy { it.period }.forEach { periodLessons ->
                        // Create a Row for each period, so lessons in the same period are next to each other
                        Row(modifier = Modifier.fillMaxWidth()) {
                            periodLessons.value.forEach { lesson ->
                                // Each lesson is rendered inside a Column with a weight modifier for even spacing
                                SchoolPlanRow(thisLesson = lesson, Modifier.weight(1f))
                            }
                        }
                        Box(
                            modifier = Modifier
                                .height(1.dp)
                                .background(color = Color(100, 100, 100))
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SchoolPlanRow(thisLesson: SchoolPlanTile, modifier: Modifier = Modifier) {
    // Pass the modifier down so that the lessons can be laid out evenly within the Row
    Column(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row {
            Text(text = "${thisLesson.period}. ")
            Text(text = "${thisLesson.classroom} ")
        }
        Row {
            Text(text = "${thisLesson.subject} ")
            Text(text = "${thisLesson.group} ")
        }
    }
}