package com.padepatfood.padepat;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRegisterActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    LinearLayout loginRegisterLinearLayout;
    Button goBackButton;
    EditText pseudoInput;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordConfirmationInput;
    Button saveButton;
    boolean isStartPage = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().hide();

        findViews();
        setPage();

    }
    //Regroupe tous les findViewById
    private void findViews(){
        registerButton = findViewById(R.id.regiserButton);
        loginButton = findViewById(R.id.loginButton);
        loginRegisterLinearLayout = findViewById(R.id.loginRegisterLinearLayout);
        goBackButton = findViewById(R.id.goBack);
    }

    //Remplis les view avec les datas correspondantes
    private void setPage(){

        goBackButton.setVisibility(View.INVISIBLE);
        generateItems();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegisterPage();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginPage();
            }
        });
    }

    //Créé des EditText ainsi qu'un bouton
    private void generateItems(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100,0,100,10);
        pseudoInput = new EditText(LoginRegisterActivity.this);
        setEditTextParams(pseudoInput,"Pseudonyme",params, R.drawable.ic_baseline_face_24);

        emailInput = new EditText(LoginRegisterActivity.this);
        setEditTextParams(emailInput,"Email",params, R.drawable.ic_baseline_alternate_email_24);

        passwordInput = new EditText(LoginRegisterActivity.this);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setEditTextParams(passwordInput,"Mot de passe",params, R.drawable.ic_baseline_lock_24);

        passwordConfirmationInput = new EditText(LoginRegisterActivity.this);
        passwordConfirmationInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setEditTextParams(passwordConfirmationInput,"Confirmation mot de passe",params, R.drawable.ic_baseline_lock_24);

        saveButton = new Button(LoginRegisterActivity.this);
        saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FCE68B")));
        saveButton.setTextColor(Color.parseColor("#000000"));
        params.topMargin = 20;
        params.bottomMargin = 0;
        saveButton.setLayoutParams(params);
    }

    //Affiche les éléments liés au login
    private void setLoginPage(){
        loginRegisterLinearLayout.removeAllViews();
        passwordInput.setText(null);
        loginRegisterLinearLayout.addView(emailInput);
        loginRegisterLinearLayout.addView(passwordInput);

        saveButton.setText("Se connecter");
        goBackButton.setText("Je n'ai pas de compte");
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegisterPage();
            }
        });
        loginRegisterLinearLayout.addView(saveButton);
        isStartPage = false;
    }

    //Affiche les éléments liés a la création d'un nouveau compte
    private void setRegisterPage(){
        loginRegisterLinearLayout.removeAllViews();
        loginRegisterLinearLayout.addView(pseudoInput);
        loginRegisterLinearLayout.addView(emailInput);
        passwordInput.setText(null);
        loginRegisterLinearLayout.addView(passwordInput);
        loginRegisterLinearLayout.addView(passwordConfirmationInput);
        saveButton.setText("S'enregister");
        goBackButton.setText("J'ai déjà un compte");
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginPage();
            }
        });
        goBackButton.setVisibility(View.VISIBLE);
        loginRegisterLinearLayout.addView(saveButton);
        isStartPage = false;
    }

    //Affiche les boutons de choix "Se connecter" et "S'enregistrer"
    private void setStartPage(){
        loginRegisterLinearLayout.removeAllViews();
        pseudoInput.setText(null);
        emailInput.setText(null);
        passwordInput.setText(null);
        passwordConfirmationInput.setText(null);
        loginRegisterLinearLayout.addView(registerButton);
        loginRegisterLinearLayout.addView(loginButton);
        isStartPage = true;
    }

    // --- FONCTIONS OUTILS ---- //

    //Fonction créée pour éviter la redondance de code - Applique les parametres layout, ainsi que le hint
    private void setEditTextParams(EditText editText,String hintText, LinearLayout.LayoutParams params,int drawable){
        editText.setBackgroundResource(R.drawable.field_rounded_corner_bottom_border);
        editText.setPadding(50,editText.getPaddingTop(),100,editText.getPaddingBottom());
        editText.setLayoutParams(params);
        editText.setHint(hintText);
        editText.setMaxLines(1);
        editText.setFilters( new InputFilter[]{new InputFilter.LengthFilter(50)});
        editText.setCompoundDrawablesWithIntrinsicBounds(drawable, 0,0 , 0);
        editText.setCompoundDrawablePadding(50);
    }

    @Override
    public void onBackPressed() {
        //Ferme l'activity ou affiche à nouveau les boutons de choix "Se connecter" et "S'enregistrer"
        if(isStartPage){
            super.onBackPressed();
        }else{
            setStartPage();
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}