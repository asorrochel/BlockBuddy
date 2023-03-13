package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.blockbuddytfg.entities.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainUserActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView mainUsuario_textView, mainUsuario_Welcome;
    FirebaseUser user;
    DatabaseReference ref;
    String uid, correoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mainuser);

        inicializarHooks();
        recogerDatosFirebase(ref);

        //ir a ajustes
        mainUsuario_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUserActivity.this, AjustesPerfil.class);
                intent.putExtra("user", user);
                intent.putExtra("correo", correoActual);
                startActivity(intent);
            }
        });
    }

    /**
     * Método que cierra el teclado cuando se pulsa fuera de un EditText.
     * @param event - Evento que se produce al pulsar fuera de un EditText.
     * @return - True, si se ha pulsado fuera de un EditText, False si no se ha pulsado.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Guardamos la vista seleccionada.
        View view = this.getCurrentFocus();

        // Si no es null (Tenemos una vista seleccionada), cerramos el teclado.
        if (view != null) {
            Context context = getApplicationContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            return true;
        } else {
            return false;
        }
    }

    private void recogerDatosFirebase(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    String nombre = usuario.getNombre();
                    mainUsuario_Welcome.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Método para inicializar los hooks.
    private void inicializarHooks() {
        mainUsuario_textView = findViewById(R.id.mainUsuario_textView);
        mainUsuario_Welcome = findViewById(R.id.mainUsuario_textView_Welcome);
        uid = getIntent().getStringExtra("uid");
        correoActual = getIntent().getStringExtra("correo");
        user = getIntent().getParcelableExtra("user");
        ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
    }
}