package com.example.blockbuddytfg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.blockbuddytfg.adapters.AdministradorAdapter;
import com.example.blockbuddytfg.adapters.ComunidadAdapter;
import com.example.blockbuddytfg.adapters.VecinosAdapter;
import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TusVecinosActivity extends AppCompatActivity {

    private VecinosAdapter vecinosAdapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private String codCom = null;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tus_vecinos);

        toolbar = findViewById(R.id.vecinos_toolbar);
        recyclerView = findViewById(R.id.rv_tus_vecinos);
        codCom = getIntent().getStringExtra("codCom");
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setToolbar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("Usuarios").orderByChild("codComunidad").equalTo(codCom);
        FirebaseRecyclerOptions<Usuario> options = new FirebaseRecyclerOptions.Builder<Usuario>()
                .setQuery(query, Usuario.class)
                .build();
        vecinosAdapter = new VecinosAdapter(options);
        recyclerView.setAdapter(vecinosAdapter);
        vecinosAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vecinosAdapter.stopListening();
    }

    /**
     * Método que añade a la activity un Toolbar.
     * @param toolbar - ToolBar que queremos añadir a la activity.
     */
    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        // Añadimos la flecha de retroceso.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Método que añade la funcionalidad de la flecha de retroceso.
     * @return - True, si se ha pulsado la flecha de retroceso, False si no se ha pulsado.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}