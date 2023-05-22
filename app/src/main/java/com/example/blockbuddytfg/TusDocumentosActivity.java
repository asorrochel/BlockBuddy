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

import com.example.blockbuddytfg.adapters.DocumentoAdapter;
import com.example.blockbuddytfg.entities.Documento;
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

public class TusDocumentosActivity extends AppCompatActivity {

    private DocumentoAdapter docAdapter;
    private FirebaseUser user;
    private LinearLayoutManager layoutManager;
    private DatabaseReference anunciosRef, refuser, mAdmin;;
    private RecyclerView recyclerView;
    private String codComunidad,codComAdmin,filtro;
    private ImageButton addAnun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tus_documentos);

        inicializarHooks();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //admin o usuario
        recogerDatosUsuarioAdmin(refuser);
    }

    private void inicializarHooks(){
        addAnun = findViewById(R.id.doc_btn_add);
        recyclerView = findViewById(R.id.doc_recycler_view);
        anunciosRef = FirebaseDatabase.getInstance().getReference().child("Documentos");
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

                    Query query = FirebaseDatabase.getInstance().getReference().child("Documentos").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Documento> options = new FirebaseRecyclerOptions.Builder<Documento>()
                            .setQuery(query, Documento.class)
                            .build();

                    docAdapter = new DocumentoAdapter(options, anunciosRef, user, TusDocumentosActivity.this, codComunidad,filtro);
                    recyclerView.setAdapter(docAdapter);
                    docAdapter.startListening();

                } else {
                    //admin | presidente
                    filtro = "admin";
                    addAnun.setVisibility(View.VISIBLE);
                    addAnuncio();

                    codComunidad = codComAdmin;
                    Query query = FirebaseDatabase.getInstance().getReference().child("Documentos").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Documento> options = new FirebaseRecyclerOptions.Builder<Documento>()
                            .setQuery(query, Documento.class)
                            .build();

                    docAdapter = new DocumentoAdapter(options, anunciosRef, user, TusDocumentosActivity.this, codComunidad, filtro);
                    recyclerView.setAdapter(docAdapter);
                    docAdapter.startListening();
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
                Intent intent = new Intent(TusDocumentosActivity.this, RegisterDocumentosActivity.class);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
                finish();
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

        Query query = FirebaseDatabase.getInstance().getReference().child("Documentos").orderByChild("codComunidad").equalTo(codComAdmin);
        FirebaseRecyclerOptions<Documento> options = new FirebaseRecyclerOptions.Builder<Documento>()
                .setQuery(query, Documento.class)
                .build();

        docAdapter = new DocumentoAdapter(options, anunciosRef, user, TusDocumentosActivity.this, codComunidad, filtro);
        recyclerView.setAdapter(docAdapter);
        docAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        docAdapter.stopListening();
    }


}