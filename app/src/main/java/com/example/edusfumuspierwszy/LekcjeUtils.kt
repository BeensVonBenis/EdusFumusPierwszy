package com.example.edusfumuspierwszy

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class SchoolPlanTile(
    val subject: String,
    val teacher: String,
    val classroom: String,
    val group: String,
    val day: Int,
    val period: Int
)

data class ClassTile(
    val id: String,
    val name: String,
)

object LekcjeUtils {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://zs2ostrzeszow.edupage.org/timetable/server/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    interface MyApi {
        @Headers("Content-Type: application/json")
        @POST("regulartt.js?__func=regularttGetData")
        suspend fun getData(@Body payload: RequestPayload): Response<JsonObject>
    }

    data class RequestPayload(
        @SerializedName("__args") val args: List<Any?>,
        @SerializedName("__gsh") val gsh: String
    )

    private var bufferedData: JsonArray? = null;

    var bufferedSchoolPlan: List<List<SchoolPlanTile>>? = null;

    private var bufferedSelectedClassId: String? = null;

    suspend fun fetchData(): JsonArray? {
        val myApi = retrofit.create(LekcjeUtils.MyApi::class.java)
        val payload = LekcjeUtils.RequestPayload(
            args = listOf(null, "269"),
            gsh = "00000000"
        )

        return try {
            val response = myApi.getData(payload)
            if (response.isSuccessful) {
                val rawResponse = response.body()
                val tables = rawResponse?.getAsJsonObject("r")
                    ?.getAsJsonObject("dbiAccessorRes")
                    ?.getAsJsonArray("tables")
                bufferedData = tables;
                tables
            } else {
                null;
            }
        } catch (e: Exception) {
            null;
        }
    }

    private fun filterLessons(selectedClassId: String?, selectedTeacherId: String?): JsonArray? {
        if (selectedClassId != null) {
            val lessons = bufferedData?.get(18)?.asJsonObject?.getAsJsonArray("data_rows")
//        val selectedClass =
//            classes?.find { it.asJsonObject["short"].asString == "4PI Tech-p" }
            var filteredLessons: JsonArray? = null
            filteredLessons = lessons?.filter { lesson ->
                val classIds = lesson.asJsonObject["classids"]?.asJsonArray
                classIds?.any { it.asString == selectedClassId } == true
            }?.let { JsonArray().apply { it.forEach { add(it) } } }
            return filteredLessons;
        } else if (selectedTeacherId != null) {
            val lessons = bufferedData?.get(18)?.asJsonObject?.getAsJsonArray("data_rows")
//        val selectedClass =
//            classes?.find { it.asJsonObject["short"].asString == "4PI Tech-p" }
            var filteredLessons: JsonArray? = null
            filteredLessons = lessons?.filter { lesson ->
                val classIds = lesson.asJsonObject["teacherids"]?.asJsonArray
                classIds?.any { it.asString == selectedTeacherId } == true
            }?.let { JsonArray().apply { it.forEach { add(it) } } }
            return filteredLessons;
        } else {
            return null;
        }
    }

    private fun parseDataForSchoolPlan(
        filteredTiles: JsonArray?,
        filteredLessons: JsonArray?
    ): List<List<SchoolPlanTile>> {
        val classrooms = bufferedData?.get(11)?.asJsonObject?.getAsJsonArray("data_rows")
        val subjects = bufferedData?.get(13)?.asJsonObject?.getAsJsonArray("data_rows")
        val teachers = bufferedData?.get(14)?.asJsonObject?.getAsJsonArray("data_rows")
        val groups = bufferedData?.get(15)?.asJsonObject?.getAsJsonArray("data_rows")
        val schoolPlan = mutableListOf<SchoolPlanTile>()
        filteredTiles?.forEach { tile ->
            val tileObject = tile.asJsonObject
            val lessonId = tileObject["lessonid"]?.asString
            val correspondingLesson = filteredLessons?.find { lesson ->
                lesson.asJsonObject["id"]?.asString == lessonId
            }?.asJsonObject

            if (correspondingLesson != null) {
                val subjectId = correspondingLesson["subjectid"]?.asString
                val duration = correspondingLesson["durationperiods"]?.asInt ?: 0

                val day = tileObject["days"]?.asString?.indexOf("1") ?: -1
                val period = tileObject["period"]?.asInt ?: 0

                val subject =
                    subjects?.find { it.asJsonObject["id"].asString == subjectId }
                        ?.asJsonObject?.get("short")?.asString ?: "Ø"

                val teacherIdsArray = correspondingLesson["teacherids"]?.asJsonArray
                val teacherId =
                    teacherIdsArray?.firstOrNull()?.asString ?: "Ø"
                val teacher = teachers?.find { it.asJsonObject["id"].asString == teacherId }
                    ?.asJsonObject?.get("short")?.asString ?: "Ø"

                val groupIdsArray = correspondingLesson["groupids"]?.asJsonArray
                val groupId = groupIdsArray?.firstOrNull()?.asString ?: "Ø"
                var group = groups?.find { it.asJsonObject["id"].asString == groupId }
                    ?.asJsonObject?.get("name")?.asString ?: "Ø"
                if (group == "Cała klasa") {
                    group = ""
                }
                val classroomIdsArray = tileObject["classroomids"]?.asJsonArray
                val classroomId =
                    classroomIdsArray?.firstOrNull()?.asString ?: "Ø"
                val classroom =
                    classrooms?.find { it.asJsonObject["id"].asString == classroomId }
                        ?.asJsonObject?.get("short")?.asString ?: "Ø"

                if (duration != 0) {
                    for (i in 0 until duration) {
                        val schoolPlanTile = SchoolPlanTile(
                            subject = subject,
                            teacher = teacher,
                            classroom = classroom,
                            group = group,
                            day = day,
                            period = period + i
                        )
                        schoolPlan.add(schoolPlanTile)
                    }
                } else {
                    val schoolPlanTile = SchoolPlanTile(
                        subject = subject,
                        teacher = teacher,
                        classroom = classroom,
                        group = group,
                        day = day,
                        period = period
                    )
                    schoolPlan.add(schoolPlanTile)
                }
            }
        }
        var currentSchoolPlan: List<List<SchoolPlanTile>>? = null
        currentSchoolPlan = schoolPlan.groupBy { it.day }
            .toSortedMap()
            .values.toList();
        return currentSchoolPlan;
    }

    private fun generateSchoolPlan(selectedClassId: String?): List<List<SchoolPlanTile>> {
        val tiles = bufferedData?.get(20)?.asJsonObject?.getAsJsonArray("data_rows")
        val filteredLessons = filterLessons(selectedClassId, null);
        var filteredTiles: JsonArray? = null
        filteredTiles = tiles?.filter { tile ->
            val lessonId = tile.asJsonObject["lessonid"]?.asString
            filteredLessons?.any { lesson ->
                val lessonObjId = lesson.asJsonObject["id"]?.asString
                lessonObjId == lessonId
            } == true
        }?.let { JsonArray().apply { it.forEach { add(it) } } }

        val schoolPlan = parseDataForSchoolPlan(filteredTiles, filteredLessons)
        this.bufferedSchoolPlan = schoolPlan;
        return schoolPlan;
    }

    fun getTeacherPlan(selectedTeacherId: String?): List<List<SchoolPlanTile>> {
        val tiles = bufferedData?.get(20)?.asJsonObject?.getAsJsonArray("data_rows")
        val filteredLessons = filterLessons(null, selectedTeacherId);
        var filteredTiles: JsonArray? = null
        filteredTiles = tiles?.filter { tile ->
            val lessonId = tile.asJsonObject["lessonid"]?.asString
            filteredLessons?.any { lesson ->
                val lessonObjId = lesson.asJsonObject["id"]?.asString
                lessonObjId == lessonId
            } == true
        }?.let { JsonArray().apply { it.forEach { add(it) } } }

        val schoolPlan = parseDataForSchoolPlan(filteredTiles, filteredLessons)
        return schoolPlan;
    }

    fun getSchoolPlan(selectedClassId: String?): List<List<SchoolPlanTile>>? {
        return if (bufferedSchoolPlan != null && selectedClassId == bufferedSelectedClassId) {
            bufferedSchoolPlan;
        } else {
            this.bufferedSelectedClassId = selectedClassId;
            generateSchoolPlan(selectedClassId);
            bufferedSchoolPlan;
        }
    }

    fun getClassesList(): List<ClassTile> {
        val classesList = mutableListOf<ClassTile>();
        bufferedData?.get(12)?.asJsonObject?.getAsJsonArray("data_rows")?.forEach { el ->
            val classTile = ClassTile(
                id = el.asJsonObject.get("id").asString,
                name = el.asJsonObject.get("name").asString
            )
            classesList.add(classTile);
        }
        return classesList;
    }

    fun getTeachersList(): List<ClassTile> {
        val classesList = mutableListOf<ClassTile>();
        bufferedData?.get(14)?.asJsonObject?.getAsJsonArray("data_rows")?.forEach { el ->
            val classTile = ClassTile(
                id = el.asJsonObject.get("id").asString,
                name = el.asJsonObject.get("name").asString
            )
            classesList.add(classTile);
        }
        return classesList;
    }
}