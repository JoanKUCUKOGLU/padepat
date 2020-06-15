package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
    LinearLayout commentsLayout;
    TextInputEditText addCommentText;

    @SuppressLint("ClickableViewAccessibility")
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
        commentsLayout = findViewById(R.id.commentLayout);
        addCommentText = findViewById(R.id.addCommentTextView);

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

        addCommentText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (addCommentText.getRight() - addCommentText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        addComment();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void addComment() {

        hideKeyboard(RecipeActivity.this);
        LinearLayout newLinearLayout = new LinearLayout(RecipeActivity.this);
        newLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        TextView userTextView = new TextView(RecipeActivity.this);
        userTextView.setTextSize(18);
        userTextView.setText("[USER NAME]");
        userTextView.setTypeface(null,Typeface.BOLD);
        params.setMargins(0, 5,0,5);
        userTextView.setLayoutParams(params);

        TextView newTextView = new TextView(RecipeActivity.this);
        newTextView.setTextSize(18);
        newTextView.setText(addCommentText.getText().toString());
        newTextView.setLayoutParams(params);

        newLinearLayout.addView(userTextView);
        newLinearLayout.addView(newTextView);

        View separator = new View(RecipeActivity.this);
        separator.setBackgroundColor(Color.parseColor("#E6D1A1"));
        LinearLayout.LayoutParams separatorLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
        separatorLayout.setMargins(0,5,0,5);
        separator.setLayoutParams(separatorLayout);
        newLinearLayout.addView(separator);

        addCommentText.setText(null);
        addCommentText.clearFocus();
        addCommentText.setCursorVisible(false);

        params.setMargins(20, 10,20,10);
        newLinearLayout.setLayoutParams(params);

        commentsLayout.addView(newLinearLayout);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

