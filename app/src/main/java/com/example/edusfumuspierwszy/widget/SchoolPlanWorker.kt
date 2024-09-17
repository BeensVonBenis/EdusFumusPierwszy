package com.example.edusfumuspierwszy

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson

class SchoolPlanWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val schoolPlan = LekcjeUtils.getSchoolPlan(applicationContext, "-114", false)
        if (schoolPlan != null) {
            cacheSchoolPlan(applicationContext, schoolPlan)
        }
        updateWidget(applicationContext)
        return Result.success()
    }

    private fun cacheSchoolPlan(context: Context, schoolPlan: List<List<SchoolPlanTile>>) {
        val sharedPreferences =
            context.getSharedPreferences("SchoolPlanPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("cached_school_plan", Gson().toJson(schoolPlan))
        editor.apply()
    }

    private fun updateWidget(context: Context) {
        // Code to trigger widget update
    }
}
