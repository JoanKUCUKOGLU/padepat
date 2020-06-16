package com.padepatfood.padepat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRegisterActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    Button saveButton;
    LinearLayout loginRegisterLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        registerButton = findViewById(R.id.regiserButton);
        loginButton = findViewById(R.id.loginButton);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.bottomMargin = 10;

        loginRegisterLinearLayout = findViewById(R.id.loginRegisterLinearLayout);
        EditText pseudoInput = new EditText(LoginRegisterActivity.this);
        pseudoInput.setEms(10);
        pseudoInput.setLayoutParams(params);
        pseudoInput.setHint("Pseudonyme");

        EditText emailInput = new EditText(LoginRegisterActivity.this);
        emailInput.setHint("Email");
        emailInput.setEms(10);
        emailInput.setLayoutParams(params);

        EditText passwordInput = new EditText(LoginRegisterActivity.this);
        passwordInput.setHint("Mot de passe");
        passwordInput.setEms(10);
        passwordInput.setLayoutParams(params);

        EditText passwordConfirmationInput = new EditText(LoginRegisterActivity.this);
        passwordConfirmationInput.setHint("Confirmation mot de passe");
        passwordConfirmationInput.setEms(10);
        passwordConfirmationInput.setLayoutParams(params);

        Button saveButton = new Button(LoginRegisterActivity.this);
        saveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FCE68B")));
        saveButton.setTextColor(Color.parseColor("#000000"));
        params.topMargin = 20;
        params.bottomMargin = 0;
        saveButton.setLayoutParams(params);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRegisterLinearLayout.removeView(registerButton);
                loginRegisterLinearLayout.removeView(loginButton);
                loginRegisterLinearLayout.addView(pseudoInput);
                loginRegisterLinearLayout.addView(emailInput);
                loginRegisterLinearLayout.addView(passwordInput);
                loginRegisterLinearLayout.addView(passwordConfirmationInput);
                saveButton.setText("S'enregister");
                loginRegisterLinearLayout.addView(saveButton);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRegisterLinearLayout.removeView(registerButton);
                loginRegisterLinearLayout.removeView(loginButton);
                loginRegisterLinearLayout.addView(emailInput);
                loginRegisterLinearLayout.addView(passwordInput);

                saveButton.setText("Se connecter");
                loginRegisterLinearLayout.addView(saveButton);
            }
        });



    }
}