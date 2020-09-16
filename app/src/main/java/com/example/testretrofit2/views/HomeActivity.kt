package com.example.testretrofit2.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.testretrofit2.ContactAdapter
import com.example.testretrofit2.R
import com.example.testretrofit2.RetrofitClient
import com.example.testretrofit2.models.Contact
import com.example.testretrofit2.models.ContactPost
import com.example.testretrofit2.models.Custom
import com.example.testretrofit2.viewmodels.FJsonViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_processbar.*
import kotlinx.android.synthetic.main.dialog_select.*
import kotlinx.android.synthetic.main.dialog_select.tv_TitleOfCustomDialogConfirm
import kotlinx.android.synthetic.main.dialog_yes_no.*

class HomeActivity : AppCompatActivity(), View.OnClickListener,
    ContactAdapter.IRecyclerViewWithHomeActivity {
    private var list: ArrayList<Contact>? = null
    private val REQUEST_HOME_TO_CREATE_OR_UPDATE = 10
    var contactAdapter: ContactAdapter? = null
    var fJsonViewModel: FJsonViewModel = FJsonViewModel()
    var isInitList = 0
    private lateinit var dialogProcessLoad: MaterialDialog
    private lateinit var dialogProcessDelete: MaterialDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var recyclerView: RecyclerView = findViewById(R.id.rv_Contact)
        contactAdapter = ContactAdapter(this, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        list?.let { contactAdapter?.setList(it) }
        recyclerView.adapter = contactAdapter

        dialogProcessLoad = MaterialDialog(this).noAutoDismiss()
            .customView(R.layout.dialog_processbar)
        dialogProcessDelete = MaterialDialog(this).noAutoDismiss()
            .customView(R.layout.dialog_processbar)

        readData()
        registerLiveDataListener()

        // sr_rvContac.setOnRefreshListener(this)

        btn_ReloadData.setOnClickListener(this)
        btn_CreateNewContact.setOnClickListener(this)
        btn_sortACSByFirstName.setOnClickListener(this)
        btn_sortDESCByFirstName.setOnClickListener(this)
        btn_sortASCByAge.setOnClickListener(this)
        btn_Search.setOnClickListener(this)
        btn_sortDESCByAge.setOnClickListener(this)
    }

    private fun registerLiveDataListener() {
        val fJsonObserver = Observer<List<Contact>> { newListContact ->
            contactAdapter?.setList(newListContact as ArrayList<Contact>)
            if (isInitList == 0) {
                isInitList = 1
                this.list = newListContact as ArrayList<Contact>?
            }
        }
        fJsonViewModel.listContact.observe(this, fJsonObserver)

        val notiObserver = Observer<String> {
            dialogProcessLoad.dismiss()
            dialogProcessDelete.dismiss()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            if (it == "Deleted Successful!") {
                isInitList = 0
                readData()
            }

        }
        fJsonViewModel.notification.observe(this, notiObserver)
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_CreateNewContact -> {
                edt_SearchInput.text.clear()
                var intent = Intent(this, CreateAndUpdateEmployeeActivity::class.java)
                intent.putExtra("BUTTON", 2)
                var bundle = Bundle()
                bundle.putSerializable("CONTACT_LIST", this.list)
                intent.putExtras(bundle)
                startActivityForResult(intent, REQUEST_HOME_TO_CREATE_OR_UPDATE)
            }
            btn_ReloadData -> {
                readData()
                edt_SearchInput.text.clear()
            }
            btn_sortACSByFirstName -> {
                fJsonViewModel.sortASCByFirstName()
                edt_SearchInput.text.clear()
            }
            btn_sortDESCByFirstName -> {
                fJsonViewModel.sortDESCByFirstName()
                edt_SearchInput.text.clear()
            }
            btn_sortASCByAge -> {
                fJsonViewModel.sortASCByAge()
                edt_SearchInput.text.clear()
            }
            btn_sortDESCByAge -> {
                fJsonViewModel.sortDESCByAge()
                edt_SearchInput.text.clear()
            }
            edt_SearchInput -> {
                fJsonViewModel.listContact.value = this.list
            }
            btn_Search -> {
                fJsonViewModel.searchByName(edt_SearchInput.text.toString(), this.list!!)
            }

        }
    }

    private fun readData() {
        edt_SearchInput.text.clear()
        val callGet = RetrofitClient.instance.getContacts()
        dialogProcessLoad.setCancelable(false)
        dialogProcessLoad.btn_CancelUpdate.setOnClickListener() {
            callGet.cancel()
            dialogProcessLoad.dismiss()
        }
        dialogProcessLoad.show()
        fJsonViewModel.getAllContact(callGet)

    }

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
            dialogYesNo.btn_AcceptDiaLogConFirm.setOnClickListener() {
                dialogYesNo.dismiss()
                val callDelete = RetrofitClient.instance.deleteContact(contact.contactId!!)

                dialogProcessDelete.show()
                dialogProcessDelete.setCancelable(false)
                dialogProcessDelete.btn_CancelUpdate.setOnClickListener() {
                    callDelete.cancel()
                    dialogProcessDelete.dismiss()
                }
                fJsonViewModel.deleteContact(callDelete, contact)
            }}
            dialogSelect.btn_Change.setOnClickListener() {
                var intent = Intent(this, CreateAndUpdateEmployeeActivity::class.java)
                var bundle = Bundle()
                var custom = Custom()
                if (contact.customFields!!.size > 1) {
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
                isInitList = 0
                Toast.makeText(this, "Saved Successful!", Toast.LENGTH_SHORT).show()
                readData()
            }
        }
    }

//    override fun onRefresh() {
//        Toast.makeText(this, "dnag refresh ", Toast.LENGTH_SHORT).show()
//        sr_rvContac.isRefreshing = false
//    }
}