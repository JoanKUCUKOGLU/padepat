package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private Recipe recipe;
    private List<Like> likeList;
    private Integer nbLikes = 0;

    private TextView nbLikesTextView;
    private Button buttonLike;
    private Button buttonDislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        recipe = intent.getParcelableExtra("recipe");

        ImageView recipeImg = findViewById(R.id.recipeImg);

        nbLikesTextView = findViewById(R.id.likeNbText);
        buttonLike = findViewById(R.id.buttonLike);
        buttonDislike = findViewById(R.id.buttonDislike);

        Picasso.get().load(recipe.getImg()).fit().centerCrop().error(R.drawable.logopadepat).into(recipeImg);

        TextView recipeNameText = findViewById(R.id.recipeName);
        recipeNameText.setText(recipe.getName());

        TextView recipePriceText = findViewById(R.id.recipePrice);
        recipePriceText.setText("~ "+recipe.getPrice() + " €");

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.setMargins(0, 0, 0, 30);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 50);

        LinearLayout ingredientsLinearLayout = (LinearLayout)findViewById(R.id.ingredientsLayout);
        for(String ingredient : recipe.getIngredients()){
            TextView ingredientText = new TextView(this);
            ingredientText.setText(ingredient);
            ingredientText.setTextSize(18);
            ingredientText.setLayoutParams(textParams);
            ingredientsLinearLayout.addView(ingredientText);
        }
        ingredientsLinearLayout.setLayoutParams(layoutParams);


        LinearLayout stepsLinearLayout = (LinearLayout)findViewById(R.id.stepsLayout);
        for(String step : recipe.getSteps()){
            TextView stepText = new TextView(this);
            stepText.setText(step);
            stepText.setTextSize(18);
            stepText.setLayoutParams(textParams);
            stepsLinearLayout.addView(stepText);
        }

        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/likes");

        // Read updates from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likeList = new ArrayList<>();
                nbLikes = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Like like = data.getValue(Like.class);
                    likeList.add(like);
                    if(like.getDeviceid().equals(device_id) && like.getRecipeid().equals(recipe.getId())) {
                        nbLikes += like.getType().equals(LikeType.Like.stringType) ? 1 : -1;
                        if(nbLikes < 0) { nbLikes = 0; }
                    }
                }
                Log.d("TEST", "Value is: " + nbLikes);
                nbLikesTextView.setText(String.valueOf(nbLikes));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TEST", "Failed to read value.", error.toException());
            }
        });
    }

    public enum LikeType {
        Like("like"),
        Dislike("dislike");

        public String stringType;
        LikeType(String type) {
            this.stringType = type;
        }
    }
}

