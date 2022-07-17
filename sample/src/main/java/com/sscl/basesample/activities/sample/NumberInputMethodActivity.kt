package com.sscl.basesample.activities.sample

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.sscl.baselibrary.utils.Tool
import com.sscl.baselibrary.widget.NumberInputMethodView
import com.sscl.basesample.R

/**
 * @author jackie
 */
class NumberInputMethodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.com_sscl_basesample_activity_number_input_metohd)
        val editText: EditText = findViewById(R.id.test_et)
        val numberInputMethodView: NumberInputMethodView =
            findViewById(R.id.number_input_method_view)
        Tool.setInpType(this, editText, InputType.TYPE_CLASS_NUMBER)
        numberInputMethodView.attach(this, editText)
    }
}