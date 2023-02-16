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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Variables.
    Toolbar toolbar;
    TextView textRegistrate, textContraseñaOlvidada;
    MaterialButton btnLogin;
    FirebaseAuth firebaseAuth;
    EditText etCorreoL ,etContraseñaL;
    TextInputLayout tilCorreoL, tilContraseñaL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ocultamos la barra de estado.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        inicializarHooks();

        setToolbar(toolbar);
        validarCampos();

        //Boton ir activity de registro
        textRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Boton de contraseña olvidada
        textContraseñaOlvidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        //Boton de login
        //Login con firebase
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String correo = etCorreoL.getText().toString();
                String contraseña = etContraseñaL.getText().toString();
                if (correo.length() == 0 || contraseña.length() == 0) {
                    Toast.makeText(LoginActivity.this, "Correo o contraseña no válidos", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(correo,contraseña).addOnCompleteListener((task) ->{
                    progressDialog.hide();
                    if(task.isSuccessful()){
                        if(firebaseAuth.getCurrentUser().isEmailVerified()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Correo no verificado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Correo o contraseña no válidos", Toast.LENGTH_SHORT).show();
                    }
                });
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
     * Método que cierra la activity al pulsar sobre la flecha de retroceso.
     * @return - True, si se ha pulsado sobre la flecha de retroceso.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Método usado para cerrar el teclado al pulsar sobre otro lado de la pantalla.
     * @param event - Objeto utilizado para informar eventos de movimiento.
     * @return - True, si la vista es distinta de null, False si la View es null.
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

    private void inicializarHooks(){
        toolbar = findViewById(R.id.register_toolbar);
        textRegistrate = findViewById(R.id.r_textoIniciarSesion);
        textContraseñaOlvidada = findViewById(R.id.Llogin_contraseña_olvidada);
        btnLogin = findViewById(R.id.r_btnregistro);
        firebaseAuth = FirebaseAuth.getInstance();
        etContraseñaL = findViewById(R.id.Llogin_prompt_contraseña_EditText);
        etCorreoL = findViewById(R.id.Llogin_prompt_correo_EditText);
        tilContraseñaL = findViewById(R.id.Llogin_prompt_contraseña);
        tilCorreoL = findViewById(R.id.Llogin_prompt_correo);
    }

    // Método que comprueba el campo de contraseña.
    private void validarContraseña() {
        etContraseñaL.addTextChangedListener(new TextWatcher() {
            // Declaramos unas variables para comprobación de restricciones.
            boolean condEmail = etCorreoL.getText().toString().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
            boolean condContraseña = etContraseñaL.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$");

            // Este método comprueba el EditText antes de que cambie su valor.
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                // - Si el campo está vacío, desactiva el botón de registro y, si no lo está, activa el botón.
                if(s.toString().isEmpty()) {
                    cambiarEstadoBoton(btnLogin,false);
                } else {
                    cambiarEstadoBoton(btnLogin,true);
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
                    if(etContraseñaL.getText().toString().isEmpty()){
                        tilContraseñaL.setError("Contraseña no válida");
                        cambiarEstadoBoton(btnLogin,false);
                    }
                    else if (!condEmail && condContraseña){
                        tilCorreoL.setError("Correo no válido");
                        tilContraseñaL.setError(null);
                        cambiarEstadoBoton(btnLogin,false);
                    } else {
                        tilContraseñaL.setError(null);
                        cambiarEstadoBoton(btnLogin,true);
                    }
                }else {
                    tilContraseñaL.setError("Contraseña no válida");
                    cambiarEstadoBoton(btnLogin,false);
                }
                // - Si el campo excede el número máximo de caracteres, mostramos el error al usuario.
                if(editable.length() > 15) {
                    tilContraseñaL.setError("Maximo caracteres permitidos");
                }
            }
        });
    }
    private void validarCampo(EditText etCampo,String regex, TextInputLayout tilCampo, String error) {
        etCampo.addTextChangedListener(new TextWatcher() {
            // Este método comprueba el EditText antes de que cambie su valor.
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                // - Si el campo está vacío, desactiva el botón de registro y, si no lo está, activa el botón.
                if(s.toString().isEmpty()) {
                    cambiarEstadoBoton(btnLogin,false);
                } else {
                    cambiarEstadoBoton(btnLogin,true);
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
                    if(etCampo.getText().toString().isEmpty() || etContraseñaL.getText().toString().isEmpty()){
                        tilCampo.setError(null);
                        cambiarEstadoBoton(btnLogin,false);
                    } else {
                        tilCampo.setError(null);
                        cambiarEstadoBoton(btnLogin,true);
                    }
                }else {
                    tilCampo.setError(error);
                    cambiarEstadoBoton(btnLogin,false);
                }
                // - Si el campo excede el número máximo de caracteres, mostramos el error al usuario.
                if(editable.length() > 35) {
                    tilCampo.setError("Maximo caracteres");
                }
            }
        });
    }

    private void validarCampos(){
        validarCampo(etCorreoL,"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",tilCorreoL,"Correo no válido");
        validarContraseña();
    }
    // Método para cambiar el estado del botón de registro.
    private void cambiarEstadoBoton(MaterialButton b,boolean estado) {
        b.setEnabled(estado);
        if(b.isEnabled() == false) {
            btnLogin.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange3)));
            btnLogin.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            btnLogin.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            btnLogin.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        }
    }
}