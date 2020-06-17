package com.padepatfood.padepat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;

import com.squareup.picasso.Picasso;

public class ProfilActivity extends AppCompatActivity {
    private GlobalData globalData;
    private User currentUser;

    private TextView userName;
    private TextView dietaryPrefsText;
    private TextView emailText;
    private ImageView img;
    private EditText userNameEdit;

    private CardView editPhotoCardView;
    private LinearLayout dietaryPrefsLayout;
    private LinearLayout emailLayout;
    private LinearLayout oldPwdLayout;
    private LinearLayout newPwdLayout;
    private LinearLayout confirmNewPwdLayout;
    private LinearLayout informationsLayout;

    private boolean isEditMode = false;
    private Button editSaveButton;
    private LinearLayout navBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        getSupportActionBar().hide();
        
        globalData = GlobalData.getInstance();
        currentUser = globalData.getCurrentUser();

        initData();
        setPage();
        navBarGestion();
    }

    //Cherche tous les elements avec findViewById pour les attribuer
    public void initData(){
        userName = findViewById(R.id.profilUserNameTextView);
        userNameEdit = findViewById(R.id.editUserName);
        img = findViewById(R.id.profilImageView);
        dietaryPrefsText = findViewById(R.id.dietaryPrefsText);
        emailText = findViewById(R.id.emailText);
        editPhotoCardView = findViewById(R.id.editPhotoCardView);

        informationsLayout = findViewById(R.id.informationsLayout);
        dietaryPrefsLayout = findViewById(R.id.dietaryPrefLayout);
        emailLayout = findViewById(R.id.emailLayout);
        oldPwdLayout= findViewById(R.id.oldPwdLayout);
        newPwdLayout= findViewById(R.id.newPwdLayout);
        confirmNewPwdLayout= findViewById(R.id.confirmNewPwdLayout);
        editSaveButton = findViewById(R.id.EditSavebutton);
    }

    //Affiche les éléments par rapport aux datas
    public  void setPage(){
        Picasso.get().load(currentUser.getImgLink()).fit().centerCrop().error(R.drawable.logopadepat).into(img);
        Picasso.get().setLoggingEnabled(true);
        String prefAlimentaireText = currentUser.getDietaryPrefs();
        dietaryPrefsText.setText(prefAlimentaireText);
        userName.setText(currentUser.getName());
        emailText.setText(currentUser.getEmail());

        userNameEdit.setVisibility(View.INVISIBLE);
        editPhotoCardView.setVisibility(View.INVISIBLE);
        informationsLayout.removeView(oldPwdLayout);
        informationsLayout.removeView(newPwdLayout);
        informationsLayout.removeView(confirmNewPwdLayout);
        editSaveButton.setText("Editer");

        editSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    saveChanges();
                }else{
                    setEditMode();
                }
            }
        });
    }

    //Passe en mode edition
    public void setEditMode(){
        userName.setVisibility(View.INVISIBLE);

        userNameEdit.setText(userName.getText());
        userNameEdit.setVisibility(View.VISIBLE);

        editPhotoCardView.setVisibility(View.VISIBLE);
        EditText newDietaryPrefText = new EditText(ProfilActivity.this);
        newDietaryPrefText.setLayoutParams(dietaryPrefsText.getLayoutParams());
        newDietaryPrefText.setText(dietaryPrefsText.getText());

        EditText newEmailText = new EditText(ProfilActivity.this);
        newEmailText.setLayoutParams(emailText.getLayoutParams());
        newEmailText.setText(emailText.getText());

        dietaryPrefsLayout.removeViewAt(1);
        emailLayout.removeViewAt(1);
        dietaryPrefsLayout.addView(newDietaryPrefText);
        emailLayout.addView(newEmailText);

        informationsLayout.addView(oldPwdLayout);
        informationsLayout.addView(newPwdLayout);
        informationsLayout.addView(confirmNewPwdLayout);
        editSaveButton.setText("Sauvegarder");
        isEditMode = true;
    }

    //Sauvegarde les changements
    public void saveChanges(){
        EditText dietaryPrefInput = (EditText) dietaryPrefsLayout.getChildAt(1);
        EditText emailInput = (EditText) emailLayout.getChildAt(1);

        currentUser.setName(userNameEdit.getText().toString());
        currentUser.setEmail(emailInput.getText().toString());
        currentUser.setDietaryPrefs(dietaryPrefInput.getText().toString());
        reload();
    }

    //Rafraichit la page
    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
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
                newActivity = new Intent(ProfilActivity.this, MainActivity.class);
                startActivity(newActivity);
            }
        });
    }
}
