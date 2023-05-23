package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
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

import com.bumptech.glide.Glide;
import com.example.blockbuddytfg.entities.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AjustesPerfil extends AppCompatActivity {
    Toolbar toolbar;
    TextView ajustes_nombre_titulo , ajustes_apellido_titulo, cambiarContraseña;
    CircleImageView ajustes_imagen;
    MaterialButton r_btnupdate;
    EditText etNombre, etTelefono, etEmail;
    TextInputLayout  tilNombre, tilTelefono, tilEmail;
    String puerta, codComunidad, piso, categoria, imagen, uid;
    FirebaseUser user;
    DatabaseReference ref;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    StorageReference mStorageRef;
    ArrayList<String> incidencias;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ajustes_perfil);
        inicializarHooks();
        validarCamposRegistro();
        setToolbar(toolbar);
        recogerDatosFirebase(ref);
        actualizarDatos(ref);
        cambiarContraseña();
        cambiarImagenPerfil();

    }

    private void cambiarImagenPerfil(){
        ajustes_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceSelection();
            }
        });
    }

    private void showImageSourceSelection() {
        String[] opciones = {"Tomar foto", "Seleccionar de la galería"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Seleccionar imagen")
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                openCamera();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    // La foto fue tomada con la cámara
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    mImageUri = getImageUri(this, bitmap);
                    Glide.with(this)
                            .load(mImageUri)
                            .into(ajustes_imagen);
                    subirImagen();
                    break;
                case REQUEST_IMAGE_PICK:
                    // La imagen fue seleccionada de la galería
                    if (data != null && data.getData() != null) {
                        mImageUri = data.getData();
                        Glide.with(this)
                                .load(mImageUri)
                                .into(ajustes_imagen);
                        subirImagen();
                    }
                    break;
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // Subir imagen a Firebase Storage y actualizar la URL en la base de datos
    private void subirImagen() {
        if (mImageUri != null) {
            // crear una referencia a la carpeta de imagenes
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + ".jpg");

            // subir la imagen a Firebase Storage
            try {
                File imageFile = createImageFile();
                InputStream inputStream = getContentResolver().openInputStream(mImageUri);
                OutputStream outputStream = new FileOutputStream(imageFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
                Uri file = Uri.fromFile(imageFile);
                fileReference.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the URL of the uploaded file
                                Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                                downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        // Update the URL in Realtime Database
                                        ref.child("imagen").setValue(imageUrl)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(AjustesPerfil.this, "Imagen subida correctamente", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AjustesPerfil.this, "Error al subir la imagen", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AjustesPerfil.this, "Error al subirla imagen", Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_LONG).show();
        }
    }

    // Crear un archivo temporal para almacenar la imagen
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
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
                    if(etCampo.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etTelefono.getText().toString().isEmpty()){
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
                    String correo = user.getEmail();
                    incidencias = usuario.getIncidencias();
                    puerta = usuario.getPuerta();
                    codComunidad = usuario.getCodComunidad();
                    piso = usuario.getPiso();
                    categoria = usuario.getCategoria();
                    imagen = usuario.getImagen();

                    ajustes_nombre_titulo.setText(nombre.substring(0, nombre.indexOf(" ")).toUpperCase());
                    ajustes_apellido_titulo.setText(nombre.substring(nombre.indexOf(" ")+1).toUpperCase());
                    Glide.with(AjustesPerfil.this)
                            .load(imagen)
                            .into(ajustes_imagen);

                    etNombre.setText(nombre);
                    etTelefono.setText(telefono);
                    etEmail.setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AjustesPerfil.this, "Error al recuperar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cambiarContraseña() {
        cambiarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostramos co Inflate la actvidad de Recordar Password.
                View v = LayoutInflater.from(AjustesPerfil.this).inflate(R.layout.activity_recordar_password, null);

                // Creamos un AlertDialog, con título, los botones de enviar y cancelar y la vista de la activity recordar password.
                new MaterialAlertDialogBuilder(AjustesPerfil.this, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("Recuperar Contraseña")
                        .setView(v)
                        .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                            // Le añadimos la funcionalidad OnClick al botón de enviar.
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();

                                EditText correoRecovery = v.findViewById(R.id.alert_rp_prompt_correo_EditText);
                                String correoRecuperacion = correoRecovery.getText().toString();
                                // Si cumple alguna de las condicione, cierra el Alert muestra un mensaje de error, si no las cumple, envía el correo de recuperación.
                                if (correoRecuperacion.isEmpty() || !correoRecuperacion.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") || !correoRecuperacion.equals(user.getEmail())) {
                                    progressDialog.hide();
                                    Toast.makeText(AjustesPerfil.this, "El correo no es valido", Toast.LENGTH_LONG).show();
                                } /*else if () {
                                progressDialog.hide();
                                Toast.makeText(login.this, "Correo no validado", Toast.LENGTH_SHORT).show();
                            }*/ else {
                                    // Enviamos el correo de recuperación a través de firebase.
                                    mAuth.sendPasswordResetEmail(correoRecuperacion).addOnCompleteListener((task) -> {
                                        progressDialog.hide();
                                        if (task.isSuccessful()) {
                                            // Notifica al usuario que se ha enviado un correo electrónico de verificación a la nueva dirección de correo electrónico
                                            Toast.makeText(AjustesPerfil.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                                            // Cierra la sesión del usuario
                                            mAuth.signOut();
                                            // Redirige al usuario a la pantalla de inicio de sesión
                                            Intent intent = new Intent(AjustesPerfil.this, LoginRegisterActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(AjustesPerfil.this, "Cuenta no registrada", Toast.LENGTH_SHORT).show();
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
    }

    private void actualizarDatos(DatabaseReference ref) {
        r_btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupera los nuevos valores de los campos
                String nombre = etNombre.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String correo = etEmail.getText().toString().trim();

                // Actualiza los valores en la base de datos
                Usuario usuario = new Usuario(nombre,telefono,puerta,codComunidad, piso, categoria, imagen,incidencias);
                ref.setValue(usuario);

                if (user != null) {
                    String email = user.getEmail();
                    //Correo electronico Update
                    if (email != null && !email.equals(correo)) {
                        mAuth.fetchSignInMethodsForEmail(correo).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()) {
                                    SignInMethodQueryResult result = task.getResult();
                                    if (result != null && result.getSignInMethods() != null && result.getSignInMethods().size() > 0) {
                                        // If the new email is already in use, show an error message to the user
                                        Toast.makeText(AjustesPerfil.this, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If the new email is not in use, update the email in the Firebase Auth and send verification email
                                        user.updateEmail(correo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // If the email was updated successfully, get the updated user and send verification email to the new email
                                                FirebaseUser updatedUser = mAuth.getCurrentUser();
                                                if (updatedUser != null) {
                                                    updatedUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Notify the user that a verification email has been sent to the new email address
                                                                Toast.makeText(AjustesPerfil.this, "Correo de verificación enviado", Toast.LENGTH_SHORT).show();
                                                                // Sign out the user
                                                                mAuth.signOut();
                                                                // Redirect the user to the login screen only if the email was updated
                                                                Intent intent = new Intent(AjustesPerfil.this, LoginRegisterActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                            } else {
                                                                // Show an error message to the user
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
                                                // Show an error message to the user
                                                Toast.makeText(AjustesPerfil.this, "No se pudo actualizar el correo electrónico", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } else {
                                    // Show an error message to the user
                                    String error = task.getException().getMessage();
                                    Toast.makeText(AjustesPerfil.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // If the email has not changed, show a success message and do not redirect to the new activity
                        Toast.makeText(AjustesPerfil.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    }
                }
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
        etTelefono = findViewById(R.id.ajustes_prompt_telefono_EditText);
        tilNombre = findViewById(R.id.ajustes_prompt_nombre);
        tilEmail = findViewById(R.id.ajustes_prompt_correo);
        tilTelefono = findViewById(R.id.ajustes_prompt_telefono);
        r_btnupdate = findViewById(R.id.r_btnupdate);
        cambiarContraseña = findViewById(R.id.ajustes_contraseña_olvidada);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        ajustes_imagen = findViewById(R.id.ajustes_foto_perfil);

        user = getIntent().getParcelableExtra("user");
        uid = user.getUid();
        ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

    }

}