package com.samoylenko.homework521;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Bundle arguments = getIntent().getExtras();
        String name = arguments.get("login").toString();

        TextView txtResult = findViewById(R.id.text_result);
        txtResult.setText(String.format(getString(R.string.welcome), name));

    }
}
