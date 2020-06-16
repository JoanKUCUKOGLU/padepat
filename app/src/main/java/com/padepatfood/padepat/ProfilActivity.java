package com.padepatfood.padepat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ProfilActivity extends AppCompatActivity {
    GlobalData globalData;
    TextView userName;
    TextView dietaryPrefsTitle;
    LinearLayout dietaryPrefsLayout;
    ImageView img;
    private LinearLayout navBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        globalData = GlobalData.getInstance();
        User currentUser = globalData.getCurrentUser();

        userName = findViewById(R.id.profilUserNameTextView);
        dietaryPrefsLayout = findViewById(R.id.dietaryPrefsLayout);
        img = findViewById(R.id.profilImageView);
        dietaryPrefsTitle = findViewById(R.id.dietaryPrefsTitle);

        Picasso.get().load(currentUser.getImgLink()).fit().centerCrop().error(R.drawable.logopadepat).into(img);
        Picasso.get().setLoggingEnabled(true);
        String prefAlimentaireText = "Préférence Alimentaire : "+currentUser.getDietaryPrefs();
        dietaryPrefsTitle.setText(prefAlimentaireText);
        userName.setText(currentUser.getName());

        navBar = findViewById(R.id.navBarLayout);
        //ImageView homeButton = (ImageView)navBar.getChildAt(0);
        ImageView favButton = (ImageView)navBar.getChildAt(1);
        ImageView homeButton = (ImageView)navBar.getChildAt(0);
        boolean isConnected= true;
        homeButton.setOnClickListener(new View.OnClickListener() {
            Intent newActivity;
            @Override
            public void onClick(View v) {
                newActivity = new Intent(ProfilActivity.this, MainActivity.class);
                startActivity(newActivity);
            }
        });
    }
}
