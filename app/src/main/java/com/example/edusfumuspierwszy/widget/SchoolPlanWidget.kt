package com.example.edusfumuspierwszy.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.edusfumuspierwszy.LekcjeUtils
import com.example.edusfumuspierwszy.PeriodStartEnd
import com.example.edusfumuspierwszy.SchoolPlanTile
import com.example.edusfumuspierwszy.minutesFromMidnight
import com.example.edusfumuspierwszy.timeStringToMinutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class SchoolPlanWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                SchoolPlanDisplay(context)
            }
        }
    }

    @Composable
    fun SchoolPlanDisplay(context: Context) {
        val day = remember { mutableIntStateOf(LocalDate.now().dayOfWeek.value - 1) };
        val currentDateTime = remember { mutableStateOf(LocalDateTime.now()) }
        val lessons = remember {
            mutableStateOf(LekcjeUtils.getSchoolPlan(context, "-114"))
        }
        val periodStartEndList = remember { mutableStateOf(LekcjeUtils.getStartEndTimes()) };
        val loading = remember { mutableStateOf(true) }
        fun updateTime() {
            currentDateTime.value = LocalDateTime.now()
            Log.d("Pobieranie", "datetimusa $currentDateTime")
            day.intValue = LocalDate.now().dayOfWeek.value - 1
        }
        LaunchedEffect(Unit) {
            loading.value = true
            lessons.value = LekcjeUtils.getSchoolPlan(context, "-114")
            periodStartEndList.value = LekcjeUtils.getStartEndTimes()
            updateTime()
            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    currentDateTime.value = LocalDateTime.now()
                    kotlinx.coroutines.delay(10000) // Update every second
                }
            }
            loading.value = false
        }
        val updateTimeHandler = {
            CoroutineScope(Dispatchers.Main).launch {
                updateTime()
            }
        }
        if (loading.value && lessons.value!!.isEmpty()) {
            Text("Pobieranie danych")
        } else {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(8.dp).background(Color(36, 36, 36))
            ) {
                Button("Aktualizuj", { updateTimeHandler() })
                lessons.value?.get(day.value)
                    ?.sortedBy { tile -> tile.period }
                    ?.chunked(5) // Split into chunks of 5
                    ?.forEach { chunk ->
                        Column {
                            chunk.forEach { element ->
                                SchoolPlanWidgetTile(
                                    tile = element,
                                    periodData = periodStartEndList.value[element.period],
                                    dateTime = currentDateTime.value
                                )
                            }
                        }
                    }
            }
        }
    }

    @Composable
    fun SchoolPlanWidgetTile(
        tile: SchoolPlanTile,
        periodData: PeriodStartEnd,
        dateTime: LocalDateTime
    ) {
        Column(modifier = GlanceModifier.fillMaxWidth()) {
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Text(
                    text = "${tile.period} ${tile.subject}  ${tile.classroom} ",
                    style = TextStyle(color = ColorProvider(Color(255, 255, 255, 255)))
                )
                Text(
                    text = "LKW ${
                        timeStringToMinutes(periodData.start) - minutesFromMidnight(
                            dateTime
                        )
                    } ${
                        timeStringToMinutes(periodData.end) - minutesFromMidnight(
                            dateTime
                        )
                    }", style = TextStyle(color = ColorProvider(Color(255, 255, 255, 255)))
                )
            }
            Text(
                modifier = GlanceModifier.background(Color(100, 100, 150, 255)).fillMaxWidth()
                    .height(1.dp),
                text = "to jest legitny kurwa border"
            )
        }

    }
    //minutesFromMidnight(timeStringToMinutes(periodData.start))
}
