package com.example.edusfumuspierwszy.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.text.Text
import com.example.edusfumuspierwszy.LekcjeUtils
import java.time.LocalDate

class SchoolPlanWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Scaffold {
                SchoolPlanDisplay()
            }
        }
    }

    @Composable
    fun SchoolPlanDisplay() {
        val day = LocalDate.now().dayOfWeek.value - 1;
        val lessons = remember {
            mutableStateOf(LekcjeUtils.getSchoolPlan("-114"))
        }
        val loading = remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            loading.value = true;
            LekcjeUtils.fetchData();
            lessons.value = LekcjeUtils.getSchoolPlan("-114");
            loading.value = false;
        }
        if (loading.value) {
            Text("Pobieranie danych")
        } else {

            Column {
                Text(text = day.toString())
                lessons.value?.get(day.toInt())?.sortedBy { tile -> tile.period }
                    ?.forEach { element ->

                        Text(text = "${element.period.toString()} ${element.subject}  ${element.classroom}")

                    }
            }
        }
    }
}


