package com.example.edusfumuspierwszy

import android.util.Log
import androidx.annotation.AnyRes
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.descriptors.StructureKind
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

val retrofit = Retrofit.Builder()
    .baseUrl("https://zs2ostrzeszow.edupage.org/timetable/server/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

data class RequestPayload(
    @SerializedName("__args") val args: List<Any?>,
    @SerializedName("__gsh") val gsh: String
)

interface MyApi {
    @Headers("Content-Type: application/json")
    @POST("regulartt.js?__func=regularttGetData")
    suspend fun getData(@Body payload: RequestPayload): Response<JsonObject>
}

object Lekcje {
    var responseText: String = ""

    fun test(): String {
        return "testasdwa"
    }

    // Function to send the POST request and handle the response
    suspend fun fetchData() {
        val myApi = retrofit.create(MyApi::class.java)
        val payload = RequestPayload(
            args = listOf(null, "269"),  // The arguments as per the PHP code
            gsh = "00000000"  // The __gsh value
        )

        try {
            // Make the API request
            val response = myApi.getData(payload)
            if (response.isSuccessful) {
                val rawResponse = response.body();
                val tables = rawResponse?.getAsJsonObject("r")
                    ?.getAsJsonObject("dbiAccessorRes")
                    ?.getAsJsonArray("tables");
                val classes = tables?.get(12)?.getAsJsonObject()?.getAsJsonObject("data_rows");
                val przedmioty = tables?.get(13)?.getAsJsonObject()?.getAsJsonObject("data_rows");
                val teachers = tables?.get(14)?.getAsJsonObject()?.getAsJsonObject("data_rows");
                Log.d("Test", "Tabele ${tables} ${classes} ${przedmioty} ${teachers}");
            } else {
                Log.d("Error code", "Error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.d("Lekcje exception", "Exception: ${e.message}")
        }
    }
}
