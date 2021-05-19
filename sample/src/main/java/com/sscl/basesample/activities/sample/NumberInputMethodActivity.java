package com.sscl.basesample.activities.sample;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sscl.baselibrary.utils.Tool;
import com.sscl.baselibrary.widget.NumberInputMethodView;
import com.sscl.basesample.R;

/**
 * @author jackie
 */
public class NumberInputMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_input_metohd);

        EditText editText = findViewById(R.id.test_et);
        NumberInputMethodView numberInputMethodView = findViewById(R.id.number_input_method_view);
        Tool.setInpType(this, editText, InputType.TYPE_CLASS_NUMBER);
        numberInputMethodView.attach(this, editText);
    }
}