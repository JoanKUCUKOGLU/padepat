package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    List<Recipe> recipeList;
    List<String> categoryList;
    Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Hide ActionBar
        getSupportActionBar().hide();

        // Check if device is connected to internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else {
            connected = false;
        }

        if (connected) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/recipes");

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    recipeList = new ArrayList<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Recipe recipe = data.getValue(Recipe.class);
                        recipeList.add(recipe);

                    }
                    Log.d("TEST", "Value is: " + recipeList);
                    loading();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TEST", "Failed to read value.", error.toException());
                }
            });
        }
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
                    i.putParcelableArrayListExtra("recipeList", (ArrayList<? extends Parcelable>) recipeList);
                    startActivity(i);
                    finish();
                }
            }
        };
        loadingThread.start();
    }
}
