package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class TuComunidadActivity extends AppCompatActivity {

    MaterialButton btn_vecinos;
    DatabaseReference mDatabase, mUsuario, mComunidad;
    TextView tucom_nombre, tucom_direccion, tucom_cp, tucom_viviendas;
    Comunidad comunidad;
    FirebaseUser user;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tu_comunidad);

        inicializarHooks();
        recogerDatosFirebase(mUsuario);
        setToolbar(toolbar);
        btn_vecinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TuComunidadActivity.this, TusVecinosActivity.class);
                intent.putExtra("codCom", comunidad.getCodigoComunidad());
                startActivity(intent);
                finish();
            }
        });
    }

    private void inicializarHooks() {
        btn_vecinos = findViewById(R.id.tucom_vecinos);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mUsuario = mDatabase.child("Usuarios").child(user.getUid());
        mComunidad = mDatabase.child("Comunidades");
        tucom_nombre = findViewById(R.id.tucom_nombre);
        tucom_direccion = findViewById(R.id.tucom_direccion);
        tucom_cp = findViewById(R.id.tucom_cp);
        tucom_viviendas = findViewById(R.id.tucom_viviendas);
        toolbar = findViewById(R.id.tucom_toolbar);
    }

    private void recogerDatosFirebase(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    mComunidad.child(usuario.getCodComunidad()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            comunidad = snapshot.getValue(Comunidad.class);
                            String nombre = comunidad.getNombre();
                            String direccion = comunidad.getDireccion();
                            String cp = comunidad.getCodigoPostal();
                            String viviendas = "VIVIENDAS: "+ comunidad.getViviendas();

                            tucom_nombre.setText(nombre.toUpperCase(Locale.ROOT));
                            tucom_direccion.setText(direccion);
                            tucom_cp.setText(cp);
                            tucom_viviendas.setText(viviendas);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error al cargar los datos de la comunidad", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error al cargar los datos del usuario", Toast.LENGTH_SHORT);
            }
        });
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