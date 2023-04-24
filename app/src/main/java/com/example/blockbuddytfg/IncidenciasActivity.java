package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.blockbuddytfg.adapters.IncidenciasAdapter;
import com.example.blockbuddytfg.entities.Incidencia;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class IncidenciasActivity extends AppCompatActivity {

    private IncidenciasAdapter incidenciasAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    FirebaseUser user;
    Toolbar toolbar;
    ImageButton addIncidencia;
    String codComunidad,filtro = "pendientes";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pendientes:
                filtro = "pendientes";
                Query query1 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada").equalTo(codComunidad+"_"+false);
                FirebaseRecyclerOptions<Incidencia> optionsPendientes = new FirebaseRecyclerOptions.Builder<Incidencia>()
                        .setQuery(query1, Incidencia.class)
                        .build();

                incidenciasAdapter = new IncidenciasAdapter(optionsPendientes, user,this, filtro);
                recyclerView.setAdapter(incidenciasAdapter);
                incidenciasAdapter.startListening();
                return true;
            case R.id.validadas:
                filtro = "validadas";
                Query query2 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada").equalTo(codComunidad+"_"+true);
                FirebaseRecyclerOptions<Incidencia> optionsValidadas = new FirebaseRecyclerOptions.Builder<Incidencia>()
                        .setQuery(query2, Incidencia.class)
                        .build();

                incidenciasAdapter = new IncidenciasAdapter(optionsValidadas, user,this, filtro);
                recyclerView.setAdapter(incidenciasAdapter);
                incidenciasAdapter.startListening();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_incidencias);

        InicializarHooks();
        toolbar = findViewById(R.id.incidencias_toolbar);
        setToolbar(toolbar);

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
            Query query = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada").equalTo(codComunidad+"_"+false);
            FirebaseRecyclerOptions<Incidencia> options = new FirebaseRecyclerOptions.Builder<Incidencia>()
                    .setQuery(query, Incidencia.class)
                    .build();

            incidenciasAdapter = new IncidenciasAdapter(options, user,this, filtro);
            recyclerView.setAdapter(incidenciasAdapter);
            incidenciasAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        incidenciasAdapter.stopListening();
    }

    private void setToolbar(androidx.appcompat.widget.Toolbar toolbar) {
        setSupportActionBar(toolbar);
        // Añadimos la flecha de retroceso.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Método que cierra la activity al pulsar sobre la flecha de retroceso.
     * @return - True, si se ha pulsado sobre la flecha de retroceso.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}