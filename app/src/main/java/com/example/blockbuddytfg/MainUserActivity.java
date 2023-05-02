package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainUserActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView mainUsuario_textView, mainUsuario_Welcome,mainUsuario_Welcome1;
    MaterialButton btn_tuComunidad;
    ConstraintLayout btn_CerrarSesion;
    CardView incidencias, contactos,reuniones,anuncios;
    FirebaseUser user;
    DatabaseReference ref,ref2;
    String uid,codComunidad, codComAdmin;
    Boolean esAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mainuser);

        inicializarHooks();
        //admin o usuario
        recogerDatosAdminUsuario(ref);

        //ir a incidencias
        incidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this, IncidenciasActivity.class);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });

        //ir a tu comunidad
        btn_tuComunidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this, TuComunidadActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });

        //ir a tus contactos
        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this, TusContactosActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });

        //ir a tus reuniones
        reuniones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this, TusReunionesActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });

        //ir a tus anuncios
        anuncios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this, TusAnunciosActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });
    }

    private void recogerDatosAdminUsuario(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    //usuario
                    String nombre = usuario.getNombre();
                    codComunidad = usuario.getCodComunidad();
                    mainUsuario_Welcome.setText(nombre);
                    mainUsuario_textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainUserActivity.this, AjustesPerfil.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    });
                    cerrarSesion();
                } else {
                    //administrador
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if(administrador != null) {
                                codComunidad = codComAdmin.replace("C - ", "");
                                esAdmin = true;
                                if(esAdmin){
                                    //ocultar elementos
                                    mainUsuario_textView.setVisibility(View.GONE);
                                    mainUsuario_Welcome.setVisibility(View.GONE);
                                    mainUsuario_Welcome1.setVisibility(View.GONE);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Método para inicializar los hooks.
    private void inicializarHooks() {
        mainUsuario_textView = findViewById(R.id.mainUsuario_textView);
        mainUsuario_Welcome1 = findViewById(R.id.mainUsuario_textView_Welcome1);
        mainUsuario_Welcome = findViewById(R.id.mainUsuario_textView_Welcome);
        uid = getIntent().getStringExtra("uid");
        codComAdmin = getIntent().getStringExtra("comunidad");
        user = getIntent().getParcelableExtra("user");
        ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
        ref2 = FirebaseDatabase.getInstance().getReference("Administradores").child(uid);
        btn_CerrarSesion = findViewById(R.id.mainUsuario_Welcome);
        btn_tuComunidad = findViewById(R.id.mainUsuario_btnComunidad);
        incidencias = findViewById(R.id.cardView2);
        contactos = findViewById(R.id.cardView1);
        reuniones = findViewById(R.id.cardView3);
        anuncios = findViewById(R.id.cardView4);

    }

    private void cerrarSesion(){
        btn_CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(MainUserActivity.this,  R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                        .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().clear().apply();
                                Intent intent = new Intent(MainUserActivity.this, LoginRegisterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

    }
    /**
     * Método que cierra el teclado cuando se pulsa fuera de un EditText.
     * @param event - Evento que se produce al pulsar fuera de un EditText.
     * @return - True, si se ha pulsado fuera de un EditText, False si no se ha pulsado.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Guardamos la vista seleccionada.
        View view = this.getCurrentFocus();

        // Si no es null (Tenemos una vista seleccionada), cerramos el teclado.
        if (view != null) {
            Context context = getApplicationContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        } else {
            return false;
        }
    }
}