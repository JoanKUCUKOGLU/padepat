package com.padepatfood.padepat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class LoginRegisterActivity extends AppCompatActivity {

    private GlobalData g;

    private LinearLayout loginRegisterLinearLayout;

    private boolean isStartPage = true;
    private boolean isInConnection = false;
    private ProgressBar loadingItem;

    private boolean isRegister;

    private Button registerButton;
    private Button loginButton;
    private Button saveButton;
    private Button goBackButton;

    private EditText pseudoInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordConfirmationInput;

    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().hide();

        g = GlobalData.getInstance();

        findViews();
        setPage();
    }

    //Regroupe tous les findViewById
    private void findViews(){
        registerButton = findViewById(R.id.regiserButton);
        loginButton = findViewById(R.id.loginButton);
        loginRegisterLinearLayout = findViewById(R.id.loginRegisterLinearLayout);
        goBackButton = findViewById(R.id.goBack);
        loadingItem = findViewById(R.id.loadingConnection);
    }

    //Remplis les view avec les datas correspondantes
    private void setPage(){
        loadingItem.setVisibility(View.INVISIBLE);
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
    private void generateItems() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(100,0,100,10);
        pseudoInput = new EditText(LoginRegisterActivity.this);
        setEditTextParams(pseudoInput,"Pseudonyme",params, R.drawable.ic_baseline_face_24);

        loginRegisterLinearLayout = findViewById(R.id.loginRegisterLinearLayout);
        pseudoInput = new EditText(LoginRegisterActivity.this);
        pseudoInput.setEms(10);
        pseudoInput.setLayoutParams(params);
        pseudoInput.setHint("Pseudonyme");
        emailInput = new EditText(LoginRegisterActivity.this);
        setEditTextParams(emailInput,"Email",params, R.drawable.ic_baseline_alternate_email_24);

        emailInput = new EditText(LoginRegisterActivity.this);
        emailInput.setHint("Email");
        emailInput.setEms(10);
        emailInput.setLayoutParams(params);
        passwordInput = new EditText(LoginRegisterActivity.this);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setEditTextParams(passwordInput,"Mot de passe",params, R.drawable.ic_baseline_lock_24);

        passwordInput = new EditText(LoginRegisterActivity.this);
        passwordInput.setHint("Mot de passe");
        passwordInput.setEms(10);
        passwordInput.setLayoutParams(params);
        passwordConfirmationInput = new EditText(LoginRegisterActivity.this);
        passwordConfirmationInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setEditTextParams(passwordConfirmationInput,"Confirmation mot de passe",params, R.drawable.ic_baseline_lock_24);

        passwordConfirmationInput = new EditText(LoginRegisterActivity.this);
        passwordConfirmationInput.setHint("Confirmation mot de passe");
        passwordConfirmationInput.setEms(10);
        passwordConfirmationInput.setLayoutParams(params);

        saveButton = new Button(LoginRegisterActivity.this);
        saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FCE68B")));
        saveButton.setTextColor(Color.parseColor("#000000"));
        params.topMargin = 20;
        params.bottomMargin = 0;
        saveButton.setLayoutParams(params);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInConnection = true;

                for(int i=0; i< loginRegisterLinearLayout.getChildCount();i++){
                    loginRegisterLinearLayout.getChildAt(i).setEnabled(false);
                }
                loadingItem.setVisibility(View.VISIBLE);
                goBackButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6C373")));
                goBackButton.setEnabled(false);
                saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6C373")));
                saveButton.setEnabled(false);

                if(isRegister) {
                    registerUser();
                } else {
                    loginUser();
                }
            }
        });
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

    //Affiche les éléments liés a la création d'un nouveau compte
    private void setRegisterPage() {
        isRegister = true;
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

    //Affiche les éléments liés au login
    private void setLoginPage() {
        isRegister = false;
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
        if(!isInConnection){
            //Ferme l'activity ou affiche à nouveau les boutons de choix "Se connecter" et "S'enregistrer"
            if(isStartPage){
                super.onBackPressed();
            }else{
                setStartPage();
            }
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

    // MANAGE USER SECTION -------------------

    private void registerUser() {
        if(isNicknameValid() && isEmailValid() && isEmailAvailable() && isPasswordValid()) {
            String hashed = encodePassword();

            User newUser = new User(
                    g.getUserList().size(),
                    pseudoInput.getText().toString(),
                    emailInput.getText().toString(),
                    hashed,
                    "img",
                    "test"
            );
            userRef.child(String.valueOf(newUser.getId())).setValue(newUser);
            finish();
        } else {
            // TODO : display error
        }
    }

    private void loginUser() {
        for(User user : g.getUserList()) {
            if(user.getEmail().equals(emailInput.getText().toString()) && user.getPassword().equals(encodePassword())) {
                g.setCurrentUser(user);
                finish();
            }
        }
        // TODO : if access here, display error
    }

    private boolean isNicknameValid() {
        Pattern pattern = Pattern.compile("[a-zA-Z]{3,}");
        return pattern.matcher(pseudoInput.getText().toString()).matches();
    }

    private boolean isPasswordValid() {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$");
        return pattern.matcher(passwordInput.getText().toString()).matches() &&
                passwordInput.getText().toString().equals(passwordConfirmationInput.getText().toString());
    }

    private boolean isEmailValid() {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(emailInput.getText().toString()).matches();
    }

    private boolean isEmailAvailable() {
        for(User user : g.getUserList()) {
            if(user.getEmail().equals(emailInput.getText().toString())) {
                return false;
            }
        }
        return true;
    }

    private String encodePassword() {
        try {
            String passwordToHash = passwordInput.getText().toString();

            Key key = new SecretKeySpec("1Hbfh667adfDEJ78".getBytes(),"AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte [] encryptedByteValue = cipher.doFinal(passwordToHash.getBytes("utf-8"));
            String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);

            return encryptedValue64;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return "";
        }
    }
}