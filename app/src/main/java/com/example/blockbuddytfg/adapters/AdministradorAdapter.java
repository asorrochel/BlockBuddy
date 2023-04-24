package com.example.blockbuddytfg.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.blockbuddytfg.R;
import com.example.blockbuddytfg.entities.Administrador;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class AdministradorAdapter extends FirebaseRecyclerAdapter<Administrador, AdministradorAdapter.AdministradorViewHolder> {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private Context context;

    public AdministradorAdapter(@NonNull FirebaseRecyclerOptions<Administrador> options, DatabaseReference dbRef, FirebaseUser user, Context context) {
        super(options);
        this.dbRef = dbRef;
        this.user = user;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdministradorViewHolder holder, int position, @NonNull Administrador model) {
        holder.bind(model);
        holder.itemView.setOnClickListener(view -> mostrarDialogo(model));
    }

    @NonNull
    @Override
    public AdministradorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admincard, parent, false);
        return new AdministradorViewHolder(view);
    }
    public static class AdministradorViewHolder extends RecyclerView.ViewHolder {

        private TextView cmNombre, cmTelefono;
        private ImageView cmImagen;

        public AdministradorViewHolder(@NonNull View itemView) {
            super(itemView);
            cmNombre = itemView.findViewById(R.id.admin_nombre);
            cmTelefono = itemView.findViewById(R.id.admin_telefono);
            cmImagen = itemView.findViewById(R.id.admin_imagen);
        }

        public void bind(Administrador admin) {
            cmNombre.setText(admin.getNombre());
            cmTelefono.setText(admin.getTelefono());
            Glide.with(itemView.getContext()).load(admin.getImagen()).into(cmImagen);
        }
    }

    private void mostrarDialogo(Administrador admin) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Selecciona una opción:");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Código para editar el administrador
            }
        });
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarAdmin();
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

    private void eliminarAdmin() {
        dbRef.child(user.getUid()).removeValue();
    }

}
