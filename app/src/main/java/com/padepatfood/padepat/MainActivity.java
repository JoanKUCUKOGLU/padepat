package com.padepatfood.padepat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baoyz.widget.PullRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private GlobalData g;

    PullRefreshLayout layout;

    private FirstAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout navBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        adapter = new FirstAdapter();
        recyclerView = findViewById(R.id.firstRecyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        layout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layout.postDelayed(() ->{
                    layout.setRefreshing(false);
                },3000);
            }
        });

        navBar = findViewById(R.id.navBarLayout);
        //ImageView homeButton = (ImageView)navBar.getChildAt(0);
        ImageView favButton = (ImageView)navBar.getChildAt(1);
        ImageView profileButton = (ImageView)navBar.getChildAt(2);
        boolean isConnected = false;
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected){

                }else{
                    Intent newActivity = new Intent(MainActivity.this, LoginRegisterActivity.class);
                    startActivity(newActivity);
                }
            }
        });
    }
}

