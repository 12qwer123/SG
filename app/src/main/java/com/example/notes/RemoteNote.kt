package com.example.notes

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
private const val BASE_URL = "http://beta.mrdekk.ru/"
private const val BEARER_TOKEN = "Bearer 8eae7e07-9f6d-4c40-becd-3a3c0e6258fa"

interface NotesApiService {
    @GET("notes")
    suspend fun getNotes(): List<Note>

    @POST("notes")
    suspend fun createNote(@Body note: Note): Note

    @PUT("notes/{id}")
    suspend fun updateNote(@Path("id") id: String, @Body note: Note): Note

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: String): Boolean
}
class RemoteNotebook {
    private val notesApi: NotesApiService

    init {
        val httplogging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(httplogging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", BEARER_TOKEN)
                    .build()
                chain.proceed(request)
            }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        notesApi = retrofit.create(NotesApiService::class.java)
    }
    //загрузка
    suspend fun loadNotes(): List<Note> {
        return try {
            notesApi.getNotes()
        } catch (e: Exception) { emptyList() }
    }
    //сохранение
    suspend fun saveNote(note: Note): Boolean {
        return try {
            if (note.uid.isBlank()) {
                notesApi.createNote(note)
            } else {
                notesApi.updateNote(note.uid, note)
            }
            true
        } catch (e: Exception) { false }
    }
    //удаление заметки
    suspend fun deleteNote(uid: String): Boolean {
        return try {
            notesApi.deleteNote(uid)
        } catch (e: Exception) { false }
    }
}