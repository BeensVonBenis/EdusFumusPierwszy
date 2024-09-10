package com.example.edusfumuspierwszy.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.edusfumuspierwszy.LekcjeUtils

class SchoolPlanWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = SchoolPlanWidget();
}