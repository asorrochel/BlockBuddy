package com.example.blockbuddytfg;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Incidencia;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class RegistrarIncidenciaActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialButton btnRegistrar, addFoto;
    EditText etNombre, etDescripcion;
    TextInputLayout tilNombre, tilDescripcion;
    DatabaseReference mDatabase;
    FirebaseUser user;
    String codComunidad,imageUrl, usuarioNombre;
    StorageReference mStorageRef;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registrar_incidencia);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        IncializarHooks();
        setToolbar(toolbar);
        cambiarEstadoBoton(btnRegistrar,false);
        validarCamposRegistro();
        addImagenIncidencia();

        mDatabase.child("Usuarios").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                usuarioNombre = usuario.getNombre();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Boton de registro donde escribimos la comunidad en la base de datos
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setMessage("Registrando comunidad...");
                if(imageUrl == null) {
                    imageUrl = "https://www.hogarmania.com/archivos/201303/bombilla-rota-xl-1280x720x80xX.jpg";
                }

                Incidencia incidencia = new Incidencia(
                        etNombre.getText().toString(),
                        etDescripcion.getText().toString(),
                        LocalDateTime.now().toString(),
                        imageUrl,
                        user.getUid(),
                        usuarioNombre,
                        codComunidad,
                        "activa",
                        false,
                        codComunidad+"_"+false,
                        codComunidad+"_"+"activa"
                );

                //añade la incidencia a la base de datos
                mDatabase.child("Incidencias").child(LocalDateTime.now().toString().replaceAll("[-:.]", "")).setValue(incidencia).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()) {
                            //añade esa incidencia al arrayList del usuario
                            mDatabase.child("Usuarios").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ArrayList<String> incidencias;
                                    ArrayList<String> incidencias2;
                                    Usuario usuario = snapshot.getValue(Usuario.class);
                                    if(usuario.getIncidencias() == null){
                                        incidencias = new ArrayList<>();
                                        incidencias.add(incidencia.getFecha().replaceAll("[-:.]", ""));
                                        usuario.setIncidencias(incidencias);
                                    } else {
                                        incidencias2 = usuario.getIncidencias();
                                        incidencias2.add(incidencia.getFecha().replaceAll("[-:.]", ""));
                                        usuario.setIncidencias(incidencias2);
                                    }
                                    mDatabase.child("Usuarios").child(user.getUid()).setValue(usuario);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.hide();
                                    Toast.makeText(RegistrarIncidenciaActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(RegistrarIncidenciaActivity.this, "Registro Completado", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegistrarIncidenciaActivity.this, IncidenciasActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegistrarIncidenciaActivity.this, "Error al realizar el registro", Toast.LENGTH_SHORT).show();
                        }
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
    private void validarCampo(EditText etCampo, String regex, TextInputLayout tilCampo, String error) {
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
                    if(etCampo.getText().toString().isEmpty()){
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
    private void cambiarEstadoBoton(MaterialButton b, boolean estado) {
        b.setEnabled(estado);
        if(b.isEnabled() == false) {
            b.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
            b.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        } else {
            b.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            b.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        }
    }

    private void validarCamposRegistro(){
        validarCampo(etNombre, "^\\w+$", tilNombre, "Solo caracteres alfanuméricos");
    }

    private void addImagenIncidencia(){
        addFoto.setOnClickListener(new View.OnClickListener() {
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
                    /*Glide.with(this)
                            .load(mImageUri)
                            .into(ajustes_imagen);*/
                    subirImagen();
                    break;
                case REQUEST_IMAGE_PICK:
                    // La imagen fue seleccionada de la galería
                    if (data != null && data.getData() != null) {
                        mImageUri = data.getData();
                        /*
                        Glide.with(this)
                                .load(mImageUri)
                                .into(ajustes_imagen);*/
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
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrl = uri.toString();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegistrarIncidenciaActivity.this, "Error al subirla imagen store", Toast.LENGTH_LONG).show();
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

    private void IncializarHooks() {
        toolbar = findViewById(R.id.cr_in_toolbar);
        btnRegistrar = findViewById(R.id.cr_in_btnregistro);
        etNombre = findViewById(R.id.cr_in_prompt_nombre_EditText);
        etDescripcion = findViewById(R.id.cr_in_prompt_descripcion_EditText);
        tilDescripcion = findViewById(R.id.cr_in_prompt_descripcion);
        tilNombre = findViewById(R.id.cr_in_prompt_nombre);
        codComunidad = getIntent().getStringExtra("codCom");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addFoto = findViewById(R.id.cr_in_addImagen);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

    }

}