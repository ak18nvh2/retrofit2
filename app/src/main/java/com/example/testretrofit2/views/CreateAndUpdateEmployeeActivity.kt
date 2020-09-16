package com.example.testretrofit2.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.testretrofit2.R
import com.example.testretrofit2.RetrofitClient
import com.example.testretrofit2.models.BodyPost
import com.example.testretrofit2.models.Contact
import com.example.testretrofit2.models.ContactPost
import com.example.testretrofit2.models.Custom
import com.example.testretrofit2.viewmodels.FJsonViewModel
import kotlinx.android.synthetic.main.activity_create_update_employee.*
import kotlinx.android.synthetic.main.dialog_processbar.*
import kotlinx.android.synthetic.main.dialog_yes_no.*
import kotlinx.android.synthetic.main.dialog_yes_no.tv_TitleOfCustomDialogConfirm
import retrofit2.Call


class CreateAndUpdateEmployeeActivity : AppCompatActivity(), View.OnClickListener {
    private var BUTTON_TYPE = 0 //  1 is change profile, 2 is create new employee
    private var contactPost: ContactPost =
        ContactPost()
    private var custom: Custom =
        Custom()
    var fJsonViewModel: FJsonViewModel =
        FJsonViewModel()
    lateinit var dialog: MaterialDialog
    private var arrayList: ArrayList<Contact> = ArrayList()
    private val REQUEST_SELECT_IMAGE = 1111
    private lateinit var callInsert: Call<Contact>
    var bodyPost = BodyPost()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_update_employee)
        init()
        registerLiveDataListener()

    }

    private fun registerLiveDataListener() {
        val notificationObserver = Observer<String> {
            when (it) {
                "BodyPostOK" -> {
                    callInsert = RetrofitClient.instance.insertContact(bodyPost)
                    dialog = MaterialDialog(this)
                        .noAutoDismiss()
                        .customView(R.layout.dialog_processbar)
                    dialog.show()
                    dialog.setCancelable(false)
                    dialog.btn_CancelUpdate.setOnClickListener() {
                        callInsert.cancel()
                    }
                    fJsonViewModel.createContact(
                        bodyPost,
                        this.arrayList,
                        BUTTON_TYPE,
                        this.callInsert
                    )
                }
                "Saved Successful!" -> {
                    val intent: Intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    this.finish()
                }
                else -> {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
        fJsonViewModel.notification.observe(this, notificationObserver)
    }

    private fun setDefaultInformation() {
        registerLiveDataListener()
        edt_InputAge.setText(contactPost.custom?.stringAge)
        edt_InputEmail.setText(contactPost.email)
        edt_InputFirstName.setText(contactPost.firstName)
        edt_InputLastName.setText(contactPost.lastName)
        if (contactPost.custom?.stringImage != null) {
            img_AvatarCreateOrUpdate.setImageURI(Uri.parse(contactPost.custom?.stringImage))
        }
    }

    private fun init() {
        btn_Cancel.setOnClickListener(this)
        btn_Save.setOnClickListener(this)
        btn_BackCreateOrUpdate.setOnClickListener(this)
        img_AvatarCreateOrUpdate.setOnClickListener(this)
        var intent = intent
        BUTTON_TYPE = intent.getIntExtra("BUTTON", 0)
        var bundle = intent.extras
        if (bundle != null) {
            if (BUTTON_TYPE == 1) {
                this.contactPost = bundle.getSerializable("CONTACTPOST") as ContactPost
                setDefaultInformation()
                edt_InputEmail.isFocusable = false
            } else if (BUTTON_TYPE == 2) {
                this.arrayList = bundle.getSerializable("CONTACT_LIST") as ArrayList<Contact>
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun selectImage() {
        val i = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).setType("image/*")
        startActivityForResult(i, REQUEST_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                this.custom.stringImage = data?.data.toString()
                img_AvatarCreateOrUpdate.setImageURI(Uri.parse(this.custom.stringImage))
            }
        }
    }

    private fun insertContactToServer() {
        this.custom.stringAge = edt_InputAge.text.toString().trim()
        this.contactPost.custom = this.custom
        this.contactPost.lastName = edt_InputLastName.text.toString().trim() + " "
        this.contactPost.firstName = edt_InputFirstName.text.toString().trim()
        this.contactPost.email = edt_InputEmail.text.toString().trim()
        bodyPost.contactPost = this.contactPost
        fJsonViewModel.checkBodyPost(bodyPost)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onClick(v: View?) {
        when (v) {
            img_AvatarCreateOrUpdate ->
                selectImage()
            btn_Save -> {
                val dialogYesNo = MaterialDialog(this)
                    .noAutoDismiss()
                    .customView(R.layout.dialog_yes_no)
                dialogYesNo.show()
                dialogYesNo.setCancelable(false)
                dialogYesNo.tv_TitleOfCustomDialogConfirm.text = "Are you sure save this?"
                dialogYesNo.btn_CancelDialogConfirm.setOnClickListener() {
                    dialogYesNo.dismiss()
                }
                dialogYesNo.btn_AcceptDiaLogConFirm.setOnClickListener() {
                    dialogYesNo.dismiss()
                    insertContactToServer()
                }
            }
            btn_Cancel -> {
                finish()
            }
            btn_BackCreateOrUpdate -> {
                finish()
            }
        }
    }
}