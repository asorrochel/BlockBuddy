package com.example.blockbuddytfg.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.blockbuddytfg.AjustesPerfil;
import com.example.blockbuddytfg.LoginRegisterActivity;
import com.example.blockbuddytfg.R;
import com.example.blockbuddytfg.RegistrarIncidenciaActivity;
import com.example.blockbuddytfg.RegistroAdminActivity;
import com.example.blockbuddytfg.entities.Administrador;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        builder.setTitle("Quieres borrar el administrador:");
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //eliminarAdmin();
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
/*
    private void eliminarAdmin() {

        //obtener el usuario que coincide con el adminUID
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Administradores");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Administrador admin = snapshot.getValue(Administrador.class);
                    if (admin != null && admin.getUid().equals(adminUID)) {
                        // Eliminar cuenta
                        user.delete(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Eliminar datos de la base de datos
                                String uidUsuario = user.getUid();
                                dbRef.child(user.getUid()).removeValue();
                                //actualizar el usuarNombre en las incidencias del usuario
                                DatabaseReference refIncidencias = FirebaseDatabase.getInstance().getReference("Comunidades");
                                refIncidencias.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            DataSnapshot usuarioNombreSnapshot = snapshot.child("administrador");
                                            String usuarioUid = snapshot.child("administrador").getValue(String.class);

                                            if (usuarioUid != null && usuarioUid.equals(uidUsuario)) {
                                                //poner el id de otro admin aleatorio
                                                for (DataSnapshot snapshot2 : dataSnapshot.getChildren()) {
                                                    String usuarioUid2 = snapshot2.child("administrador").getValue(String.class);
                                                    if (usuarioUid2 != null && !usuarioUid2.equals(uidUsuario)) {
                                                        refIncidencias.child(snapshot.getKey()).child("administrador").setValue(usuarioUid2);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Manejar el error de cancelación, si es necesario
                                    }
                                });
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
                FirebaseAuth.getInstance().signOut();
                context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(context, LoginRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
            */
}
