package com.example.testretrofit2.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class BodyPost {
    @SerializedName("contact")
    @Expose
    var contactPost: ContactPost? = null
}