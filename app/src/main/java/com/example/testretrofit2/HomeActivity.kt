package com.example.testretrofit2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.testretrofit2.models.Contact
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_select.*

class HomeActivity : AppCompatActivity(), View.OnClickListener,
    ContactAdapter.IRecyclerViewWithHomeActivity {
    private var list: ArrayList<Contact>? = ArrayList<Contact>()
    private val REQUEST_HOME_TO_CREATE_OR_UPDATE = 10
    var contactAdapter: ContactAdapter? = null
    var fJsonViewModel: FJsonViewModel = FJsonViewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var recyclerView: RecyclerView = findViewById(R.id.rv_Contact)
        contactAdapter = ContactAdapter(this, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        list?.let { contactAdapter?.setList(it) }
        recyclerView.adapter = contactAdapter

        fJsonViewModel =
            ViewModelProviders.of(this).get<FJsonViewModel>(FJsonViewModel::class.java)
        fJsonViewModel.readFJson(this)
        registerLiveDataListener()

       // sr_rvContac.setOnRefreshListener(this)

        btn_ReloadData.setOnClickListener(this)
        btn_CreateNewContact.setOnClickListener(this)
        btn_sortACSByFirstName.setOnClickListener(this)
        btn_sortDESCByFirstName.setOnClickListener(this)



    }

    private fun registerLiveDataListener(){
        val fJsonObserver = Observer<List<Contact>> { newListContact -> contactAdapter?.setList(newListContact as ArrayList<Contact>) }
        fJsonViewModel.listContact.observe(this, fJsonObserver)
    }

    override fun onClick(v: View?) {
        when (v) {
            btn_CreateNewContact -> {
                var intent = Intent(this, CreateAndUpdateEmployeeActivity::class.java)
                intent.putExtra("BUTTON", 2)
                var bundle = Bundle()
                bundle.putSerializable("CONTACT_LIST", this.list)
                intent.putExtras(bundle)
                startActivityForResult(intent, REQUEST_HOME_TO_CREATE_OR_UPDATE)
            }
            btn_ReloadData -> {
                fJsonViewModel.readFJson(this)
            }
            btn_sortACSByFirstName -> {
                fJsonViewModel.sortASCByFirstName()
            }
            btn_sortDESCByFirstName -> {
                fJsonViewModel.sortDESCByFirstName()
            }
        }
    }

    private fun readData() {
        fJsonViewModel.readFJson(this)
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
           fJsonViewModel.deleteContact(this,contact)
        }
        dialogSelect.btn_Change.setOnClickListener() {
            var intent = Intent(this, CreateAndUpdateEmployeeActivity::class.java)
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
                readData()
            }
        }
    }

//    override fun onRefresh() {
//        Toast.makeText(this, "dnag refresh ", Toast.LENGTH_SHORT).show()
//        sr_rvContac.isRefreshing = false
//    }
}