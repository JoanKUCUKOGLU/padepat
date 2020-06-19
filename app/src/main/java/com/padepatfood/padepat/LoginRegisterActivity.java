package com.padepatfood.padepat;

import android.content.Context;
import android.content.Intent;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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


    private Animation popAnimation;
    private Animation fadeinAnimation;
    private Animation fadeoutAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().hide();

        g = GlobalData.getInstance();

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("/users");

        findViews();
        setPage();

        popAnimation = AnimationUtils.loadAnimation(LoginRegisterActivity.this, R.anim.animationpop);
        fadeinAnimation = AnimationUtils.loadAnimation(LoginRegisterActivity.this, R.anim.fadein);
        fadeoutAnimation = AnimationUtils.loadAnimation(LoginRegisterActivity.this, R.anim.fadeout);

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
        setEditTextParams(pseudoInput,getString(R.string.nickname),params, R.drawable.ic_baseline_face_24);

        emailInput = new EditText(LoginRegisterActivity.this);
        setEditTextParams(emailInput,getString(R.string.email),params, R.drawable.ic_baseline_alternate_email_24);

        passwordInput = new EditText(LoginRegisterActivity.this);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setEditTextParams(passwordInput,getString(R.string.password),params, R.drawable.ic_baseline_lock_24);

        passwordConfirmationInput = new EditText(LoginRegisterActivity.this);
        passwordConfirmationInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        setEditTextParams(passwordConfirmationInput,getString(R.string.password_confirmation),params, R.drawable.ic_baseline_lock_24);

        saveButton = new Button(LoginRegisterActivity.this);
        saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FCE68B")));
        saveButton.setTextColor(Color.parseColor("#000000"));
        params.topMargin = 20;
        params.bottomMargin = 0;
        saveButton.setLayoutParams(params);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        saveButton.setText(getString(R.string.sign_up));
        goBackButton.setText(getString(R.string.account_already_exists));
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRegisterLinearLayout.startAnimation(fadeoutAnimation);
                setLoginPage();
            }
        });
        goBackButton.setVisibility(View.VISIBLE);
        loginRegisterLinearLayout.addView(saveButton);
        loginRegisterLinearLayout.startAnimation(fadeinAnimation);
        isStartPage = false;
    }

    //Affiche les éléments liés au login
    private void setLoginPage() {
        isRegister = false;
        loginRegisterLinearLayout.removeAllViews();
        passwordInput.setText(null);
        loginRegisterLinearLayout.addView(emailInput);
        loginRegisterLinearLayout.addView(passwordInput);

        saveButton.setText(getString(R.string.sign_in));
        goBackButton.setText(getString(R.string.account_doesnt_exists));
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRegisterLinearLayout.startAnimation(fadeoutAnimation);
                setRegisterPage();
            }
        });
        loginRegisterLinearLayout.addView(saveButton);
        loginRegisterLinearLayout.startAnimation(fadeinAnimation);
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

    private void setEnableToAllElements(boolean toggle){
        for(int i=0; i< loginRegisterLinearLayout.getChildCount();i++){
            loginRegisterLinearLayout.getChildAt(i).setEnabled(toggle);
        }
        goBackButton.setEnabled(toggle);
        saveButton.setEnabled(toggle);
        if(!toggle){
            loadingItem.setVisibility(View.VISIBLE);
            goBackButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6C373")));
            saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6C373")));
        }else{
            loadingItem.setVisibility(View.INVISIBLE);
            goBackButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button)));
            saveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button)));
        }
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
        if(isNicknameValid() && isEmailValid() && isEmailAvailable() && isPasswordValid() && isPasswordConfirmationValid()) {
            isInConnection = true;
            setEnableToAllElements(false);
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
            isInConnection = false;
            setEnableToAllElements(true);
            if(!isNicknameValid()){
                Toast.makeText(LoginRegisterActivity.this, getString(R.string.three_caracters_needed) ,Toast.LENGTH_SHORT).show();
            }
            if(!isEmailValid()){
                Toast.makeText(LoginRegisterActivity.this, getString(R.string.invalid_email) ,Toast.LENGTH_SHORT).show();
            }
            if(!isEmailAvailable()){
                Toast.makeText(LoginRegisterActivity.this, getString(R.string.email_already_used) ,Toast.LENGTH_SHORT).show();
            }
            if(!isPasswordValid()){
                Toast.makeText(LoginRegisterActivity.this, getString(R.string.invalid_password) ,Toast.LENGTH_LONG).show();
            }
            if(!isPasswordConfirmationValid()){
                Toast.makeText(LoginRegisterActivity.this, getString(R.string.passwords_dont_match) ,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loginUser() {
        if(checkUserExist()){
            isInConnection = true;
            setEnableToAllElements(false);
            finish();
            Intent intent = new Intent("finish_activity");
            sendBroadcast(intent);
        }else{
            isInConnection = false;
            setEnableToAllElements(true);
            Toast.makeText(LoginRegisterActivity.this, getString(R.string.password_email_invalid) ,Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkUserExist(){
        boolean userExist = false;
        for(User user : g.getUserList()) {
        if(user.getEmail().equals(emailInput.getText().toString()) && user.getPassword().equals(encodePassword())) {
            g.setCurrentUser(user);
            userExist = true;
        }
    }

    return  userExist;
}
    private boolean isNicknameValid() {
        Pattern pattern = Pattern.compile("[a-zA-Z]{3,}");
        return pattern.matcher(pseudoInput.getText().toString()).matches();
    }

    private boolean isPasswordValid() {
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$");
        return pattern.matcher(passwordInput.getText().toString()).matches();
    }

    private boolean isPasswordConfirmationValid(){
        return passwordInput.getText().toString().equals(passwordConfirmationInput.getText().toString());
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