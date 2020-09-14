package com.example.testretrofit2.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class CustomField : Serializable{
    @SerializedName("kind")
    @Expose
    var kind: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

    @SerializedName("fieldType")
    @Expose
    var fieldType: String? = null

    @SerializedName("deleted")
    @Expose
    var deleted: Boolean? = null
}