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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //Variables
    Toolbar toolbar;
    TextView textIniciarSesion;
    MaterialButton btnRegistrar;
    EditText etNombre, etCorreo, etTelefono, etPuerta, etPiso, etCodCom, etContraseña;
    TextInputLayout tilNombre, tilCorreo, tilTelefono, tilPuerta, tilPiso, tilCodCom, tilContraseña;

    private DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        inicializarHooks();
        setToolbar(toolbar);
        //Setter por defecto del botón de registro a desactivado.
        cambiarEstadoBoton(btnRegistrar,false);
        validarCamposRegistro();

        //Boton para cambiar a la activity de login
        textIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Boton de registro donde escribimos el usuario en la base de datos
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String correo = etCorreo.getText().toString();
                String contraseña = etContraseña.getText().toString();

              firebaseAuth.createUserWithEmailAndPassword(correo,contraseña).addOnCompleteListener((task -> {
                  progressDialog.hide();
                  if(task.isSuccessful()){
                      firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                  Toast.makeText(RegisterActivity.this, "Registro Completado, Verifique su Correo", Toast.LENGTH_LONG).show();
                                  //creamos el usuario con los datos introducidos en el registro
                                  Usuario usuario = new Usuario(
                                          etNombre.getText().toString(),
                                          etCorreo.getText().toString(),
                                          etTelefono.getText().toString(),
                                          etPuerta.getText().toString(),
                                          etCodCom.getText().toString(),
                                          etContraseña.getText().toString(),
                                          etPiso.getText().toString(),
                                          "usuario",
                                          null);
                                  //escribimos el usuario en la base de datos
                                  mDatabase.child("Usuarios").child(firebaseAuth.getUid()).setValue(usuario);
                              } else {
                                  Toast.makeText(RegisterActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                              }
                          }
                      });
                      Intent intent = new Intent(RegisterActivity.this, LoginRegisterActivity.class);
                      startActivity(intent);
                  } else {
                      Toast.makeText(RegisterActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                  }
              }));
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
                    if(etCampo.getText().toString().isEmpty() || etContraseña.getText().toString().isEmpty() || etCorreo.getText().toString().isEmpty() || etTelefono.getText().toString().isEmpty() || etPuerta.getText().toString().isEmpty() || etPiso.getText().toString().isEmpty() || etCodCom.getText().toString().isEmpty()){
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

    // Método que comprueba el campo de contraseña.
    private void validarContraseña() {
        etContraseña.addTextChangedListener(new TextWatcher() {
            // Declaramos unas variables para comprobación de restricciones.
            boolean condEmail = etCorreo.getText().toString().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
            boolean condContraseña = etContraseña.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$");

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
                 * - Si el campo no está vacío y cumple con la expersión regular, comprobamos el estado de los otros campos,
                 * si no cumple la condición, mostramos el error y desactivamos el botón.
                 */
                if(!editable.toString().isEmpty() && editable.toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$")) {
                    if(etNombre.getText().toString().isEmpty() || etCorreo.getText().toString().isEmpty() || etTelefono.getText().toString().isEmpty() || etPuerta.getText().toString().isEmpty() || etPiso.getText().toString().isEmpty() || etCodCom.getText().toString().isEmpty()){
                        tilContraseña.setError(null);
                        cambiarEstadoBoton(btnRegistrar,false);
                    }
                    else if (!condEmail && condContraseña){
                        tilCorreo.setError("Correo no válido");
                        tilContraseña.setError(null);
                        cambiarEstadoBoton(btnRegistrar,false);
                    } else {
                        tilContraseña.setError(null);
                        cambiarEstadoBoton(btnRegistrar,true);
                    }
                }else {
                    tilContraseña.setError("Min 8 Max 15 | 1 Mayuscula | 1 Minuscula | 1 Numero | 1 Caracter especial @#$%^&+=");
                    cambiarEstadoBoton(btnRegistrar,false);
                }
                // - Si el campo excede el número máximo de caracteres, mostramos el error al usuario.
                if(editable.length() > 15) {
                    tilContraseña.setError("Maximo caracteres permitidos");
                }
            }
        });
    }

    // Método para cambiar el estado del botón de registro.
    private void cambiarEstadoBoton(MaterialButton b,boolean estado) {
        b.setEnabled(estado);
        if(b.isEnabled() == false) {
            btnRegistrar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
            btnRegistrar.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            btnRegistrar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            btnRegistrar.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        }
    }

    // Método para inicializar los hooks.
    private void inicializarHooks() {
        toolbar = findViewById(R.id.register_toolbar);
        textIniciarSesion = findViewById(R.id.r_textoIniciarSesion);
        btnRegistrar = findViewById(R.id.r_btnregistro);
        tilNombre = findViewById(R.id.login_prompt_nombre);
        etNombre = findViewById(R.id.login_prompt_nombre_EditText);
        tilCorreo = findViewById(R.id.login_prompt_correo);
        etCorreo = findViewById(R.id.login_prompt_correo_EditText);
        tilTelefono = findViewById(R.id.login_prompt_telefono);
        etTelefono = findViewById(R.id.login_prompt_telefono_EditText);
        tilPuerta = findViewById(R.id.login_prompt_puerta);
        etPuerta = findViewById(R.id.login_prompt_puerta_EditText);
        tilPiso = findViewById(R.id.login_prompt_piso);
        etPiso = findViewById(R.id.login_prompt_piso_EditText);
        tilCodCom = findViewById(R.id.login_prompt_codCom);
        etCodCom = findViewById(R.id.login_prompt_codCom_EditText);
        tilContraseña = findViewById(R.id.login_prompt_contraseña);
        etContraseña = findViewById(R.id.login_prompt_contraseña_EditText);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
    }

    // Método para validar los campos del registro.
    private void validarCamposRegistro(){
        validarCampo(etNombre,"[a-zA-ZáéíóúÁÉÍÓÚS\\s]{1,35}",tilNombre,"Solo caracteres alfabéticos");
        validarCampo(etCorreo,"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",tilCorreo,"Correo no válido");
        validarContraseña();
        validarCampo(etTelefono,"^[0-9]{9}$",tilTelefono,"Teléfono no válido");
        validarCampo(etPiso,"^\\d{1,2}$",tilPiso,"Piso no válido");
        validarCampo(etPuerta,"^[a-zA-Z]+$",tilPuerta,"Puerta no válida");
        validarCampo(etCodCom,"^[0-9]{5}[A-Z]$",tilCodCom,"Código no válido");
    }
}