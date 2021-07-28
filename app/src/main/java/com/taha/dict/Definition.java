package com.taha.dict;

import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

public class Definition extends AppCompatActivity {

    private TextView mChosenWord;
    private TextView mUsedIn;
    private TextView mDefinition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.definition_page);

        mChosenWord = findViewById(R.id.txt_chosen_word);
        mDefinition = findViewById(R.id.txt_definition);
        mUsedIn = findViewById(R.id.txt_used_in);

        Bundle bundle = getIntent().getExtras();

        mChosenWord.setText(bundle.getString(MainActivity.WORD));
        mDefinition.setText(bundle.getString(MainActivity.DEFINITION));
        mUsedIn.setText(bundle.getString(MainActivity.USED_IN));
    }
}