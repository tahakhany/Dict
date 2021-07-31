package com.taha.dict;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private MyRecyclerViewAdapter.ItemClickListener itemClickListener;
    private MyRecyclerViewAdapter adapter;
    private ArrayList<Dictionary> mFoundWords;
    private ArrayList<Dictionary> mDictionary;
    private Intent switchActivityToDictionary;
    private Intent switchActivityToSettings;
    private TextView mSearchBox;
    private TextView mSortedByTextView;
    private ImageButton mSettingsButton;
    private ImageButton mSortByButton;
    private RecyclerView recyclerView;
    private Toolbar mActionBarToolbar;


    public static final String WORD = "word";
    public static final String DEFINITION = "definition";
    public static final String USED_IN = "used_in";
    public static Context APPLICATION_CONTEXT ;
    private final boolean ASCENDING = true;
    private final boolean DESCENDING = false;

    public static final String FILE_NAME = "dictionary_save_file";

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBarToolbar();
        setContentView(R.layout.activity_main);
        APPLICATION_CONTEXT = getApplicationContext();

        //initialising the views
        mSearchBox = findViewById(R.id.txt_search_box);
        recyclerView = findViewById(R.id.rcy_list);
        mSettingsButton = findViewById(R.id.btn_settings);
        mSortByButton = findViewById(R.id.btn_sort_by);
        mSortedByTextView = findViewById(R.id.txt_sorted_by);

        //intents needed for activity switching
        switchActivityToDictionary = new Intent(this, Definition.class);
        switchActivityToSettings = new Intent(this, Settings.class);

        //gets database from the WordDatabase class
        getDatabase();
        sortBy(WORD, ASCENDING);


        //listener for any change or pressing enter in the search view
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mFoundWords = updateFoundWords(mSearchBox.getText().toString(), mDictionary);
                updateFoundList(mFoundWords, recyclerView);
            }
        });

        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                mFoundWords = updateFoundWords(textView.getText().toString(), mDictionary);
                updateFoundList(mFoundWords, recyclerView);
                return true;
            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(switchActivityToSettings);
            }
        });

        View.OnClickListener sortListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_sort_by_word_ascending:
                                sortBy(WORD, ASCENDING);
                                updateFoundList(adapter.getArrayList(), recyclerView);
                                break;
                            case R.id.menu_sort_by_word_descending:
                                sortBy(WORD, DESCENDING);
                                updateFoundList(adapter.getArrayList(), recyclerView);
                                break;
                            case R.id.menu_sort_by_used_in_ascending:
                                sortBy(USED_IN,ASCENDING);
                                updateFoundList(adapter.getArrayList(), recyclerView);
                                break;
                            case R.id.menu_sort_by_used_in_descending:
                                sortBy(USED_IN, DESCENDING);
                                updateFoundList(adapter.getArrayList(), recyclerView);
                                break;
                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.menu);
                popup.show();
            }
        };

        mSortByButton.setOnClickListener(sortListener);
        mSortedByTextView.setOnClickListener(sortListener);

        //initialising the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new MyRecyclerViewAdapter(this, mDictionary);

        itemClickListener = new MyRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(WORD, mFoundWords.get(position).getWord());
                bundle.putString(DEFINITION, mFoundWords.get(position).getDefinition());
                bundle.putString(USED_IN, mFoundWords.get(position).getUsedIn());
                switchActivityToDictionary.putExtras(bundle);
                startActivity(switchActivityToDictionary);
            }
        };
        adapter.setClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);
    }

    //loads database from WordDatabase class
    private void getDatabase() {
        mDictionary = new ArrayList<>();
        Dictionary[] tempArrayDatabase = WordDatabase.getDictionary();
        Collections.addAll(mDictionary, tempArrayDatabase);
        mFoundWords = mDictionary;
    }

    //this method searches the array list and returns the quarry's answer.
    private ArrayList<Dictionary> updateFoundWords(String searchText, ArrayList<Dictionary> dictionary) {

        ArrayList<Dictionary> mFoundWords = new ArrayList<>();
        ArrayList<Integer> foundWordIndex = new ArrayList<>();

        if (searchText.equals("")) {
            return dictionary;
        } else {
            for (int i = 0; i < dictionary.size(); i++) {
                if (dictionary.get(i).getWord().startsWith(searchText)) {
                    mFoundWords.add(dictionary.get(i));
                    foundWordIndex.add(i);
                }
            }
            for (int i = 0; i < dictionary.size(); i++) {
                if (!foundWordIndex.contains(i) && dictionary.get(i).getWord().contains(searchText)) {
                    mFoundWords.add(dictionary.get(i));
                }
            }
            return mFoundWords;
        }
    }

    //this method updates the recycler view with the provided arraylist.
    private void updateFoundList(ArrayList<Dictionary> foundWordList, RecyclerView recyclerView) {
        adapter = new MyRecyclerViewAdapter(this, foundWordList);
        adapter.setClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);
    }

    private void sortBy(String sortBy, boolean direction) {
        if(direction == ASCENDING){
            for (int i = 0; i < mDictionary.size(); i++) {
                for (int j = i + 1; j < mDictionary.size(); j++) {
                    switch (sortBy) {
                        case WORD:
                            mSortedByTextView.setText(getText(R.string.sorted_by_word_ascending));

                            if (mDictionary.get(i).getWord().compareTo(mDictionary.get(j).getWord()) > 0) {
                                Collections.swap(mDictionary, i, j);

                            }
                            break;

                        case USED_IN:
                            mSortedByTextView.setText(getText(R.string.sorted_by_usage_ascending));

                            if (mDictionary.get(i).getUsedIn().compareTo(mDictionary.get(j).getUsedIn()) > 0) {
                                Collections.swap(mDictionary, i, j);

                            }

                            break;
                    }
                }
            }
        } else {
            for (int i = 0; i < mDictionary.size(); i++) {
                for (int j = i + 1; j < mDictionary.size(); j++) {
                    switch (sortBy) {
                        case WORD:
                            mSortedByTextView.setText(getText(R.string.sorted_by_word_descending));

                            if (mDictionary.get(i).getWord().compareTo(mDictionary.get(j).getWord()) < 0) {
                                Collections.swap(mDictionary, i, j);
                            }
                            break;

                        case USED_IN:
                            mSortedByTextView.setText(getText(R.string.sorted_by_usage_descending));

                            if (mDictionary.get(i).getUsedIn().compareTo(mDictionary.get(j).getUsedIn()) < 0) {
                                Collections.swap(mDictionary, i, j);
                            }
                            break;
                    }
                }
            }
        }
    }

    public void saveArrayList(ArrayList<Dictionary> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<Dictionary> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<Dictionary>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /*protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }
}

