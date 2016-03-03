package com.cs160.joleary.catnip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class ZipEntry extends Activity {

    public static final String ZIPCODE_KEY = "zipcode";

    private Button submitButton;
    private Button backButton;
    private EditText zipCodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_entry);

        submitButton = (Button) findViewById(R.id.submitButton);
        backButton = (Button) findViewById(R.id.backButton);
        zipCodeEditText = (EditText) findViewById(R.id.zipCodeEditText);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(homeIntent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Pattern.matches("[0-9]+", zipCodeEditText.getText()) && (zipCodeEditText.getText().length() == 5)) {
                    Intent infoIntent = new Intent(getBaseContext(), InfoPanel.class);
                    infoIntent.putExtra(ZIPCODE_KEY, Integer.parseInt(zipCodeEditText.getText().toString()));
                    startActivity(infoIntent);
                } else {
                    Toast.makeText(ZipEntry.this, "Invalid zip code entered.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
