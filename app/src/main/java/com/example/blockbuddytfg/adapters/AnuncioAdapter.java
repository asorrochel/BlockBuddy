package com.example.blockbuddytfg.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blockbuddytfg.R;
import com.example.blockbuddytfg.entities.Anuncio;
import com.example.blockbuddytfg.entities.Reunion;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class AnuncioAdapter extends FirebaseRecyclerAdapter<Anuncio, AnuncioAdapter.AnuncioViewHolder> {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private Context context;
    String codComunidad, filtro;
    private static TextView cmDescripcion, cmTitulo;

    public AnuncioAdapter(@NonNull FirebaseRecyclerOptions<Anuncio> options, DatabaseReference dbRef, FirebaseUser user, Context context, String codComunidad, String filtro) {
        super(options);
        this.dbRef = dbRef;
        this.user = user;
        this.context = context;
        this.codComunidad = codComunidad;
        this.filtro = filtro;
    }

    @Override
    protected void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position, @NonNull Anuncio model) {
        if(filtro.equals("usuario")){
           holder.bind(model);
        } else {
            holder.bind(model);
            holder.itemView.setOnClickListener(view -> mostrarDialogo(model));
        }
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anunciocard, parent, false);
        return new AnuncioViewHolder(view);
    }

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        public AnuncioViewHolder(@NonNull View itemView) {
            super(itemView);
            cmTitulo = itemView.findViewById(R.id.anuncio_titulo);
            cmDescripcion = itemView.findViewById(R.id.anuncio_descripcion);
        }

        public void bind(Anuncio anuncio) {
            cmDescripcion.setText(anuncio.getDescripcion());
            cmTitulo.setText(anuncio.getTitulo());
        }
    }

    private void mostrarDialogo(Anuncio anuncio) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Selecciona una opción:");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Código para editar el anuncio
            }
        });
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarAnuncio(anuncio);
            }
        });
        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void eliminarAnuncio(Anuncio anuncio) {
        dbRef.child(codComunidad+"_anuncio_"+  anuncio.getFecha().replaceAll("[-:.]", "")).removeValue();
    }
}
