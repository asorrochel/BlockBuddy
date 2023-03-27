package com.example.blockbuddytfg;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                // Mostramos co Inflate la actvidad de Recordar Password.
                View v = LayoutInflater.from(LoginActivity.this).inflate(R.layout.activity_recordar_password, null);

                // Creamos un AlertDialog, con título, los botones de enviar y cancelar y la vista de la activity recordar password.
                new MaterialAlertDialogBuilder(LoginActivity.this, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Recuperar Contraseña")
                        .setView(v)
                        .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                            // Le añadimos la funcionalidad OnClick al botón de enviar.
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();
                                progressDialog.setMessage("Enviando correo ...");
                                EditText correoRecovery = v.findViewById(R.id.alert_rp_prompt_correo_EditText);
                                String correoRecuperacion = correoRecovery.getText().toString();

                                // Si cumple alguna de las condicione, cierra el Alert muestra un mensaje de error, si no las cumple, envía el correo de recuperación.
                                if (correoRecuperacion.isEmpty() || !correoRecuperacion.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                                    progressDialog.hide();
                                    Toast.makeText(LoginActivity.this, "El correo no es valido", Toast.LENGTH_LONG).show();
                                } /*else if () {
                                progressDialog.hide();
                                Toast.makeText(login.this, "Correo no validado", Toast.LENGTH_SHORT).show();
                            }*/ else {
                                    // Enviamos el correo de recuperación a través de firebase.
                                    firebaseAuth.sendPasswordResetEmail(correoRecuperacion).addOnCompleteListener((task) -> {
                                        progressDialog.hide();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Cuenta no registrada", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            // Le añadimos la funcionalidad OnClick al botón de cancelar, que nos cerrará el Alert.
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        //Boton de login
        //Login con firebase
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setMessage("Iniciando sesión...");
                String correo = etCorreoL.getText().toString();
                String contraseña = etContraseñaL.getText().toString();
                if (correo.length() == 0 || contraseña.length() == 0) {
                    Toast.makeText(LoginActivity.this, "Correo o contraseña no válidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar si es un administrador
                if (correo.equals("admin") && contraseña.equals("admin")) {
                    Intent intent = new Intent(LoginActivity.this, MainAdminActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(correo,contraseña).addOnCompleteListener((task) ->{
                    progressDialog.show();
                    progressDialog.setMessage("Iniciando sesión...");
                    if(task.isSuccessful()){
                        if(firebaseAuth.getCurrentUser().isEmailVerified()){
                            //Obtenemos el usuario autenticado de firebase authenticacion
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            //Obtenemos el uid del usuario autenticado
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            //Obtenemos la referencia de la base de datos
                            //usuario
                            DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
                            DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference("Administradores").child(uid);

                            //Comprobamos si el usuario es administrador
                            adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        //Obtenemos los datos del usuario
                                        Administrador administrador = dataSnapshot.getValue(Administrador.class);
                                       Intent intent = new Intent(LoginActivity.this, MainAdministradorActivity.class);
                                        intent.putExtra("user", user);
                                        intent.putExtra("uid", uid);

                                       startActivity(intent);
                                       progressDialog.dismiss();
                                       finish();
                                    } else {
                                        //Obtenemos los datos del usuario
                                        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                progressDialog.dismiss();
                                                if (snapshot.exists()) {
                                                    //Obtenemos los datos del usuario
                                                    Usuario usuario = snapshot.getValue(Usuario.class);
                                                    //Pasar los datos del usuario a la siguiente activity
                                                    Intent intent = new Intent(LoginActivity.this, MainUserActivity.class);
                                                    intent.putExtra("user", user);
                                                    intent.putExtra("uid", uid);
                                                    intent.putExtra("codCom", usuario.getCodComunidad());
                                                    intent.putExtra("nombre", usuario.getNombre());
                                                    intent.putExtra("telefono", usuario.getTelefono());
                                                    intent.putExtra("piso", usuario.getPiso());
                                                    intent.putExtra("puerta", usuario.getPuerta());
                                                    intent.putExtra(("categoria"), usuario.getCategoria());
                                                    intent.putExtra("imagen", usuario.getImagen());

                                                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("correo", correo);
                                                    editor.putString("contraseña", contraseña);
                                                    editor.apply();

                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Error al verificar si el usuario es administrador", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Correo no verificado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
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
                if(!editable.toString().isEmpty() && editable.toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$") || editable.toString().equals("admin")) {
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
                if(!editable.toString().isEmpty() && editable.toString().matches(regex) || editable.toString().equals("admin")) {
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