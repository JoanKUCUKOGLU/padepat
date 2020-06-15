package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class RecipeActivity extends AppCompatActivity {

    private Recipe recipe;
    private List<Like> totalLikeList;
    private List<Like> myLikeList;
    private Integer nbLikes = 0;

    private String currentDeviceId;

    private ProgressBar likeProgressBar;
    private Button buttonLike;
    private Button buttonDislike;

    FirebaseDatabase database;
    DatabaseReference likeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        recipe = intent.getParcelableExtra("recipe");

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        ImageView recipeImg = findViewById(R.id.recipeImg);

        currentDeviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        likeProgressBar = findViewById(R.id.likeProgressBar);
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

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLike(LikeType.Like);
            }
        });

        buttonDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLike(LikeType.Dislike);
            }
        });

        // DATABASE
        database = FirebaseDatabase.getInstance();
        likeRef = database.getReference("/likes");

        // Read updates from the database
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalLikeList = new ArrayList<>();
                myLikeList = new ArrayList<>();
                nbLikes = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Like like = data.getValue(Like.class);
                    totalLikeList.add(like);
                    if(like.getRecipeid().equals(recipe.getId())) {
                        myLikeList.add(like);
                    }
                }
                Log.d("TEST", "Value is: " + nbLikes);
                buttonLike.setText(String.valueOf(myLikeList.stream().filter(like -> like.getType().equals(LikeType.Like.stringType)).count()));
                buttonDislike.setText(String.valueOf(myLikeList.stream().filter(like -> like.getType().equals(LikeType.Dislike.stringType)).count()));
                if(myLikeList.stream().filter(like -> !like.getType().equals(LikeType.None.stringType)).count() > 0) {
                    likeProgressBar.setMax((int) myLikeList.stream().filter(like -> !like.getType().equals(LikeType.None.stringType)).count());
                    likeProgressBar.setProgress((int) myLikeList.stream().filter(like -> like.getType().equals(LikeType.Like.stringType)).count());
                } else {
                    likeProgressBar.setMax(2);
                    likeProgressBar.setProgress(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TEST", "Failed to read value.", error.toException());
            }
        });
    }

    private void updateLike(LikeType type) {
        Like currentLike = new Like();
        // New line in DB
        if(myLikeList.stream().filter(like -> like.getDeviceid().equals(currentDeviceId)).count() == 0) {
            currentLike = new Like(totalLikeList.size() > 0 ? totalLikeList.get(totalLikeList.size()-1).getLikeid() + 1 : 0,
                    currentDeviceId, recipe.getId(), type.stringType);
        } else { // Update existing line
            currentLike = myLikeList.stream().filter(like -> like.getDeviceid().equals(currentDeviceId)).collect(Collectors.toList()).get(0);
            currentLike.setType(currentLike.getType().equals(type.stringType) ? LikeType.None.stringType : type.stringType);
        }
        likeRef.child(String.valueOf(currentLike.getLikeid())).setValue(currentLike);
    }

    public enum LikeType {
        Like("like"),
        Dislike("dislike"),
        None("none");

        public String stringType;
        LikeType(String type) {
            this.stringType = type;
        }
    }
}

