package com.padepatfood.padepat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NavBarGestion {
    private static GlobalData g = GlobalData.getInstance();
    public static void manage(String parentActivity, String flagActivity, Activity context){
        LinearLayout navBar = context.findViewById(R.id.navBarLayout);
        boolean canStartNewHomeActivity = true;
        boolean canStartNewProfileActivity = true;
        boolean canStartNewFavActivity = true;
        ImageView homeButton = (ImageView)navBar.getChildAt(0);
        ImageView favButton = (ImageView)navBar.getChildAt(1);
        ImageView profileButton = (ImageView)navBar.getChildAt(2);

        if(flagActivity.equals("MAIN")){
            canStartNewHomeActivity = false;
        }
        if(flagActivity.equals("FAV")){
            canStartNewFavActivity = false;
        }
        if(flagActivity.equals("PROFILE")){
            canStartNewProfileActivity = false;
        }

        boolean finalCanStartNewHomeActivity = canStartNewHomeActivity;
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!parentActivity.equals("MAIN")){
                    if(finalCanStartNewHomeActivity){
                        context.startActivity(new Intent(context,MainActivity.class).putExtra("flagActivity",parentActivity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }else{
                        context.finish();
                    }
                }
            }
        });

        boolean finalCanStartNewFavActivity = canStartNewFavActivity;
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!parentActivity.equals("FAV")){

                    if(finalCanStartNewFavActivity){

                    }else{
                        context.finish();
                    }
                }
            }
        });

        boolean finalCanStartNewProfileActivity = canStartNewProfileActivity;
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!parentActivity.equals("PROFILE"))
                if(finalCanStartNewProfileActivity){
                    if(g.isUserLogged()){
                        context.startActivity(new Intent(context,ProfilActivity.class).putExtra("flagActivity",parentActivity));
                    }else{
                        context.startActivity(new Intent(context,LoginRegisterActivity.class));
                    }
                }else{
                    context.finish();
                }
            }
        });
    }
}
