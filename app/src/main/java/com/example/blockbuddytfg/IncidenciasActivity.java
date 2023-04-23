package com.example.blockbuddytfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.blockbuddytfg.adapters.ComunidadAdapter;
import com.example.blockbuddytfg.adapters.IncidenciasAdapter;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Incidencia;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class IncidenciasActivity extends AppCompatActivity {

    private IncidenciasAdapter incidenciasAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    FirebaseUser user;
    ImageButton addIncidencia;
    String codComunidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_incidencias);

        InicializarHooks();

        recyclerView = findViewById(R.id.incidencias_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        addIncidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncidenciasActivity.this, RegistrarIncidenciaActivity.class);
                intent.putExtra("codCom", codComunidad);
                startActivity(intent);
            }
        });
    }

    private void InicializarHooks() {
        addIncidencia = findViewById(R.id.incidencias_btn_añadirIncidencia);
        codComunidad = getIntent().getStringExtra("codCom");
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("codComunidad").equalTo(codComunidad);
        FirebaseRecyclerOptions<Incidencia> options = new FirebaseRecyclerOptions.Builder<Incidencia>()
                .setQuery(query, Incidencia.class)
                .build();

        incidenciasAdapter = new IncidenciasAdapter(options);
        recyclerView.setAdapter(incidenciasAdapter);
        incidenciasAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        incidenciasAdapter.stopListening();
    }
}