package com.example.testretrofit2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.testretrofit2.databinding.ActivityDetailBinding
import com.example.testretrofit2.models.Contact
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail)
        var intent = intent
        var bundle = intent.extras
        if( bundle != null ){
            var contact = bundle.getSerializable("CONTACT") as Contact
            binding.contact = contact
        }
        btn_BackDetail.setOnClickListener(){
            finish()
        }
    }
}