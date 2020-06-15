package com.padepatfood.padepat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
            text.setText("This app needs an Internet connection");
            text.setGravity(Gravity.CENTER);
            alert.setView(text);
            alert.show();
        }

        if (connected) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/recipes");

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    recipeList = new ArrayList<>();
                    categoryList = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Recipe recipe = data.getValue(Recipe.class);
                        recipeList.add(recipe);

                        if (!categoryList.contains(recipe.getType())) {
                            categoryList.add(recipe.getType());
                        }
                    }
                    Log.d("TEST", "Value is: " + recipeList);
                    g.setRecipeList(recipeList);
                    g.setCategoryList(categoryList);

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

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TEST", "Failed to read value.", error.toException());
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

    private Bitmap drawable_from_url(String url) throws java.net.MalformedURLException, java.io.IOException {

        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");

        connection.connect();
        InputStream input = connection.getInputStream();

        return BitmapFactory.decodeStream(input);
    }
}
