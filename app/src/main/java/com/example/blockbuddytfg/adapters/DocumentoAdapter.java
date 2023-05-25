package com.example.blockbuddytfg.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blockbuddytfg.R;
import com.example.blockbuddytfg.RegisterAnunciosActivity;
import com.example.blockbuddytfg.RegisterDocumentosActivity;
import com.example.blockbuddytfg.entities.Documento;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DocumentoAdapter extends FirebaseRecyclerAdapter<Documento, DocumentoAdapter.DocumentoViewHolder> {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private Context context;
    String codComunidad, filtro = "admin";
    private static TextView cmDescripcion, cmTitulo;

    public DocumentoAdapter(@NonNull FirebaseRecyclerOptions<Documento> options, DatabaseReference dbRef, FirebaseUser user, Context context, String codComunidad, String filtro) {
        super(options);
        this.dbRef = dbRef;
        this.user = user;
        this.context = context;
        this.codComunidad = codComunidad;
        this.filtro = filtro;
    }

    @Override
    protected void onBindViewHolder(@NonNull DocumentoViewHolder holder, int position, @NonNull Documento model) {
        if(filtro.equals("usuario")){
           holder.bind(model);
            holder.itemView.setOnClickListener(view -> mostrarDialogoUsuario(model));
        } else {
            holder.bind(model);
            holder.itemView.setOnClickListener(view -> mostrarDialogo(model));
        }
    }

    @NonNull
    @Override
    public DocumentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.documentocard, parent, false);
        return new DocumentoViewHolder(view);
    }

    public static class DocumentoViewHolder extends RecyclerView.ViewHolder {

        public DocumentoViewHolder(@NonNull View itemView) {
            super(itemView);
            cmTitulo = itemView.findViewById(R.id.doc_titulo);
            cmDescripcion = itemView.findViewById(R.id.doc_descripcion);
        }

        public void bind(Documento documento) {
            cmDescripcion.setText(documento.getDescripcion());
            cmTitulo.setText(documento.getTitulo());
        }
    }

private void mostrarDialogoUsuario(Documento documento) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
    builder.setTitle("Selecciona una opción:");
    builder.setPositiveButton("Descargar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Descargar el documento
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("documents").child(documento.getUrl());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    String fileExtension = getFileExtension(documento.getUrl());
                    String mimeType = getMimeTypeFromExtension(fileExtension);
                    String fileName = documento.getTitulo() + fileExtension;

                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                            .setMimeType(mimeType)
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setAllowedOverRoaming(false)
                            .setTitle(documento.getTitulo());
                    downloadManager.enqueue(request);
                }
            });
        }
    });
    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });
    builder.show();
}

private void mostrarDialogo(Documento documento) {
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
    builder.setTitle("Selecciona una opción:");
    builder.setPositiveButton("Descargar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //Download the document
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("documents").child(documento.getUrl());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, documento.getTitulo())
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setAllowedOverRoaming(false)
                            .setTitle(documento.getTitulo());
                    downloadManager.enqueue(request);
                }
            });
        }
    });
    builder.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //Código para editar el documento
            Intent intent = new Intent(context, RegisterDocumentosActivity.class);
            intent.putExtra("editar", true);
            intent.putExtra("documento", documento);
            context.startActivity(intent);
        }
    });

    builder.setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            eliminarDocumento(documento);
        }
    });

    builder.show();
}

    private String getFileExtension(String url) {
        int lastDotPosition = url.lastIndexOf(".");
        if (lastDotPosition != -1) {
            return url.substring(lastDotPosition);
        }
        return "";
    }

    private String getMimeTypeFromExtension(String fileExtension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
    }


    private void eliminarDocumento(Documento documento) {
        dbRef.child(codComunidad+"_documento_"+ documento.getFecha().replaceAll("[-:.]", "")).removeValue();
    }
}
