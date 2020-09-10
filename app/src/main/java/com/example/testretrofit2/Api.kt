package com.example.testretrofit2

import com.example.testretrofit2.models.Contact
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("contacts/bookmark")
    fun getContacts(): Call<FJsonGet>



    @POST("contact")
    fun insertContact(@Body bodyPost: BodyPost): Call<Contact>


    @DELETE("contact/{id}")
    fun deleteContact(@Path("id") contactId: String): Call<Unit>
}
