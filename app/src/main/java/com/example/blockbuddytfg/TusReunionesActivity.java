package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.blockbuddytfg.adapters.ComunidadAdapter;
import com.example.blockbuddytfg.adapters.ContactoAdapter;
import com.example.blockbuddytfg.adapters.ReunionAdapter;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Contacto;
import com.example.blockbuddytfg.entities.Reunion;
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

public class TusReunionesActivity extends AppCompatActivity {

    private ReunionAdapter reunionAdapter;
    private FirebaseUser user;
    private LinearLayoutManager layoutManager;
    private DatabaseReference reunionesRef, refuser, mAdmin;;
    private RecyclerView recyclerView;
    private String codComunidad,codComAdmin,filtro;
    private ImageButton addReu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tus_reuniones);

        inicializarHooks();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //admin o usuario
        recogerDatosUsuarioAdmin(refuser);
    }

    private void inicializarHooks(){
        addReu = findViewById(R.id.reuniones_btn_addContacto);
        recyclerView = findViewById(R.id.reuniones_recycler_view);
        reunionesRef = FirebaseDatabase.getInstance().getReference().child("Reuniones");
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
                    addReu.setVisibility(View.GONE);
                    codComunidad = usuario.getCodComunidad();
                    filtro = "usuario";

                    Query query = FirebaseDatabase.getInstance().getReference().child("Reuniones").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Reunion> options = new FirebaseRecyclerOptions.Builder<Reunion>()
                            .setQuery(query, Reunion.class)
                            .build();

                    reunionAdapter = new ReunionAdapter(options, reunionesRef, user, TusReunionesActivity.this, codComunidad,filtro);
                    recyclerView.setAdapter(reunionAdapter);
                    reunionAdapter.startListening();

                } else {
                    //admin | presidente
                    filtro = "admin";
                    addReu.setVisibility(View.VISIBLE);
                    añadirReunion();

                    codComunidad = codComAdmin;
                    Query query = FirebaseDatabase.getInstance().getReference().child("Reuniones").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Reunion> options = new FirebaseRecyclerOptions.Builder<Reunion>()
                            .setQuery(query, Reunion.class)
                            .build();

                    reunionAdapter = new ReunionAdapter(options, reunionesRef, user, TusReunionesActivity.this, codComunidad, filtro);
                    recyclerView.setAdapter(reunionAdapter);
                    reunionAdapter.startListening();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void añadirReunion(){
        addReu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TusReunionesActivity.this, RegisterReunionesActivity.class);
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

        Query query = FirebaseDatabase.getInstance().getReference().child("Reuniones").orderByChild("codComunidad").equalTo(codComAdmin);
        FirebaseRecyclerOptions<Reunion> options = new FirebaseRecyclerOptions.Builder<Reunion>()
                .setQuery(query, Reunion.class)
                .build();

        reunionAdapter = new ReunionAdapter(options, reunionesRef, user, TusReunionesActivity.this, codComunidad, filtro);
        recyclerView.setAdapter(reunionAdapter);
        reunionAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        reunionAdapter.stopListening();
    }


}