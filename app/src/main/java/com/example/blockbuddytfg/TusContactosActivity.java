package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.blockbuddytfg.adapters.ComunidadAdapter;
import com.example.blockbuddytfg.adapters.ContactoAdapter;
import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Contacto;
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

public class TusContactosActivity extends AppCompatActivity {

    private ContactoAdapter contactoAdapter;
    private FirebaseUser user;
    private LinearLayoutManager layoutManager;
    private DatabaseReference contactosRef;
    private RecyclerView recyclerView;
    private String codComunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tus_contactos);

        ImageButton addCon = findViewById(R.id.contactos_btn_addContacto);

        recyclerView = findViewById(R.id.contactos_recycler_view);
        contactosRef = FirebaseDatabase.getInstance().getReference().child("Contactos");
        user = FirebaseAuth.getInstance().getCurrentUser();

        String uid = user.getUid();
        DatabaseReference refuser = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    codComunidad = usuario.getCodComunidad();
                    Log.println(Log.INFO, "Comunidad", codComunidad);

                    Query query = FirebaseDatabase.getInstance().getReference().child("Contactos").orderByChild("codComunidad").equalTo(codComunidad);
                    FirebaseRecyclerOptions<Contacto> options = new FirebaseRecyclerOptions.Builder<Contacto>()
                            .setQuery(query, Contacto.class)
                            .build();

                    contactoAdapter = new ContactoAdapter(options, contactosRef, user, TusContactosActivity.this, codComunidad);
                    recyclerView.setAdapter(contactoAdapter);
                    contactoAdapter.startListening();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        addCon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TusContactosActivity.this, RegisterContactoActivity.class);
                startActivity(intent);
            }
        });
    }
}