package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.Fade;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeActivity extends AppCompatActivity {

    GlobalData g;

    private Recipe recipe;
    private List<Like> totalLikeList;
    private List<Like> myLikeList;
    private Integer nbLikes = 0;

    private ProgressBar likeProgressBar;
    private Button buttonLike;
    private Button buttonDislike;

    private FirebaseDatabase database;
    private DatabaseReference likeRef;
    private LinearLayout commentsLayout;
    private TextInputEditText addCommentText;

    private LinearLayout navBar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        getSupportActionBar().hide();

        g = GlobalData.getInstance();

        Intent intent = getIntent();
        recipe = intent.getParcelableExtra("recipe");

        setTransition();
        findViews();
        setPage();
        manageLikesDisplay();

        NavBarGestion.manage("NONE","NONE",RecipeActivity.this);
    }

    //Crée la transition pour l'activity
    private void setTransition() {
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }

    //Regroupe tous les findViewById
    private void findViews(){
        likeProgressBar = findViewById(R.id.likeProgressBar);
        buttonLike = findViewById(R.id.buttonLike);
        buttonDislike = findViewById(R.id.buttonDislike);
        commentsLayout = findViewById(R.id.commentLayout);
        addCommentText = findViewById(R.id.addCommentTextView);
    }

    //Remplis les view avec les datas correspondantes
    @SuppressLint("ClickableViewAccessibility")
    private void setPage(){
        ImageView recipeImg = findViewById(R.id.recipeImg);
        Picasso.get().load(recipe.getImg()).fit().centerCrop().error(R.drawable.logopadepat).into(recipeImg);

        TextView recipeNameText = findViewById(R.id.recipeName);
        recipeNameText.setText(recipe.getName());

        TextView recipePriceText = findViewById(R.id.recipePrice);
        recipePriceText.setText("~ "+recipe.getPrice() + " €");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout ingredientsLinearLayout = (LinearLayout)findViewById(R.id.ingredientsLayout);
        putDataInLinearLayout(ingredientsLinearLayout,recipe.getIngredients());

        LinearLayout stepsLinearLayout = (LinearLayout)findViewById(R.id.stepsLayout);
        putDataInLinearLayout(stepsLinearLayout,recipe.getSteps());

        params.bottomMargin = 50;
        ingredientsLinearLayout.setLayoutParams(params);

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

    //Gere l'affichage les likes dans la progress bar et dans les boutons
    private void manageLikesDisplay(){
        // DATABASE
        database = FirebaseDatabase.getInstance();
        likeRef = database.getReference("/likes");

        TextView likeDislikeDisplayNumber = findViewById(R.id.likeNumberDisplay);
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
                Log.d("TEST", getString(R.string.value).toString() + nbLikes);

                long likes = myLikeList.stream().filter(like -> like.getType().equals(LikeType.Like.stringType)).count();
                long disLikes = myLikeList.stream().filter(like -> like.getType().equals(LikeType.Dislike.stringType)).count();
                buttonLike.setText(String.valueOf(likes));
                buttonDislike.setText(String.valueOf(disLikes));
                if(myLikeList.stream().filter(like -> !like.getType().equals(LikeType.None.stringType)).count() > 0) {
                    likeProgressBar.setMax((int) myLikeList.stream().filter(like -> !like.getType().equals(LikeType.None.stringType)).count());
                    likeProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D81414")));
                    likeProgressBar.setProgress((int) likes);
                    String likeDislikeText = likes +" likes - "+ disLikes +" dislikes";
                    likeDislikeDisplayNumber.setText(likeDislikeText);
                    likeDislikeDisplayNumber.setVisibility(View.VISIBLE);
                } else {
                    likeProgressBar.setMax(0);
                    likeProgressBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                    likeDislikeDisplayNumber.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TEST", getString(R.string.read_failed), error.toException());
            }
        });
    }

    //Remplit les linearLayout avec les items de la liste passée en paramètre
    private void putDataInLinearLayout(LinearLayout linearLayout,List<String> stringList){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 30;

        for(String text : stringList){
            TextView newTextView = new TextView(this);
            newTextView.setText(text);
            newTextView.setTextSize(18);
            newTextView.setLayoutParams(params);
            linearLayout.addView(newTextView);
        }
    }

    //Ajoute un commentaire
    private void addComment() {

        hideKeyboard(RecipeActivity.this);
        commentsLayout.addView(generateCommentLayoutChild());
        addCommentText.setText(null);
        addCommentText.clearFocus();
        addCommentText.setCursorVisible(false);
    }

    //Genere la zone ou apparaissent les actions liées au commentaire courant
    private LinearLayout generateCommentActionLayout() {
        LinearLayout.LayoutParams actionLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionLayout.setMargins(5,5,20,5);
        LinearLayout actionLinearLayout = new LinearLayout(RecipeActivity.this);
        actionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView editImg = new ImageView(RecipeActivity.this);
        editImg.setImageResource(R.drawable.ic_baseline_edit_24);
        editImg.setLayoutParams(actionLayout);
        editImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                LinearLayout parentLinearLayout = (LinearLayout) editImg.getParent().getParent();
                TextView text = (TextView)parentLinearLayout.getChildAt(1);

                TextInputEditText editText = new TextInputEditText(RecipeActivity.this);
                editText.setHint(getString(R.string.comment_edit));
                editText.setLayoutParams(text.getLayoutParams());
                editText.setText(text.getText());
                editText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_baseline_send_35,0);
                editText.setFocusable(true);
                editText.requestFocus();
                parentLinearLayout.removeView(text);
                parentLinearLayout.addView(editText,1);

                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int DRAWABLE_LEFT = 0;
                        final int DRAWABLE_TOP = 1;
                        final int DRAWABLE_RIGHT = 2;
                        final int DRAWABLE_BOTTOM = 3;

                        if(event.getAction() == MotionEvent.ACTION_UP) {
                            if(event.getRawX() >= (addCommentText.getRight() - addCommentText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                hideKeyboard(RecipeActivity.this);
                                text.setText(editText.getText().toString());
                                editText.clearFocus();
                                editText.setCursorVisible(false);
                                parentLinearLayout.removeView(editText);
                                parentLinearLayout.addView(text,1);
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        });
        ImageView deleteImg = new ImageView(RecipeActivity.this);
        deleteImg.setImageResource(R.drawable.ic_baseline_delete_24);
        deleteImg.setLayoutParams(actionLayout);
        deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout parentLinearLayout = (LinearLayout) deleteImg.getParent().getParent();
                commentsLayout.removeView(parentLinearLayout);
            }
        });
        actionLinearLayout.addView(editImg);
        actionLinearLayout.addView(deleteImg);
        return actionLinearLayout;
    }

    //Genere la zone ou apparait le commentaire courant
    private LinearLayout generateCommentLayoutChild(){
        LinearLayout newLinearLayout = new LinearLayout(RecipeActivity.this);
        newLinearLayout.setOrientation(LinearLayout.VERTICAL);
        int textSize = 18;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView userTextView = new TextView(RecipeActivity.this);
        userTextView.setTextSize(textSize);
        userTextView.setText("[USER NAME]");
        userTextView.setTypeface(null,Typeface.BOLD);
        params.setMargins(0, 5,0,5);
        userTextView.setLayoutParams(params);

        TextView newTextView = new TextView(RecipeActivity.this);
        newTextView.setTextSize(textSize);
        newTextView.setText(addCommentText.getText().toString());
        newTextView.setLayoutParams(params);

        newLinearLayout.addView(userTextView);
        newLinearLayout.addView(newTextView);

        newLinearLayout.addView(generateCommentActionLayout());

        View separator = new View(RecipeActivity.this);
        separator.setBackgroundColor(Color.parseColor("#E6D1A1"));
        LinearLayout.LayoutParams separatorLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);
        separatorLayout.setMargins(0,5,0,5);
        separator.setLayoutParams(separatorLayout);
        newLinearLayout.addView(separator);


        params.setMargins(20, 10,20,10);
        newLinearLayout.setLayoutParams(params);

        return newLinearLayout;
    }

    //Gere la mise a jour des likes
    private void updateLike(LikeType type) {
        Like currentLike;
        if(g.isUserLogged()) {
            // New line in DB
            if(myLikeList.stream().filter(like -> like.getUserid().equals(g.getCurrentUser().getId())).count() == 0) {
                currentLike = new Like(totalLikeList.size() > 0 ? totalLikeList.get(totalLikeList.size()-1).getLikeid() + 1 : 0,
                        g.getCurrentUser().getId(), recipe.getId(), type.stringType);
            } else { // Update existing line
                currentLike = myLikeList.stream().filter(like -> like.getUserid().equals(g.getCurrentUser().getId())).collect(Collectors.toList()).get(0);
                currentLike.setType(currentLike.getType().equals(type.stringType) ? LikeType.None.stringType : type.stringType);
            }
            likeRef.child(String.valueOf(currentLike.getLikeid())).setValue(currentLike);
        } else {
            // TODO : Ajouter popup pour demander à l'utilisateur s'il veut se connecter pour liker et renvoyer sur LoginRegisterActivity
        }
    }

    //Permet de forcer la fermeture du clavier
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

    //Gestion de la navBar
    public void navBarGestion(){
        navBar = findViewById(R.id.navBarLayout);
        ImageView favButton = (ImageView)navBar.getChildAt(1);
        ImageView homeButton = (ImageView)navBar.getChildAt(0);
        boolean isConnected= true;
        homeButton.setOnClickListener(new View.OnClickListener() {
            Intent newActivity;
            @Override
            public void onClick(View v) {
                newActivity = new Intent(RecipeActivity.this, MainActivity.class);
                startActivity(newActivity);
            }
        });
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

