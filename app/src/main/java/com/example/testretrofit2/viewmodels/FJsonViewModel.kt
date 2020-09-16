package com.example.testretrofit2.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.testretrofit2.views.CreateAndUpdateEmployeeActivity
import com.example.testretrofit2.R
import com.example.testretrofit2.RetrofitClient
import com.example.testretrofit2.models.BodyPost
import com.example.testretrofit2.models.Contact
import com.example.testretrofit2.models.FJsonGet
import kotlinx.android.synthetic.main.dialog_processbar.*
import kotlinx.android.synthetic.main.dialog_select.tv_TitleOfCustomDialogConfirm
import kotlinx.android.synthetic.main.dialog_yes_no.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class FJsonViewModel : ViewModel() {
    var listContact: MutableLiveData<List<Contact>> = MutableLiveData()
    var notification: MutableLiveData<String> = MutableLiveData()
    fun sortASCByFirstName() {
        var arrListContact =
            this.listContact.value?.sortedWith(compareBy { it.firstName?.toLowerCase() })?.toList()
        listContact.value = arrListContact as ArrayList<Contact>
    }

    fun sortDESCByFirstName() {
        var arrListContact =
            this.listContact.value?.sortedWith(compareBy { it.firstName?.toLowerCase() })?.toList()
        listContact.value = (arrListContact as ArrayList<Contact>).reversed()
    }

    fun sortASCByAge() {
        var arrListContact =
            this.listContact.value?.sortedWith(compareBy { it.customFields?.get(0)?.value?.toInt() })
                ?.toList()
        listContact.value = arrListContact as ArrayList<Contact>
    }

    fun sortDESCByAge() {
        var arrListContact =
            this.listContact.value?.sortedWith(compareBy { it.customFields?.get(0)?.value?.toInt() })
                ?.toList()
        listContact.value = (arrListContact as ArrayList<Contact>).reversed()
    }

    fun searchByName(name: String, arr: ArrayList<Contact>) {
        var arrayListContact: ArrayList<Contact> = ArrayList()
        arr.forEachIndexed { _, contact ->
            if (contact.firstName?.contains(name)!! || contact.lastName?.contains(name)!!) {
                arrayListContact.add(contact)
            }
        }
        this.listContact.value = arrayListContact
    }

    fun getAllContact(callGet: Call<FJsonGet>) {

        callGet.enqueue(object : Callback<FJsonGet> {
            override fun onFailure(call: Call<FJsonGet>, t: Throwable) {
                if (callGet.isCanceled) {
                    notification.value = "Canceled successful!"
                } else {
                    notification.value = "Can't load data, please try again!"
                }
            }

            override fun onResponse(call: Call<FJsonGet>, response: Response<FJsonGet>) {
                if (response.isSuccessful) {
                    listContact.value = response.body()?.contacts
                    notification.value = "Load successful!"
                } else {
                    notification.value = "Can't load data, please try again!"
                }
            }
        }
        )

    }

    fun checkBodyPost(bodyPost: BodyPost) {
        val pattenEmail = Regex("[a-zA-Z0-9_.]+@[a-zA-Z]+\\.[a-zA-Z]+")
        if (bodyPost.contactPost?.firstName == "") {
            notification.value = "First Name must not be empty!"
        } else if (bodyPost.contactPost?.lastName?.trim() == "") {
            notification.value = "Last Name must not be empty!"
        } else if (bodyPost.contactPost?.email == "") {
            notification.value = "Email must not be empty!"
        } else if (!pattenEmail.containsMatchIn(bodyPost.contactPost?.email.toString())) {
            notification.value = "Must type correct email form!"
        } else if (bodyPost.contactPost?.custom?.stringAge == "") {
            notification.value = "Age must not be empty!"
        } else {
            notification.value = "BodyPostOK"
        }
    }

    fun createContact(
        bodyPost: BodyPost,
        arr: ArrayList<Contact>,
        btnType: Int,
        callInsert: Call<Contact>
    ) {
        var isSameEmail = 0
        if (btnType == 2) {
            arr.forEachIndexed { index, contact ->
                if (contact.email == bodyPost.contactPost?.email.toString()) {
                    isSameEmail = 1
                }
            }
            if (isSameEmail == 1) {
                notification.value = "This email already exists!"
            }
        }
        if (btnType == 1 || isSameEmail == 0) {
            callInsert.enqueue(object : Callback<Contact> {
                override fun onFailure(call: Call<Contact>, t: Throwable) {
                    if (callInsert.isCanceled) {
                        notification.value = "Canceled successful!"
                    } else {
                        notification.value = "Can't update data, please try again!"
                    }
                }

                override fun onResponse(
                    call: Call<Contact>,
                    response: Response<Contact>
                ) {
                    if (response.isSuccessful) {
                        notification.value = "Saved Successful!"
                    } else {
                        notification.value = "Can't update data, please try again!"
                    }

                }
            })
        }
    }


    fun deleteContact(callDelete: Call<Unit>, contact: Contact) {

        callDelete.enqueue(object : Callback<Unit> {

            override fun onFailure(call: Call<Unit>, t: Throwable) {

                if (callDelete.isCanceled) {
                    notification.value = "Canceled Successful!"
                } else {
                    notification.value = "Can't delete! Please try again!"
                }
            }
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    notification.value = "Deleted Successful!"
                } else {
                    notification.value = "Can't delete! Please try again!"
                }
            }
        })
    }

}

