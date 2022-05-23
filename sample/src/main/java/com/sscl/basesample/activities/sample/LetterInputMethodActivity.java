package com.sscl.basesample.activities.sample;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sscl.baselibrary.utils.Tool;
import com.sscl.baselibrary.widget.LetterInputMethodView;
import com.sscl.basesample.R;

/**
 * @author jackie
 */
public class LetterInputMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_sscl_basesample_activity_letter_input_method);

        EditText editText = findViewById(R.id.test_et);
        LetterInputMethodView letterInputMethodView = findViewById(R.id.letter_input_method_view);
        Tool.setInpType(this, editText, InputType.TYPE_CLASS_TEXT);
        letterInputMethodView.attach(this);
    }
}