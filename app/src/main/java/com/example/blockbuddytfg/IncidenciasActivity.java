package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.blockbuddytfg.adapters.IncidenciasAdapter;
import com.example.blockbuddytfg.entities.Incidencia;
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

public class IncidenciasActivity extends AppCompatActivity {

    private IncidenciasAdapter incidenciasAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private DatabaseReference mUsuario, mAdmin;
    private TextView titulo;
    FirebaseUser user;
    Toolbar toolbar;
    ImageButton addIncidencia;
    String codComunidad, filtro = "pendientes";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        mUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null) {
                    inflater.inflate(R.menu.overflow_menu_usuario, menu);
                } else {
                    mAdmin.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Usuario admin = snapshot.getValue(Usuario.class);
                            if (admin != null) {
                                inflater.inflate(R.menu.overflow_menu_admin, menu);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pendientes:
                titulo.setText("PENDIENTES");
                filtro = "pendientes";
                Query query1 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada").equalTo(codComunidad + "_" + false);
                FirebaseRecyclerOptions<Incidencia> optionsPendientes = new FirebaseRecyclerOptions.Builder<Incidencia>()
                        .setQuery(query1, Incidencia.class)
                        .build();

                incidenciasAdapter = new IncidenciasAdapter(optionsPendientes, user, this, filtro);
                recyclerView.setAdapter(incidenciasAdapter);
                incidenciasAdapter.startListening();
                return true;
            case R.id.validadas:
                titulo.setText("VALIDADAS");
                filtro = "validadas";
                Query query2 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada").equalTo(codComunidad + "_" + true);
                FirebaseRecyclerOptions<Incidencia> optionsValidadas = new FirebaseRecyclerOptions.Builder<Incidencia>()
                        .setQuery(query2, Incidencia.class)
                        .build();

                incidenciasAdapter = new IncidenciasAdapter(optionsValidadas, user, this, filtro);
                recyclerView.setAdapter(incidenciasAdapter);
                incidenciasAdapter.startListening();
                return true;
            case R.id.propias:
                titulo.setText("MIS INCIDENCIAS");
                filtro = "propias";
                Query query3 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("usuario").equalTo(user.getUid());
                FirebaseRecyclerOptions<Incidencia> optionsPropias = new FirebaseRecyclerOptions.Builder<Incidencia>()
                        .setQuery(query3, Incidencia.class)
                        .build();

                incidenciasAdapter = new IncidenciasAdapter(optionsPropias, user, this, filtro);
                recyclerView.setAdapter(incidenciasAdapter);
                incidenciasAdapter.startListening();
                return true;
            case R.id.activas:
                titulo.setText("ACTIVAS");
                filtro = "activas";
                Query query4 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada_estado").equalTo(codComunidad + "_activa" + "_" + true);
                FirebaseRecyclerOptions<Incidencia> optionsActivas = new FirebaseRecyclerOptions.Builder<Incidencia>()
                        .setQuery(query4, Incidencia.class)
                        .build();

                incidenciasAdapter = new IncidenciasAdapter(optionsActivas, user, this, filtro);
                recyclerView.setAdapter(incidenciasAdapter);
                incidenciasAdapter.startListening();
                return true;
            case R.id.terminadas:
                titulo.setText("TERMINADAS");
                filtro = "terminadas";
                Query query5 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada_estado").equalTo(codComunidad + "_terminada" + "_" + true);
                FirebaseRecyclerOptions<Incidencia> optionsTerminadas = new FirebaseRecyclerOptions.Builder<Incidencia>()
                        .setQuery(query5, Incidencia.class)
                        .build();

                incidenciasAdapter = new IncidenciasAdapter(optionsTerminadas, user, this, filtro);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        mUsuario = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(user.getUid());
        mAdmin = FirebaseDatabase.getInstance().getReference().child("Administradores").child(user.getUid());
        titulo = findViewById(R.id.titulo_incidencias);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if (usuario != null) {
                    titulo.setText("ACTIVAS");
                    filtro = "activas";
                } else {
                    mAdmin.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Usuario admin = snapshot.getValue(Usuario.class);
                            if (admin != null) {
                                titulo.setText("VALIDADAS");
                                filtro = "validadas";
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
        Query query4 = FirebaseDatabase.getInstance().getReference().child("Incidencias").orderByChild("cod_validada_estado").equalTo(codComunidad + "_activa_" + true);
        FirebaseRecyclerOptions<Incidencia> optionsActivas = new FirebaseRecyclerOptions.Builder<Incidencia>()
                .setQuery(query4, Incidencia.class)
                .build();

        incidenciasAdapter = new IncidenciasAdapter(optionsActivas, user, this, filtro);
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
     *
     * @return - True, si se ha pulsado sobre la flecha de retroceso.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}