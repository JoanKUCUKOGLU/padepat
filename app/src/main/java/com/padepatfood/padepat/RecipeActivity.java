package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Intent intent = getIntent();
        Recipe recipe = intent.getParcelableExtra("recipe");

        ImageView recipeImg = findViewById(R.id.recipeImg);
        TextView recipeTxt = findViewById(R.id.recipeText);

        Picasso.get().load(recipe.getImg()).fit().centerCrop().error(R.drawable.logopadepat).into(recipeImg);
        recipeTxt.setText(recipe.getSteps());
    }
}
