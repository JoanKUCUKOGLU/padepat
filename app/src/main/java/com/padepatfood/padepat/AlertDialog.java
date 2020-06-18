package com.padepatfood.padepat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlertDialog extends AppCompatActivity {

    public void showDialog(RecipeActivity recipeActivity, String content, Context context){

        Dialog dialog = new Dialog(recipeActivity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.alertdialog);

        TextView contTextView = (TextView) dialog.findViewById(R.id.alertDialogContent);
        contTextView.setText(content);

        Button signInButton = (Button) dialog.findViewById(R.id.signinButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, LoginRegisterActivity.class));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
}
