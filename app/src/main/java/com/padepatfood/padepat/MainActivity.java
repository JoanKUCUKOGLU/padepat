package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Recipe> recipeList = new ArrayList<>();

    private FirstAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();


        //List<Recipe> recipeList = JsonDecoder.getRecipesFromJson(this);

        recipeList = getIntent().getParcelableArrayListExtra("recipeList");

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

