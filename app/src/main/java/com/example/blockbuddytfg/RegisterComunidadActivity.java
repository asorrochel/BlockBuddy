package com.example.blockbuddytfg;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegisterComunidadActivity extends AppCompatActivity {

    //Variables
    Toolbar toolbar;
    MaterialButton btnRegistrar;
    EditText etNombre, etDireccion, etCodCom, etViviendas, etCodPostal;
    TextInputLayout tilNombre, tilDireccion, tilCodCom, tilViviendas, tilCodPostal;

    private DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ArrayList<String> incidencias = new ArrayList<>();
    ArrayList<String> contactos = new ArrayList<>();
    ArrayList<String> reuniones = new ArrayList<>();
    ArrayList<String> documentos = new ArrayList<>();
    ArrayList<String> anuncios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_comunidad);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        inicializarHooks();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setToolbar(toolbar);
        //Setter por defecto del botón de registro a desactivado.
        cambiarEstadoBoton(btnRegistrar,false);
        validarCamposRegistro();

        //Boton de registro donde escribimos la comunidad en la base de datos
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setMessage("Registrando comunidad...");

                String codigoComunidad = etCodCom.getText().toString();

                // Verificar que el código de la comunidad no se haya utilizado previamente
                mDatabase.child("Comunidades").orderByChild("codigoComunidad").equalTo(codigoComunidad).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // El código de la comunidad ya se encuentra registrado
                            progressDialog.hide();
                            Toast.makeText(RegisterComunidadActivity.this, "El código de la comunidad ya se encuentra registrado", Toast.LENGTH_SHORT).show();
                        } else {
                            // El código de la comunidad es válido, se puede registrar la comunidad
                            Comunidad comunidad = new Comunidad(
                                    etNombre.getText().toString(),
                                    etDireccion.getText().toString(),
                                    codigoComunidad,
                                    etViviendas.getText().toString(),
                                    etCodPostal.getText().toString(),
                                    firebaseUser.getUid(),
                                    incidencias,
                                    contactos,
                                    reuniones,
                                    documentos,
                                    anuncios
                                    );
                            mDatabase.child("Comunidades").child(codigoComunidad).setValue(comunidad).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.hide();
                                    if (task.isSuccessful()) {
                                        //añade esa comunidad al arrayList del administrador
                                        mDatabase.child("Administradores").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                ArrayList<String> comunidades;
                                                Administrador administrador = snapshot.getValue(Administrador.class);
                                                if(administrador.getComunidades() == null){
                                                    comunidades = new ArrayList<>();
                                                    comunidades.add(comunidad.getCodigoComunidad());
                                                } else {
                                                    comunidades = administrador.getComunidades();
                                                    comunidades.add(comunidad.getCodigoComunidad());
                                                }
                                                administrador.setComunidades(comunidades);
                                                mDatabase.child("Administradores").child(firebaseUser.getUid()).setValue(administrador);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                progressDialog.hide();
                                                Toast.makeText(RegisterComunidadActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Toast.makeText(RegisterComunidadActivity.this, "Registro Completado", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(RegisterComunidadActivity.this, MainAdministradorActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterComunidadActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.hide();
                        Toast.makeText(RegisterComunidadActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    };

    /**
     * Método que añade a la activity un Toolbar.
     * @param toolbar - ToolBar que queremos añadir a la activity.
     */
    private void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        // Añadimos la flecha de retroceso.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

    /**
     * Metodo que comprueba que los campos de registro cumplen una validación.
     * @param etCampo - EditText que queremos validar.
     * @param regex - Expresión regular
     * @param tilCampo - TextInputLayout que queremos validar.
     * @param error - Mensaje de error que queremos mostrar.
     */
    private void validarCampo(EditText etCampo,String regex, TextInputLayout tilCampo, String error) {
        etCampo.addTextChangedListener(new TextWatcher() {
            // Este método comprueba el EditText antes de que cambie su valor.
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                // - Si el campo está vacío, desactiva el botón de registro y, si no lo está, activa el botón.
                if(s.toString().isEmpty()) {
                    cambiarEstadoBoton(btnRegistrar,false);
                } else {
                    cambiarEstadoBoton(btnRegistrar,true);
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
                if(!editable.toString().isEmpty() && editable.toString().matches(regex)) {
                    if(etCampo.getText().toString().isEmpty() || etNombre.getText().toString().isEmpty() || etDireccion.getText().toString().isEmpty() || etCodCom.getText().toString().isEmpty() || etCodPostal.getText().toString().isEmpty() || etViviendas.getText().toString().isEmpty() || etViviendas.getText().toString().isEmpty()){
                        tilCampo.setError(null);
                        cambiarEstadoBoton(btnRegistrar,false);
                    } else {
                        tilCampo.setError(null);
                        cambiarEstadoBoton(btnRegistrar,true);
                    }
                }else {
                    tilCampo.setError(error);
                    cambiarEstadoBoton(btnRegistrar,false);
                }
                // - Si el campo excede el número máximo de caracteres, mostramos el error al usuario.
                if(editable.length() > 35) {
                    tilCampo.setError("Maximo caracteres");
                }
            }
        });
    }

    // Método para cambiar el estado del botón de registro.
    private void cambiarEstadoBoton(MaterialButton b,boolean estado) {
        b.setEnabled(estado);
        if(b.isEnabled() == false) {
            b.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
            b.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            b.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            b.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        }
    }

    // Método para inicializar los hooks.
    private void inicializarHooks() {
        toolbar = findViewById(R.id.register_com_toolbar2);
        btnRegistrar = findViewById(R.id.register_com_btnregistro2);

        tilNombre = findViewById(R.id.register_com_prompt_nombre);
        etNombre = findViewById(R.id.register_com_prompt_nombre_EditText);

        tilDireccion = findViewById(R.id.register_com_prompt_direccion);
        etDireccion = findViewById(R.id.register_com_prompt_direccion_EditText);

        tilCodCom = findViewById(R.id.register_com_prompt_codComunidad);
        etCodCom = findViewById(R.id.register_com_prompt_codComunidad_EditText);

        tilViviendas = findViewById(R.id.register_com_prompt_viviendas);
        etViviendas = findViewById(R.id.register_com_prompt_viviendas_EditText);

        tilCodPostal = findViewById(R.id.register_com_prompt_codPostal);
        etCodPostal = findViewById(R.id.register_com_prompt_codPostal_EditText);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
    }

    // Método para validar los campos del registro.
    private void validarCamposRegistro(){
        validarCampo(etNombre,"[a-zA-ZáéíóúÁÉÍÓÚS\\s]{1,35}",tilNombre,"Solo caracteres alfabéticos");
        validarCampo(etDireccion,"^[a-zA-Z0-9\\s\\p{P}]{1,35}$",tilDireccion,"Dirección no válida");
        validarCampo(etCodCom,"^[0-9]{5}[A-Z]$",tilCodCom,"Codigo no válido");
        validarCampo(etViviendas,"^[0-9]{1,4}$",tilViviendas,"Viviendas no válidas");
        validarCampo(etCodPostal,"^(?:0[1-9]|[1-4]\\d|5[0-2])\\d{3}$",tilCodPostal,"Codigo postal no válido");
        //añadir resto de validaciones
    }
}