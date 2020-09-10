package com.example.testretrofit2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.testretrofit2.models.Contact
import kotlinx.android.synthetic.main.activity_create_update_employee.*
import kotlinx.android.synthetic.main.dialog_processbar.*
import kotlinx.android.synthetic.main.dialog_yes_no.*
import kotlinx.android.synthetic.main.dialog_yes_no.tv_TitleOfCustomDialogConfirm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateAndUpdateEmployeeActivity : AppCompatActivity(), View.OnClickListener {
    private var BUTTON_TYPE = 0 //  1 is change profile, 2 is create new employee
    private var contactPost: ContactPost = ContactPost()
    private var custom: Custom = Custom()
    var fJsonViewModel: FJsonViewModel = FJsonViewModel()
    private var arrayList: ArrayList<Contact> = ArrayList()
    val REQUEST_SELECT_IMAGE = 1111
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_update_employee)
        init()
        fJsonViewModel =
            ViewModelProviders.of(this).get<FJsonViewModel>(FJsonViewModel::class.java)
    }

    private fun setDefaultInformation() {
        edt_InputAge.setText(contactPost.custom?.stringAge)
        edt_InputEmail.setText(contactPost.email)
        edt_InputFirstName.setText(contactPost.firstName)
        edt_InputLastName.setText(contactPost.lastName)
        if ( contactPost.custom?.stringImage != null) {
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
    fun selectImage(view: View) {
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

                this.custom?.stringImage = data?.data.toString()
                img_AvatarCreateOrUpdate.setImageURI(Uri.parse(this.custom?.stringImage))
            }
        }
    }

    private var isFinish = 0
    fun checkFinish() {

            val intent: Intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            this.finish()


    }

    private fun insertContactToServer() {
        val pattenEmail = Regex("[a-zA-Z0-9_.]+@[a-zA-Z]+\\.[a-zA-Z]+")
        if (edt_InputFirstName.text.toString().trim() == "") {
            Toast.makeText(applicationContext, "First Name must not be empty!", Toast.LENGTH_SHORT)
                .show()
        }  else if (edt_InputLastName.text.toString().trim() == "") {
            Toast.makeText(applicationContext, "Last Name must not be empty!", Toast.LENGTH_SHORT)
                .show()
        }  else if (edt_InputEmail.text.toString().trim() == "") {
            Toast.makeText(applicationContext, "Email must not be empty!", Toast.LENGTH_SHORT)
                .show()
        } else if (!pattenEmail.containsMatchIn(edt_InputEmail.text.toString())) {
            Toast.makeText(applicationContext, "Must type correct email form!", Toast.LENGTH_SHORT)
                .show()
        } else if (edt_InputAge.text.toString().trim() == "") {
            Toast.makeText(applicationContext, "Age must not be empty!", Toast.LENGTH_SHORT).show()
        } else {
            this.custom?.stringAge = edt_InputAge.text.toString().trim()
            this.contactPost.custom = this.custom
            this.contactPost.lastName = edt_InputLastName.text.toString().trim() + " "
            this.contactPost.firstName = edt_InputFirstName.text.toString().trim()
            this.contactPost.email = edt_InputEmail.text.toString().trim()
            var bodyPost = BodyPost()
            bodyPost.contactPost = this.contactPost

            fJsonViewModel.createContact(this, bodyPost,this)


        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onClick(v: View?) {
        when (v) {
            img_AvatarCreateOrUpdate ->
                selectImage(img_AvatarCreateOrUpdate)

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
                    if (BUTTON_TYPE == 1) {
                        if (this.contactPost.email != edt_InputEmail.text.toString()) {
                            Toast.makeText(
                                applicationContext,
                                "Must not change Email!",
                                Toast.LENGTH_SHORT
                            ).show()
                            edt_InputEmail.setText(this.contactPost.email)

                        } else {
                            insertContactToServer()
                        }
                    } else if (BUTTON_TYPE == 2) {
                        var checkSameEmail = 0
                        arrayList.forEachIndexed { index, contact ->
                            if (contact.email == edt_InputEmail.text.toString()) {
                                checkSameEmail = 1
                            }
                        }
                        if (checkSameEmail == 1) {
                            Toast.makeText(
                                applicationContext,
                                "This email already exists!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            insertContactToServer()
                        }

                    }
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