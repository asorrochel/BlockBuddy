package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AjustesPerfil extends AppCompatActivity {
    Toolbar toolbar;
    TextView ajustes_nombre_titulo , ajustes_apellido_titulo;
    MaterialButton r_btnupdate;
    EditText etNombre, etTelefono, etEmail, etPassword;
    TextInputLayout  tilNombre, tilTelefono, tilEmail, tilPassword;
    String puerta, codComunidad, piso, categoria, imagen, uid;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ajustes_perfil);

        inicializarHooks();
        validarCamposRegistro();
        setToolbar(toolbar);
        recogerDatosFirebase(ref);

        // Agrega un OnClickListener al botón
        r_btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupera los nuevos valores de los campos
                String nombre = etNombre.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String correo = etEmail.getText().toString().trim();
                String contraseña = etPassword.getText().toString().trim();

                // Actualiza los valores en la base de datos
                Usuario usuario = new Usuario(nombre, correo, telefono,puerta,codComunidad, contraseña, piso, categoria, imagen);
                ref.setValue(usuario);

                if (user != null) {
                    String email = user.getEmail();
                    //Correo electronico Update
                    if (email != null && !email.equals(correo)) {
                        user.updateEmail(correo).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Si se actualizó correctamente, obtén el usuario actualizado y envía la verificación al nuevo correo electrónico
                                FirebaseUser updatedUser = mAuth.getCurrentUser();
                                if (updatedUser != null) {
                                    updatedUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Notifica al usuario que se ha enviado un correo electrónico de verificación a la nueva dirección de correo electrónico
                                                Toast.makeText(AjustesPerfil.this, "Correo de verificación enviado", Toast.LENGTH_SHORT).show();
                                                // Cierra la sesión del usuario
                                                mAuth.signOut();
                                                // Redirige al usuario a la pantalla de inicio de sesión
                                                Intent intent = new Intent(AjustesPerfil.this, LoginRegisterActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            } else {
                                                // Muestra un mensaje de error al usuario
                                                String error = task.getException().getMessage();
                                                Toast.makeText(AjustesPerfil.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Muestra un mensaje de error al usuario
                                Toast.makeText(AjustesPerfil.this, "No se pudo actualizar el correo electrónico", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (contraseña != null) {
                        user.updatePassword(contraseña).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AjustesPerfil.this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                    // Cierra la sesión del usuario
                                    mAuth.signOut();
                                    // Redirige al usuario a la pantalla de inicio de sesión
                                    Intent intent = new Intent(AjustesPerfil.this, LoginRegisterActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    // Muestra un mensaje de error al usuario
                                    String error = task.getException().getMessage();
                                    Toast.makeText(AjustesPerfil.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
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
     * Metodo que comprueba que los campos de ajustes cumplen una validación.
     * @param etCampo - EditText que queremos validar.
     * @param regex - Expresión regular
     * @param tilCampo - TextInputLayout que queremos validar.
     * @param error - Mensaje de error que queremos mostrar.
     */
    private void validarCampo(EditText etCampo, String regex, TextInputLayout tilCampo, String error) {
        etCampo.addTextChangedListener(new TextWatcher() {
            // Este método comprueba el EditText antes de que cambie su valor.
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                // - Si el campo está vacío, desactiva el botón de registro y, si no lo está, activa el botón.
                if(s.toString().isEmpty()) {
                    cambiarEstadoBoton(r_btnupdate,false);
                } else {
                    cambiarEstadoBoton(r_btnupdate,true);
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
                    if(etCampo.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etTelefono.getText().toString().isEmpty()){
                        tilCampo.setError(null);
                        cambiarEstadoBoton(r_btnupdate,false);
                    } else {
                        tilCampo.setError(null);
                        cambiarEstadoBoton(r_btnupdate,true);
                    }
                }else {
                    tilCampo.setError(error);
                    cambiarEstadoBoton(r_btnupdate,false);
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
        etPassword.addTextChangedListener(new TextWatcher() {
            // Declaramos unas variables para comprobación de restricciones.
            boolean condEmail = etEmail.getText().toString().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
            boolean condContraseña = etPassword.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$");

            // Este método comprueba el EditText antes de que cambie su valor.
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                // - Si el campo está vacío, desactiva el botón de ajustes y, si no lo está, activa el botón.
                if(s.toString().isEmpty()) {
                    cambiarEstadoBoton(r_btnupdate,false);
                } else {
                    cambiarEstadoBoton(r_btnupdate,true);
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
                    if(etNombre.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etTelefono.getText().toString().isEmpty()){
                        tilPassword.setError(null);
                        cambiarEstadoBoton(r_btnupdate,false);
                    }
                    else if (!condEmail && condContraseña){
                        tilEmail.setError("Correo no válido");
                        tilPassword.setError(null);
                        cambiarEstadoBoton(r_btnupdate,false);
                    } else {
                        tilPassword.setError(null);
                        cambiarEstadoBoton(r_btnupdate,true);
                    }
                }else {
                    tilPassword.setError("Min 8 Max 15 | 1 Mayuscula | 1 Minuscula | 1 Numero | 1 Caracter especial @#$%^&+=");
                    cambiarEstadoBoton(r_btnupdate,false);
                }
                // - Si el campo excede el número máximo de caracteres, mostramos el error al usuario.
                if(editable.length() > 15) {
                    tilPassword.setError("Maximo caracteres permitidos");
                }
            }
        });
    }

    // Método para cambiar el estado del botón de ajustes.
    private void cambiarEstadoBoton(MaterialButton b, boolean estado) {
        b.setEnabled(estado);
        if(b.isEnabled() == false) {
            r_btnupdate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
            r_btnupdate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            r_btnupdate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            r_btnupdate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        }
    }

    // Método para validar los campos de ajustes.
    private void validarCamposRegistro(){
        validarCampo(etNombre,"[a-zA-ZáéíóúÁÉÍÓÚS\\s]{1,35}",tilNombre,"Solo caracteres alfabéticos");
        validarCampo(etEmail,"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",tilEmail,"Correo no válido");
        validarContraseña();
        validarCampo(etTelefono,"^[0-9]{9}$",tilTelefono,"Teléfono no válido");
    }

    private void recogerDatosFirebase(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                if(usuario != null) {
                    String nombre = usuario.getNombre();
                    String telefono = usuario.getTelefono();
                    String correo = usuario.getCorreo();
                    String contraseña = usuario.getContraseña();
                    puerta = usuario.getPuerta();
                    codComunidad = usuario.getCodComunidad();
                    piso = usuario.getPiso();
                    categoria = usuario.getCategoria();
                    imagen = usuario.getImagen();

                    ajustes_nombre_titulo.setText(nombre.substring(0, nombre.indexOf(" ")).toUpperCase());
                    ajustes_apellido_titulo.setText(nombre.substring(nombre.indexOf(" ")+1).toUpperCase());
                    etNombre.setText(nombre);
                    etTelefono.setText(telefono);
                    etEmail.setText(correo);
                    etPassword.setText(contraseña);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Método para inicializar los hooks.
    private void inicializarHooks() {
        toolbar = findViewById(R.id.ajustes_toolbar);
        ajustes_nombre_titulo = findViewById(R.id.ajustes_nombre_titulo);
        ajustes_apellido_titulo = findViewById(R.id.ajustes_apellido);
        etNombre = findViewById(R.id.ajustes_prompt_nombre_EditText);
        etEmail = findViewById(R.id.ajustes_prompt_correo_EditText);
        etPassword = findViewById(R.id.ajustes_prompt_contraseña_EditText);
        etTelefono = findViewById(R.id.ajustes_prompt_telefono_EditText);
        tilNombre = findViewById(R.id.ajustes_prompt_nombre);
        tilEmail = findViewById(R.id.ajustes_prompt_correo);
        tilPassword = findViewById(R.id.ajustes_prompt_contraseña);
        tilTelefono = findViewById(R.id.ajustes_prompt_telefono);
        r_btnupdate = findViewById(R.id.r_btnupdate);

        user = getIntent().getParcelableExtra("user");
        uid = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
        mAuth = FirebaseAuth.getInstance();
    }
}