package com.example.blockbuddytfg;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    //Splash Screen Time
    private static int SPLASH_SCREEN = 3000;

    //Variables
    ImageView logo, squircle1, squircle2;
    Animation topAnim, bottomAnim;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String correo = sharedPreferences.getString("correo", "");
        String contraseña = sharedPreferences.getString("contraseña", "");
        if (!correo.isEmpty() && !contraseña.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Obtenemos el usuario autenticado de firebase authenticacion
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //Obtenemos el uid del usuario autenticado
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            //Obtenemos la referencia de la base de datos
                            DatabaseReference userRefe = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
                            DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference("Administradores").child(uid);

                            //Comprobamos si el usuario es administrador
                            adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        //El usuario es administrador
                                        //Obtenemos los datos del usuario
                                        Administrador usuario = snapshot.getValue(Administrador.class);
                                        //Pasar los datos del usuario a la siguiente activity
                                        Intent intent = new Intent(SplashActivity.this, MainAdministradorActivity.class);
                                        intent.putExtra("user", user);
                                        intent.putExtra("uid", uid);
                                        intent.putExtra("telefono", usuario.getTelefono());
                                        intent.putExtra("imagen", usuario.getImagen());

                                        startActivity(intent);
                                    } else {
                                        //El usuario no es administrador
                                        //Obtenemos los datos del usuario
                                        userRefe.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                //Obtenemos los datos del usuario
                                                Usuario usuario = snapshot.getValue(Usuario.class);
                                                //Pasar los datos del usuario a la siguiente activity
                                                Intent intent = new Intent(SplashActivity.this, MainUserActivity.class);
                                                intent.putExtra("user", user);
                                                intent.putExtra("uid", uid);
                                                intent.putExtra("codCom", usuario.getCodComunidad());
                                                intent.putExtra("nombre", usuario.getNombre());
                                                intent.putExtra("telefono", usuario.getTelefono());
                                                intent.putExtra("piso", usuario.getPiso());
                                                intent.putExtra("puerta", usuario.getPuerta());
                                                intent.putExtra(("categoria"), usuario.getCategoria());
                                                intent.putExtra("imagen", usuario.getImagen());

                                                startActivity(intent);
                                                finish();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(SplashActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SplashActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // El inicio de sesión automático falló.
                            // Abre la actividad de inicio de sesión y finaliza la SplashScreen.
                            Intent intent = new Intent(SplashActivity.this, LoginRegisterActivity.class);
                            startActivity(intent);
                        }
                    });
        } else {
            // No se encontraron credenciales de inicio de sesión automático.
            // Abre la actividad de inicio de sesión y finaliza la SplashScreen.

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


    }
}