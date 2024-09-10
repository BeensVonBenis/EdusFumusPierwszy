package com.example.edusfumuspierwszy

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.edusfumuspierwszy.widget.SchoolPlanWidget

/**
 * Implementation of App Widget functionality.
 */
class TestWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = SchoolPlanWidget()
}
