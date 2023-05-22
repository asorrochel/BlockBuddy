package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.blockbuddytfg.adapters.AnuncioAdapter;
import com.example.blockbuddytfg.entities.Anuncio;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TusAnunciosActivity extends AppCompatActivity {

    private AnuncioAdapter anuncioAdapter;
    private FirebaseUser user;
    private LinearLayoutManager layoutManager;
    private DatabaseReference anunciosRef, refuser, mAdmin;;
    private RecyclerView recyclerView;
    private String codComunidad,codComAdmin,filtro = "admin";
    private ImageButton addAnun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tus_anuncios);

        inicializarHooks();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //admin o usuario
        recogerDatosUsuarioAdmin(refuser);
    }

    private void inicializarHooks(){
        addAnun = findViewById(R.id.anuncios_btn_add);
        recyclerView = findViewById(R.id.anuncios_recycler_view);
        anunciosRef = FirebaseDatabase.getInstance().getReference().child("Anuncios");
        user = FirebaseAuth.getInstance().getCurrentUser();
        refuser = FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid());
        mAdmin = FirebaseDatabase.getInstance().getReference().child("Administradores").child(user.getUid());
        codComAdmin = getIntent().getStringExtra("codCom");
    }

    private void recogerDatosUsuarioAdmin(DatabaseReference refuser){
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    //usuario
                    addAnun.setVisibility(View.GONE);
                    codComunidad = usuario.getCodComunidad();
                    filtro = "usuario";

                    Query query = FirebaseDatabase.getInstance().getReference().child("Anuncios").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Anuncio> options = new FirebaseRecyclerOptions.Builder<Anuncio>()
                            .setQuery(query, Anuncio.class)
                            .build();

                    anuncioAdapter = new AnuncioAdapter(options, anunciosRef, user, TusAnunciosActivity.this, codComunidad,filtro);
                    recyclerView.setAdapter(anuncioAdapter);
                    anuncioAdapter.startListening();

                } else {
                    //admin | presidente
                    filtro = "admin";
                    addAnun.setVisibility(View.VISIBLE);
                    addAnuncio();

                    codComunidad = codComAdmin;
                    Query query = FirebaseDatabase.getInstance().getReference().child("Anuncios").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Anuncio> options = new FirebaseRecyclerOptions.Builder<Anuncio>()
                            .setQuery(query, Anuncio.class)
                            .build();

                    anuncioAdapter = new AnuncioAdapter(options, anunciosRef, user, TusAnunciosActivity.this, codComunidad, filtro);
                    recyclerView.setAdapter(anuncioAdapter);
                    anuncioAdapter.startListening();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addAnuncio(){
        addAnun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TusAnunciosActivity.this, RegisterAnunciosActivity.class);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null) {
                    filtro = "usuario";
                    codComunidad = usuario.getCodComunidad();
                } else {
                    mAdmin.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Usuario admin = snapshot.getValue(Usuario.class);
                            if (admin != null) {
                                filtro = "admin";
                                codComunidad = codComAdmin;
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

        Query query = FirebaseDatabase.getInstance().getReference().child("Anuncios").orderByChild("codComunidad").equalTo(codComAdmin);
        FirebaseRecyclerOptions<Anuncio> options = new FirebaseRecyclerOptions.Builder<Anuncio>()
                .setQuery(query, Anuncio.class)
                .build();

        anuncioAdapter = new AnuncioAdapter(options, anunciosRef, user, TusAnunciosActivity.this, codComunidad, filtro);
        recyclerView.setAdapter(anuncioAdapter);
        anuncioAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        anuncioAdapter.stopListening();
    }


}