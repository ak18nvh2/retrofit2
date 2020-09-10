package com.example.testretrofit2

import com.example.testretrofit2.models.Contact
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class FJsonGet {
    @SerializedName("contacts")
    @Expose
    var contacts: List<Contact>? = null

    @SerializedName("total_contacts")
    @Expose
    var totalContacts: Int? = null
}