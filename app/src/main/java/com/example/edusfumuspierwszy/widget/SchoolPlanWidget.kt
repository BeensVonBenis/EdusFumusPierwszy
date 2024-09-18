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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
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
            mutableStateOf(LekcjeUtils.getSchoolPlan(context, "-114", false))
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
            lessons.value = LekcjeUtils.getSchoolPlan(context, "-114", false)
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
                modifier = GlanceModifier.fillMaxSize().padding(top = 16.dp)
                    .background(Color(36, 36, 36)).width(360.dp)
            ) {
                lessons.value?.get(day.intValue)
                    ?.sortedBy { tile -> tile.period }
                    ?.chunked(5) // Split into chunks of 5
                    ?.forEach { chunk ->
                        Column(modifier = GlanceModifier.padding(start = 8.dp, end = 8.dp)) {
                            Text(
                                modifier = GlanceModifier.background(Color(100, 100, 150, 255))
                                    .fillMaxWidth()
                                    .height(1.dp),
                                text = "to jest legitny kurwa border"
                            )
                            chunk.forEach { element ->
                                SchoolPlanWidgetTile(
                                    tile = element,
                                    periodData = periodStartEndList.value[element.period - 1],
                                    dateTime = currentDateTime.value
                                )
                            }
                        }
                    }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Button(
                        "⟳",
                        { updateTimeHandler() },
                        GlanceModifier.size(32.dp).padding(bottom = 4.dp),
                        style = TextStyle(color = ColorProvider(Color.White))
                    )
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
        var timeFromStart = timeStringToMinutes(periodData.start) - minutesFromMidnight(
            dateTime
        )
        val timeFromEnd = timeStringToMinutes(periodData.end) - minutesFromMidnight(
            dateTime
        )

        fun getProgress(): Number {
            val duration = timeFromEnd - timeFromStart;
            return if (timeFromStart < 0) {
                if (timeFromEnd < 0) {
                    360
                } else {
                    360 - (360 * timeFromEnd / duration)
                }
            } else {
                0;
            }

        }
        Column(modifier = GlanceModifier.fillMaxWidth()) {
            Box {
                Row {
                    Text(
                        modifier = GlanceModifier.background(Color(100, 100, 150, 50))
                            .width(getProgress().toInt().dp)
                            .height(22.dp),
                        text = ""
                    )
                    if (getProgress().toInt() in 1..359) {
                        Text(
                            modifier = GlanceModifier.background(Color(100, 100, 150, 80))
                                .width(2.dp)
                                .height(22.dp),
                            text = ""
                        )
                    }

                }

                Row(modifier = GlanceModifier.fillMaxWidth().padding(1.dp)) {
                    Text(
                        text = "${tile.period} ${tile.subject}  ${tile.classroom} ",
                        style = TextStyle(color = ColorProvider(Color(255, 255, 255, 255)))
                    )
                    Text(
                        text = "LKW $timeFromStart $timeFromEnd",
                        style = TextStyle(color = ColorProvider(Color(255, 255, 255, 255)))
                    )
                }
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
