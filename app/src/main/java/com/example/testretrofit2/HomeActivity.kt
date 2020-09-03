package com.example.testretrofit2

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_processbar.*
import kotlinx.android.synthetic.main.dialog_select.*
import kotlinx.android.synthetic.main.dialog_select.tv_TitleOfCustomDialogConfirm
import kotlinx.android.synthetic.main.dialog_yes_no.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity(), View.OnClickListener,
    ContactAdapter.IRecyclerViewWithHomeActivity {
    private var list: ArrayList<Contact>? = ArrayList<Contact>()
    private val REQUEST_HOME_TO_CREATE_OR_UPDATE = 10
    var contactAdapter: ContactAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var recyclerView: RecyclerView = findViewById(R.id.rv_Contact)
        contactAdapter = ContactAdapter(this, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        list?.let { contactAdapter?.setList(it) }
        recyclerView.adapter = contactAdapter
        btn_ReloadData.setOnClickListener(this)
        btn_CreateNewContact.setOnClickListener(this)
        readData()
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_CreateNewContact -> {
                var intent = Intent(this, CreateAndUpdateEmployee::class.java)
                intent.putExtra("BUTTON", 2)
                var bundle = Bundle()
                bundle.putSerializable("CONTACT_LIST", this.list)
                intent.putExtras(bundle)
                startActivityForResult(intent, REQUEST_HOME_TO_CREATE_OR_UPDATE)
            }
            btn_ReloadData -> {
                readData()
            }
        }
    }

    private fun readData() {
        this.list?.clear()
        contactAdapter?.setList(this.list!!)
        val dialogProcess = MaterialDialog(this)
            .noAutoDismiss()
            .customView(R.layout.dialog_processbar)
        dialogProcess.setCancelable(false)
        var callGet = RetrofitClient.instance.getContacts()
        dialogProcess.btn_CancelUpdate.setOnClickListener() {
            callGet.cancel()
            dialogProcess.dismiss()
        }
        dialogProcess.show()
        dialogProcess.btn_CancelUpdate.setOnClickListener(){
            callGet.cancel()
        }
        callGet.enqueue(object : Callback<FJsonGet> {
            override fun onFailure(call: Call<FJsonGet>, t: Throwable) {
                dialogProcess.dismiss()
                if(callGet.isCanceled){
                    Toast.makeText(applicationContext, "Canceled successfully!", Toast.LENGTH_SHORT).show()
                }
                btn_ReloadData.visibility = View.VISIBLE
                Toast.makeText(
                    applicationContext,
                    "co van de ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("AAAAARead Failure", t.message)
            }

            override fun onResponse(call: Call<FJsonGet>, response: Response<FJsonGet>) {
                dialogProcess.dismiss()
                if (response.isSuccessful) {
                    list = response.body()?.contacts as ArrayList<Contact>?
                    contactAdapter?.setList(list!!)
                    btn_ReloadData.visibility = View.INVISIBLE
                } else {
                    Toast.makeText(
                        applicationContext,
                        "lam gi co gi," + response.code().toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    btn_ReloadData.visibility = View.VISIBLE
                    Log.d("AAAAARead response !", response.message() + response.code())
                }
            }
        }

        )


    }

    //
    override fun doSomeThingOnClick(contact: Contact) {
        var intent = Intent(this, DetailActivity::class.java)
        var bundle = Bundle()
        bundle.putSerializable("CONTACT", contact)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun doSomeThingOnLongClick(contact: Contact) {

        val dialogSelect = MaterialDialog(this)
            .noAutoDismiss()
            .customView(R.layout.dialog_select)

        dialogSelect.setCancelable(false)
        dialogSelect.show()
        dialogSelect.btn_CancelDialog.setOnClickListener() {
            dialogSelect.dismiss()
        }
        dialogSelect.btn_Delete.setOnClickListener() {
          // window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            dialogSelect.dismiss()
            val dialogYesNo = MaterialDialog(this)
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
                val dialog = MaterialDialog(this)
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
                        Toast.makeText(applicationContext, "fail ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.d("AAAAAADelete Failure",t.message )

                    }

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        dialog.dismiss()
                        if(response.isSuccessful){
                            Log.d("AAAAAADelete Successful",response.message() + " " + response.code() )
                           // window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(applicationContext, "Deleted Successful!", Toast.LENGTH_SHORT).show()
                            readData()
                        } else {
                            Log.d("AAAAAADelete response !",response.message() + " " + response.code())

                            Toast.makeText(applicationContext, "Cann't delete! Please try again!", Toast.LENGTH_SHORT).show()
                        }

                    }

                })


            }

        }
        dialogSelect.btn_Change.setOnClickListener() {
            var intent = Intent(this, CreateAndUpdateEmployee::class.java)
            var bundle = Bundle()
            var custom = Custom()
            if(contact.customFields!!.size > 1) {
                custom.stringImage = contact.customFields?.get(1)?.value
            }

            custom.stringAge = contact.customFields?.get(0)?.value
            var contactPost = ContactPost()
            contactPost.custom = custom
            contactPost.email = contact.email
            contactPost.firstName = contact.firstName
            contactPost.lastName = contact.lastName
            bundle.putSerializable("CONTACTPOST", contactPost)
            intent.putExtras(bundle)
            intent.putExtra("BUTTON", 1)
            startActivityForResult(intent, REQUEST_HOME_TO_CREATE_OR_UPDATE)
            dialogSelect.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_HOME_TO_CREATE_OR_UPDATE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show()
                readData()
            }
        }
    }
}