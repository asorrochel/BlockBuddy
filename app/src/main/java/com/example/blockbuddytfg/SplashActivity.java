package com.example.blockbuddytfg;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    //Splash Screen Time
    private static int SPLASH_SCREEN = 3000;

    //Variables
    ImageView logo, squircle1, squircle2;
    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //Hooks
        logo = findViewById(R.id.splash_logo);
        squircle1 = findViewById(R.id.squircle1);
        squircle2 = findViewById(R.id.squircle2);

        //Animaciones
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Set Animaciones
        squircle1.setAnimation(bottomAnim);
        squircle2.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);

        //Cambiar de actividad
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginRegisterActivity.class);
                //Añadir animación de transición entre actividades
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(logo, "logo_image");
                //Comprobar versión de Android es superior a Lollipop para evitar errores
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        },SPLASH_SCREEN);





    }
}