package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.blockbuddytfg.adapters.ComunidadAdapter;
import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainAdministradorActivity extends AppCompatActivity {
    private ComunidadAdapter communityAdapter;
    private FirebaseUser user;
    private LinearLayoutManager layoutManager;
    private DatabaseReference communityRef;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_administrador);

        ImageButton addCom = findViewById(R.id.admin_btn_añadirComunidad);
        TextView cerrarSesion = findViewById(R.id.mainAdminstrador_textView_Welcome);
        TextView ajustes = findViewById(R.id.mainAdministrador_ajustes);
        recyclerView = findViewById(R.id.admin_recycler_view);
        communityRef = FirebaseDatabase.getInstance().getReference().child("Comunidades");
        user = FirebaseAuth.getInstance().getCurrentUser();

        String uid = user.getUid();
        DatabaseReference refuser = FirebaseDatabase.getInstance().getReference("Administradores").child(uid);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(MainAdministradorActivity.this,  R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                        .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().clear().apply();
                                Intent intent = new Intent(MainAdministradorActivity.this, LoginRegisterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
        addCom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainAdministradorActivity.this, RegisterComunidadActivity.class);
                startActivity(intent);
            }
        });
        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAdministradorActivity.this, AjustesPerfilAdmin.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Administrador usuario = snapshot.getValue(Administrador.class);
                if(usuario != null) {
                    String nombre = usuario.getNombre();
                    cerrarSesion.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("Comunidades").orderByChild("administrador").equalTo(user.getUid());
        FirebaseRecyclerOptions<Comunidad> options = new FirebaseRecyclerOptions.Builder<Comunidad>()
                .setQuery(query, Comunidad.class)
                .build();

        communityAdapter = new ComunidadAdapter(options, communityRef, user, this);
        recyclerView.setAdapter(communityAdapter);
        communityAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        communityAdapter.stopListening();
    }
}
