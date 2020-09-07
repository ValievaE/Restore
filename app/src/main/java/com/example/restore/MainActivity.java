package com.example.restore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    public static final String PREFS = "large_text";
    public static final String PREFS_TEXT = "text";
    public static final String PREFS_TEXT_LENGTH = "leng";
    private List<Map<String, String>> content;
    private ListView listView;
    private TextView textViewTitle;
    private TextView textViewSubtitle;
    private String noteTxt = "";
    private String noteTxtLength = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleAdapter adapter;
    private String[] arrayContent;
    private ArrayList<Integer> removedItems;
    private static final String key = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        savePrefs();
        fillContent();

        String[] from = new String[]{"title", "subtitle"};
        int[] to = new int[]{R.id.textViewTitle, R.id.textViewSubtitle};
        adapter = new SimpleAdapter(this, content, R.layout.list_item, from, to);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                content.remove(position);
                adapter.notifyDataSetChanged();
                removedItems.add(position);
            }
        });



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillContent();
                getDateFromSharedPref();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });



    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(key, removedItems);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Integer> list = savedInstanceState.getIntegerArrayList(key);

        for (int i = 0; i < list.toArray().length; i++) {
            content.remove(i);

        }
        adapter.notifyDataSetChanged();
    }

    private void init() {
        listView = findViewById(R.id.listView);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewSubtitle = findViewById(R.id.textViewSubtitle);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        arrayContent = getString(R.string.large_text).split("\\n\\n");
        removedItems = new ArrayList<>();
    }

    private void savePrefs() {
        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor myEditor = sharedPreferences.edit();
        for (int i = 0; i < arrayContent.length; i++) {
            noteTxt = arrayContent[i];
            noteTxtLength = String.valueOf(arrayContent[i].length());
        }

        myEditor.putString(PREFS_TEXT, noteTxt);
        myEditor.putString(PREFS_TEXT_LENGTH, noteTxtLength);
        myEditor.apply();
    }


    private void fillContent() {
        content = new ArrayList<>();
        Map<String, String> map;
        for (int i = 0; i < arrayContent.length; i++) {
            map = new HashMap<>();
            map.put("title", arrayContent[i]);
            map.put("subtitle", String.valueOf(arrayContent[i].length()));
            content.add(map);
        }


    }

    private void getDateFromSharedPref() {

        noteTxt = sharedPreferences.getString(PREFS_TEXT, "");
        textViewTitle.setText(noteTxt);
        noteTxtLength = sharedPreferences.getString(PREFS_TEXT_LENGTH, "");
        textViewSubtitle.setText(noteTxtLength);
    }
}