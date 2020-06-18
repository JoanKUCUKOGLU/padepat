package com.padepatfood.padepat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    GlobalData g;

    List<Recipe> recipeList;
    List<String> categoryList;
    List<User> userList;
    Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Hide ActionBar
        getSupportActionBar().hide();

        g = GlobalData.getInstance();
        g.setContext(this);

        // Check if device is connected to internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else {
            connected = false;
        }

        g.setConnected(connected);

        if (connected) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            // Read from the database
            DatabaseReference recipeRef = database.getReference("/recipes");
            recipeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    recipeList = new ArrayList<>();
                    categoryList = new ArrayList<>();

                    saveDataInInternalStorage(dataSnapshot);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Recipe recipe = data.getValue(Recipe.class);
                        recipeList.add(recipe);

                        saveImgInInternalStorage(recipe.getId(), recipe.getImg());

                        // save category list
                        if (!categoryList.contains(recipe.getType())) {
                            categoryList.add(recipe.getType());
                        }
                    }
                    Log.d("TEST", getString(R.string.value) + recipeList);
                    g.setRecipeList(recipeList);
                    g.setCategoryList(categoryList);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TEST", getString(R.string.read_failed), error.toException());
                }
            });

            DatabaseReference userRef = database.getReference("/users");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userList = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        userList.add(user);
                    }
                    g.setUserList(userList);
                    Log.d("TEST", getString(R.string.value) + recipeList);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TEST", getString(R.string.read_failed), error.toException());
                }
            });

            loading();
        } else {
            recipeList = JSONDecoder.getRecipesFromJson(this);

            if(recipeList != null) {
                categoryList = new ArrayList<>();
                for(Recipe recipe : recipeList) {
                    if (!categoryList.contains(recipe.getType())) {
                        categoryList.add(recipe.getType());
                    }
                }
                g.setRecipeList(recipeList);
                g.setCategoryList(categoryList);
                loading();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(StartActivity.this);
                alert.setTitle("Error");
                alert.setNegativeButton("OK", (dialog, which) -> finish());
                alert.setIcon(android.R.drawable.ic_dialog_alert);
                TextView text = new TextView(StartActivity.this);
                text.setText("This app needs an Internet connection for the first connection");
                text.setGravity(Gravity.CENTER);
                alert.setView(text);
                alert.show();
            }
        }
    }

    private void saveDataInInternalStorage(DataSnapshot dataSnapshot) {
        // save all recipes in json for offline mode
        Gson gson = new Gson();
        String s1 = gson.toJson(dataSnapshot.getValue());

        try {
            File dir = getFilesDir();
            File file = new File(dir, "recettes.json");
            boolean deleted = file.delete();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("recettes.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(s1);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveImgInInternalStorage(Integer id, String src) {
        DownloadImgFromURL difu = new DownloadImgFromURL(this, id, src);
        difu.execute();
    }

    public void loading() {
        // Load MainPage
        Thread loadingThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(3000);  //Delay of 3 seconds
                } catch (Exception e) {
                } finally {
                    Intent i = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        loadingThread.start();
    }
}
