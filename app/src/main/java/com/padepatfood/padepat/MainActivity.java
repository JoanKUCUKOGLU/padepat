package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirstAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        adapter = new FirstAdapter(Arrays.asList(RecipeType.values()));//Get a list of questions for the adaptater
        recyclerView = findViewById(R.id.firstRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public enum RecipeType {
        Vege("végétarien"),
        Classic("classique");

        public String stringType;
        RecipeType(String type) {
            this.stringType = type;
        }
    }
}

