package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Recipe> recipeListX = new ArrayList<>();

    private FirstAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        adapter = new FirstAdapter(Arrays.asList(RecipeType.values()));//Get a list of questions for the adaptater
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/recipes");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeListX = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    Recipe recipe = data.getValue(Recipe.class);
                    recipeListX.add(recipe);
                }
                Log.d("TEST", "Value is: " + recipeListX);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TEST", "Failed to read value.", error.toException());
            }
        });


        List<Recipe> recipeList = JsonDecoder.getRecipesFromJson(this);

        adapter = new FirstAdapter(recipeList);//Get a list of questions for the adaptater
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

