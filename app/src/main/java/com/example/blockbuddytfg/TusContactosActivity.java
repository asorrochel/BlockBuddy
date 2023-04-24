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
import com.example.blockbuddytfg.adapters.ContactoAdapter;
import com.example.blockbuddytfg.entities.Contacto;
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

public class TusContactosActivity extends AppCompatActivity {

    private ContactoAdapter contactoAdapter;
    private FirebaseUser user;
    private LinearLayoutManager layoutManager;
    private DatabaseReference contactosRef, refuser;
    private RecyclerView recyclerView;
    private String codComunidad,codComAdmin,filtro;
    private ImageButton addCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tus_contactos);

        inicializarHooks();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //admin o usuario
        recogerDatosUsuarioAdmin(refuser);
    }

    private void inicializarHooks(){
        addCon = findViewById(R.id.contactos_btn_addContacto);
        recyclerView = findViewById(R.id.contactos_recycler_view);
        contactosRef = FirebaseDatabase.getInstance().getReference().child("Contactos");
        user = FirebaseAuth.getInstance().getCurrentUser();
        refuser = FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid());
        codComAdmin = getIntent().getStringExtra("codCom");
    }

    private void recogerDatosUsuarioAdmin(DatabaseReference refuser){
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    //usuario
                    addCon.setVisibility(View.GONE);
                    codComunidad = usuario.getCodComunidad();
                    filtro = "usuario";

                    Query query = FirebaseDatabase.getInstance().getReference().child("Contactos").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Contacto> options = new FirebaseRecyclerOptions.Builder<Contacto>()
                            .setQuery(query, Contacto.class)
                            .build();

                    contactoAdapter = new ContactoAdapter(options, contactosRef, user, TusContactosActivity.this, codComunidad,filtro);
                    recyclerView.setAdapter(contactoAdapter);
                    contactoAdapter.startListening();


                } else {
                    //admin | presidente
                    filtro = "admin";
                    addCon.setVisibility(View.VISIBLE);
                    añadirContacto();

                    codComunidad = codComAdmin;
                    Query query = FirebaseDatabase.getInstance().getReference().child("Contactos").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Contacto> options = new FirebaseRecyclerOptions.Builder<Contacto>()
                            .setQuery(query, Contacto.class)
                            .build();

                    contactoAdapter = new ContactoAdapter(options, contactosRef, user, TusContactosActivity.this, codComunidad, filtro);
                    recyclerView.setAdapter(contactoAdapter);
                    contactoAdapter.startListening();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void añadirContacto(){
        addCon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TusContactosActivity.this, RegisterContactoActivity.class);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });
    }


}