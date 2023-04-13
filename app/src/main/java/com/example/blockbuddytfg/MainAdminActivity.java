package com.example.blockbuddytfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.blockbuddytfg.adapters.AdministradorAdapter;
import com.example.blockbuddytfg.entities.Administrador;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainAdminActivity extends AppCompatActivity {

    private AdministradorAdapter administradorAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_admin);

        user = FirebaseAuth.getInstance().getCurrentUser();

        MaterialButton btnAddUser = findViewById(R.id.btnRegistrarAdmin);
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Administradores");
        FirebaseRecyclerOptions<Administrador> options =
                new FirebaseRecyclerOptions.Builder<Administrador>()
                        .setQuery(adminRef, Administrador.class)
                        .build();
        AdministradorAdapter adapter = new AdministradorAdapter(options, adminRef, user, this);
        recyclerView = findViewById(R.id.admin2_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        administradorAdapter = adapter;

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdminActivity.this, RegistroAdminActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        administradorAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        administradorAdapter.stopListening();
    }
}
