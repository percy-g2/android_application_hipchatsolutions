package com.androidevlinux.percy.hipchatsolutions;

import android.app.Activity;
import android.hardware.input.InputManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        textView = (TextView)findViewById(R.id.outputtextview);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
                case R.id.button:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    textView.setText(editText.getText().toString());
                break;
                default:
                break;
        }
    }
}
