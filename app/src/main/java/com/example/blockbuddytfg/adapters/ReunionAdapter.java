package com.example.blockbuddytfg.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blockbuddytfg.R;
import com.example.blockbuddytfg.RegisterReunionesActivity;
import com.example.blockbuddytfg.entities.Contacto;
import com.example.blockbuddytfg.entities.Reunion;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ReunionAdapter extends FirebaseRecyclerAdapter<Reunion, ReunionAdapter.ReunionViewHolder> {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private Context context;
    String codComunidad, filtro;
    private static TextView cmDescripcion, cmFecha, cmHora;

    public ReunionAdapter(@NonNull FirebaseRecyclerOptions<Reunion> options, DatabaseReference dbRef, FirebaseUser user, Context context, String codComunidad, String filtro) {
        super(options);
        this.dbRef = dbRef;
        this.user = user;
        this.context = context;
        this.codComunidad = codComunidad;
        this.filtro = filtro;
    }

    @Override
    protected void onBindViewHolder(@NonNull ReunionViewHolder holder, int position, @NonNull Reunion model) {
        if(filtro.equals("usuario")){
           holder.bind(model);
        } else {
            holder.bind(model);
            holder.itemView.setOnClickListener(view -> mostrarDialogo(model));
        }
    }

    @NonNull
    @Override
    public ReunionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reunionescard, parent, false);
        return new ReunionViewHolder(view);
    }

    public static class ReunionViewHolder extends RecyclerView.ViewHolder {

        public ReunionViewHolder(@NonNull View itemView) {
            super(itemView);
            cmDescripcion = itemView.findViewById(R.id.reunion_descripcion);
            cmFecha = itemView.findViewById(R.id.reunion_fecha);
            cmHora = itemView.findViewById(R.id.reunion_hora);

        }

        public void bind(Reunion reunion) {
            cmDescripcion.setText(reunion.getDescripcion());
            cmFecha.setText(reunion.getFecha());
            cmHora.setText(reunion.getHora());
        }
    }

    private void mostrarDialogo(Reunion reunion) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Selecciona una opción:");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Código para editar la reunion
                Intent intent = new Intent(context, RegisterReunionesActivity.class);
                intent.putExtra("editar", true);
                intent.putExtra("reunion", reunion);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarReunion(reunion);
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

    private void eliminarReunion(Reunion reunion) {
        dbRef.child(codComunidad+"_reunion_"+cmFecha.getText().toString().replace("/","")).removeValue();
    }
}
