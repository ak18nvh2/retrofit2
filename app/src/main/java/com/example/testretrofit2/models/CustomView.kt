package com.example.testretrofit2.models

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.example.testretrofit2.R
import kotlinx.android.synthetic.main.activity_create_update_employee.view.*

class CustomView : AppCompatEditText {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr)
    val validateEmail: String = "[a-zA-Z0-9]+@[a-z]+\\.[a-z]+"
    var mImage : Drawable? = null
    init {

         mImage = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_check_24, null
        )
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int, count: Int, after: Int
            ) {
                // Do nothing.
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int, before: Int, count: Int
            ) {
            }


            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            override fun afterTextChanged(s: Editable) {
                if (s.matches(validateEmail.toRegex())) {
                    showImage()
                }
                else{
                    hideImage(this@CustomView)
                }
            }
        })
    }
    // mở hoặc hộ cái
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun showImage(){
        // cái này là set vị trí hiện ảnh, cái t để mImage là vị trí bên phải đấy
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, mImage, null
        )
        edt_InputEmail.setTextColor(Color.BLACK)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun hideImage(view : View){
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, null, null
        )
        edt_InputEmail.setTextColor(Color.RED)
    }
}


