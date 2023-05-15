package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Contacto;
import com.example.blockbuddytfg.entities.Reunion;
import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterReunionesActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialButton btnRegistrar;
    EditText etDescripcion, etFecha;
    TextView tituloReunion;
    TextInputLayout tilDescripcion, tilFecha;
    String codigoComunidad, codComAdmin;
    Reunion reunionEditar = new Reunion();
    private DatabaseReference mDatabase,mUsuarios, mAdmin;
    boolean editar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_reuniones);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        inicializarHooks();

        setToolbar(toolbar);
        recogerDatosAdminoPresidente(mUsuarios);
        //Setter por defecto del botón de registro a desactivado.
        cambiarEstadoBoton(btnRegistrar, false);
        validarCamposRegistro();

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterReunionesActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                                etFecha.setText(fechaSeleccionada);
                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });

        editarReunionOCrear(editar);
        //registrar reunion
        if(editar == false) {
            btnRegistrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    progressDialog.setMessage("Registrando reunión...");

                    Reunion reunion = new Reunion(
                            etDescripcion.getText().toString(),
                            etFecha.getText().toString(),
                            codigoComunidad
                    );
                    //añade esa reunion a la base de datos
                    mDatabase.child("Reuniones").child(codigoComunidad + "_reunion_" + etFecha.getText().toString().replace("/","")).setValue(reunion).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.hide();
                            if (task.isSuccessful()) {
                                //añade el contacto a la lista de reuniones de la comunidad
                                mDatabase.child("Comunidades").child(codigoComunidad).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> reuniones;
                                        Comunidad comunidad = snapshot.getValue(Comunidad.class);
                                        if (comunidad.getReuniones() == null) {
                                            reuniones = new ArrayList<>();
                                            reuniones.add(codigoComunidad + "_reunion_" + etFecha.getText().toString().replace("/", ""));
                                        } else {
                                            reuniones = comunidad.getReuniones();
                                            reuniones.add(codigoComunidad + "_reunion_" + etFecha.getText().toString().replace("/", ""));
                                        }
                                        comunidad.setReuniones(reuniones);
                                        mDatabase.child("Comunidades").child(codigoComunidad).setValue(comunidad);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.hide();
                                        Toast.makeText(RegisterReunionesActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(RegisterReunionesActivity.this, "Registro Completado", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterReunionesActivity.this, TusReunionesActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterReunionesActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
        //editar reunion
        else {
            btnRegistrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    progressDialog.setMessage("Editando reunión...");

                    Reunion reunion = new Reunion(
                            etDescripcion.getText().toString(),
                            etFecha.getText().toString(),
                            reunionEditar.getCodComunidad()
                    );

                    String existingKey = reunionEditar.getCodComunidad() + "_reunion_" + reunionEditar.getFecha().replace("/", "");
                    mDatabase.child("Reuniones").child(existingKey).removeValue();

                    //añade esa reunion a la base de datos
                    mDatabase.child("Reuniones").child(reunionEditar.getCodComunidad() + "_reunion_" + etFecha.getText().toString().replace("/","")).setValue(reunion).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.hide();
                            if (task.isSuccessful()) {
                                //añade el contacto a la lista de reuniones de la comunidad
                                mDatabase.child("Comunidades").child(reunionEditar.getCodComunidad()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> reuniones;
                                        Comunidad comunidad = snapshot.getValue(Comunidad.class);
                                        if (comunidad.getReuniones() == null) {
                                            reuniones = new ArrayList<>();
                                            reuniones.add(reunionEditar.getCodComunidad() + "_reunion_" + etFecha.getText().toString().replace("/", ""));
                                        } else {
                                            reuniones = comunidad.getReuniones();
                                            reuniones.remove(existingKey);
                                            reuniones.add(reunionEditar.getCodComunidad() + "_reunion_" + etFecha.getText().toString().replace("/", ""));
                                        }
                                        comunidad.setReuniones(reuniones);
                                        mDatabase.child("Comunidades").child(reunionEditar.getCodComunidad()).setValue(comunidad);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.hide();
                                        Toast.makeText(RegisterReunionesActivity.this, "Error al realizar la modificación", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(RegisterReunionesActivity.this, "Modificación Completado", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterReunionesActivity.this, TusReunionesActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterReunionesActivity.this, "Error al realizar la modificación", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Método que añade a la activity un Toolbar.
     *
     * @param toolbar - ToolBar que queremos añadir a la activity.
     */
    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        // Añadimos la flecha de retroceso.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    /**
     * Método que añade la funcionalidad de la flecha de retroceso.
     *
     * @return - True, si se ha pulsado la flecha de retroceso, False si no se ha pulsado.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /**
     * Método que cierra el teclado cuando se pulsa fuera de un EditText.
     *
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
    /**
     * Metodo que comprueba que los campos de registro cumplen una validación.
     *
     * @param etCampo  - EditText que queremos validar.
     * @param regex    - Expresión regular
     * @param tilCampo - TextInputLayout que queremos validar.
     * @param error    - Mensaje de error que queremos mostrar.
     */
    private void validarCampo(EditText etCampo, String regex, TextInputLayout tilCampo, String error) {
        etCampo.addTextChangedListener(new TextWatcher() {
            // Este método comprueba el EditText antes de que cambie su valor.
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                // - Si el campo está vacío, desactiva el botón de registro y, si no lo está, activa el botón.
                if (s.toString().isEmpty()) {
                    cambiarEstadoBoton(btnRegistrar, false);
                } else {
                    cambiarEstadoBoton(btnRegistrar, true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            // Este método comprueba el EditText después de que cambie su valor.
            @Override
            public void afterTextChanged(Editable editable) {
                /*
                 * - Si el campo no está vacío y cumple con la expersión regular (Sólo caracteres alfanuméricos), comprobamos el estado de los otros campos,
                 * si no cumple la condición, mostramos el error y desactivamos el botón.
                 */
                if (!editable.toString().isEmpty() && editable.toString().matches(regex)) {
                    if (etCampo.getText().toString().isEmpty() || etDescripcion.getText().toString().isEmpty() || etFecha.getText().toString().isEmpty()) {
                        tilCampo.setError(null);
                        cambiarEstadoBoton(btnRegistrar, false);
                    } else {
                        tilCampo.setError(null);
                        cambiarEstadoBoton(btnRegistrar, true);
                    }
                } else {
                    tilCampo.setError(error);
                    cambiarEstadoBoton(btnRegistrar, false);
                }
                // - Si el campo excede el número máximo de caracteres, mostramos el error al usuario.
                if (editable.length() > 35) {
                    tilCampo.setError("Maximo caracteres");
                }
            }
        });
    }
    // Método para cambiar el estado del botón de registro.
    private void cambiarEstadoBoton(MaterialButton b, boolean estado) {
        b.setEnabled(estado);
        if (b.isEnabled() == false) {
            b.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            b.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        } else {
            b.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
            b.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        }
    }
    // Método para inicializar los hooks.
    private void inicializarHooks() {
        toolbar = findViewById(R.id.register_reu_toolbar2);
        btnRegistrar = findViewById(R.id.register_reu_btn);
        tilDescripcion = findViewById(R.id.register_reu_prompt_descripcion);
        etDescripcion = findViewById(R.id.register_reu_prompt_nombre_EditText);
        tilFecha = findViewById(R.id.register_reu_prompt_fecha);
        etFecha = findViewById(R.id.register_reu_prompt_fecha_EditText);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAdmin = FirebaseDatabase.getInstance().getReference("Administradores").child(firebaseUser.getUid());
        mUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid());
        codComAdmin = getIntent().getStringExtra("codCom");
        editar = getIntent().getBooleanExtra("editar", false);
        tituloReunion = findViewById(R.id.register_reu_iniciar_sesion);
        reunionEditar = (Reunion) getIntent().getSerializableExtra("reunion");
    }
    // Método para validar los campos del registro.
    private void validarCamposRegistro() {
        validarCampo(etDescripcion, "[a-zA-ZáéíóúÁÉÍÓÚS\\s]{1,35}", tilDescripcion, "Solo caracteres alfabéticos");
    }

    private void editarReunionOCrear(boolean editar){
        if(editar == false) {
            btnRegistrar.setText("Registrar");
        }
        else {
            btnRegistrar.setText("Editar");
            tituloReunion.setText("Editar Reunión");

            etDescripcion.setText(reunionEditar.getDescripcion());
            etFecha.setText(reunionEditar.getFecha());
        }
    }

    private void recogerDatosAdminoPresidente(DatabaseReference mUsuarios) {
        mUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    codigoComunidad = usuario.getCodComunidad();
                } else {
                    mAdmin.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if(administrador != null) {
                                codigoComunidad = codComAdmin;
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
    }
}
