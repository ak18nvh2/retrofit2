package com.example.testretrofit2

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.testretrofit2.models.Contact
import kotlinx.android.synthetic.main.dialog_processbar.*
import kotlinx.android.synthetic.main.dialog_select.tv_TitleOfCustomDialogConfirm
import kotlinx.android.synthetic.main.dialog_yes_no.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FJsonViewModel : ViewModel() {
    var listContact: MutableLiveData<List<Contact>> = MutableLiveData()

    fun sortASCByFirstName(){
        var arrListContact = this.listContact.value?.sortedWith(compareBy {it.firstName})?.toTypedArray()?.toList()
        listContact.value = arrListContact as ArrayList<Contact>
    }
    fun sortDESCByFirstName(){
        var arrListContact = this.listContact.value?.sortedWith(compareBy {it.firstName})?.toTypedArray()?.toList()
        listContact.value = (arrListContact as ArrayList<Contact>).reversed()
    }
    fun sortASCByAge(){
        var arrListContact = this.listContact.value?.sortedWith(compareBy {it.customFields?.get(0)?.value})?.toTypedArray()?.toList()
        listContact.value = arrListContact as ArrayList<Contact>
    }
    fun sortDESCByAge(){
        var arrListContact = this.listContact.value?.sortedWith(compareBy {it.customFields?.get(0)?.value})?.toTypedArray()?.toList()
        listContact.value = (arrListContact as ArrayList<Contact>).reversed()
    }
    fun readFJson(context: Context) {
        val dialogProcess = MaterialDialog(context)
            .noAutoDismiss()
            .customView(R.layout.dialog_processbar)
        dialogProcess.setCancelable(false)
        var callGet = RetrofitClient.instance.getContacts()
        dialogProcess.btn_CancelUpdate.setOnClickListener() {
            callGet.cancel()
            dialogProcess.dismiss()
        }
        dialogProcess.show()
        dialogProcess.btn_CancelUpdate.setOnClickListener() {
            callGet.cancel()
        }
        callGet.enqueue(object : Callback<FJsonGet> {
            override fun onFailure(call: Call<FJsonGet>, t: Throwable) {
                dialogProcess.dismiss()
                if (callGet.isCanceled) {
                    Toast.makeText(context, "Canceled successfully!", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(
                        context,
                        "co van de ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d("AAAAARead Failure", t.message)
            }

            override fun onResponse(call: Call<FJsonGet>, response: Response<FJsonGet>) {
                dialogProcess.dismiss()
                if (response.isSuccessful) {
                    listContact.value = response.body()?.contacts
                } else {
                    Toast.makeText(
                        context,
                        "lam gi co gi," + response.code().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("AAAAARead response !", response.message() + response.code())
                }
            }
        }

        )
    }

    fun createContact(context: Context, bodyPost: BodyPost, activity: CreateAndUpdateEmployeeActivity){

        val callInsert = RetrofitClient.instance.insertContact(bodyPost)
        val dialog = MaterialDialog(context)
            .noAutoDismiss()
            .customView(R.layout.dialog_processbar)
        dialog.show()
        dialog.setCancelable(false)
        dialog.btn_CancelUpdate.setOnClickListener() {
            callInsert.cancel()
        }
        callInsert.enqueue(object : Callback<Contact> {
            override fun onFailure(call: Call<Contact>, t: Throwable) {

                Log.d("AAAAACreate Failure", t.message)
                dialog.dismiss()
                if (callInsert.isCanceled) {
                    Toast.makeText(
                        context,
                        "Canceled Successful!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Save failed! Please try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(
                call: Call<Contact>,
                response: Response<Contact>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Saved Successful!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    activity.checkFinish()
                    Log.d("AAAAACreate response", response.body()?.contactId)
                } else {
                    Toast.makeText(
                        context,
                        "Save failed! Please try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "AAAAAACreate response !",
                        response.code().toString() + response.message()
                    )
                }
                dialog.dismiss()
            }
        })

    }

    fun deleteContact(context: Context, contact: Contact){
        val dialogYesNo = MaterialDialog(context)
            .noAutoDismiss()
            .customView(R.layout.dialog_yes_no)
        dialogYesNo.show()
        dialogYesNo.setCancelable(false)
        dialogYesNo.tv_TitleOfCustomDialogConfirm.text = "Are you sure delete this?"
        dialogYesNo.btn_CancelDialogConfirm.setOnClickListener() {
            dialogYesNo.dismiss()
        }
        dialogYesNo.btn_AcceptDiaLogConFirm.setOnClickListener(){
            dialogYesNo.dismiss()
            val callDelete =  RetrofitClient.instance.deleteContact(contact.contactId!!)
            val dialog = MaterialDialog(context)
                .noAutoDismiss()
                .customView(R.layout.dialog_processbar)
            dialog.show()
            dialog.setCancelable(false)
            dialog.btn_CancelUpdate.setOnClickListener(){
                callDelete.cancel()
                dialog.dismiss()
            }
            callDelete.enqueue(object: Callback<Unit>{

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    dialog.dismiss()
                    // window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(context, "fail ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("AAAAAADelete Failure",t.message )

                }

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    dialog.dismiss()
                    if(response.isSuccessful){
                        Log.d("AAAAAADelete Successful",response.message() + " " + response.code() )
                        // window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(context, "Deleted Successful!", Toast.LENGTH_SHORT).show()
                        readFJson(context)
                    } else {
                        Log.d("AAAAAADelete response !",response.message() + " " + response.code())

                        Toast.makeText(context, "Can't delete! Please try again!", Toast.LENGTH_SHORT).show()
                    }

                }

            })


        }

    }

}