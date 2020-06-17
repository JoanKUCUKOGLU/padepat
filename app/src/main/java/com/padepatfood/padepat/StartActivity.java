package com.padepatfood.padepat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    GlobalData g;

    List<Recipe> recipeList;
    List<String> categoryList;
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
            AlertDialog.Builder alert = new AlertDialog.Builder(StartActivity.this);
            alert.setTitle("Error");
            alert.setNegativeButton("OK", null);
            alert.setIcon(android.R.drawable.ic_dialog_alert);
            TextView text = new TextView(StartActivity.this);
            text.setText(getString(R.string.internet_needed).toString());
            text.setGravity(Gravity.CENTER);
            alert.setView(text);
            alert.show();
        }

        g.setConnected(connected);

        if (connected) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/recipes");

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
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
        } else {
            recipeList = JSONDecoder.getRecipesFromJson(this);

            categoryList = new ArrayList<>();
            for(Recipe recipe : recipeList) {
                if (!categoryList.contains(recipe.getType())) {
                    categoryList.add(recipe.getType());
                }
            }
            g.setRecipeList(recipeList);
            g.setCategoryList(categoryList);
        }
        loading();
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
                    sleep(5000);  //Delay of 5 seconds
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
