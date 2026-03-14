package com.example.booki;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {


    Handler handler = new Handler();

    Animation leftanim, rightanim, downanim;
    ImageView img_splash1, img_splash2;

    TextView logo_txt, logo_txt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        leftanim = AnimationUtils.loadAnimation(this, R.anim.left_animation);
        rightanim = AnimationUtils.loadAnimation(this, R.anim.right_animation);
        downanim = AnimationUtils.loadAnimation(this, R.anim.down_animation);

        // Hooks
        img_splash1 = findViewById(R.id.img_splash1);
        img_splash2 = findViewById(R.id.img_splash2);
        logo_txt = findViewById(R.id.logo_text);
        logo_txt2 = findViewById(R.id.logo_txt2);

        // Setting Animation
        img_splash1.setAnimation(rightanim);
        img_splash2.setAnimation(leftanim);
        logo_txt.setAnimation(downanim);
        logo_txt2.setAnimation(downanim);

        handler.postDelayed(() -> {
            Intent i = new Intent(Splash.this, LoginActivity.class);

            Pair<View, String> pair = new Pair<>(img_splash1, "trans_img_splash1");         // variable_name, trans_name

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splash.this, pair);
            startActivity(i, options.toBundle());
            finish();
        }, 4000);
    }
}
