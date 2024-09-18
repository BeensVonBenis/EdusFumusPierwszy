package com.example.edusfumuspierwszy.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
            IconButton(onClick = { selectedDay.value = abs((selectedDay.intValue - 1 + 5)) % 5 }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous day")
            }
            Text(
                text = "Wybrany dzień to ${intToWeekName(selectedDay.value)}",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
            IconButton(onClick = { selectedDay.value = (selectedDay.intValue + 1) % 5 }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next day")
            }
        }
        Text(text = selectedDay.value.toString())
//        if (plan!!.isNotEmpty()) {
//            plan.get(selectedDay.value).forEach { el ->
//                Text(text = el.subject)
//            }
//        }
    }
}