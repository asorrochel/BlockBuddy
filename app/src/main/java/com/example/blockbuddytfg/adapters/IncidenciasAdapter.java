package com.example.blockbuddytfg.adapters;

import android.annotation.SuppressLint;
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
import com.example.blockbuddytfg.entities.Incidencia;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class IncidenciasAdapter extends FirebaseRecyclerAdapter<Incidencia, IncidenciasAdapter.IncidenciasViewHolder> {
    private static Context context;
    FirebaseUser user;
    String filtro;
    public IncidenciasAdapter(@NonNull FirebaseRecyclerOptions<Incidencia> options, FirebaseUser user, Context context, String filtro) {
        super(options);
        this.user = user;
        this.context = context;
        this.filtro = filtro;
    }

    @NonNull
    @Override
    public IncidenciasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incidenciacard, parent, false);
        return new IncidenciasViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull IncidenciasAdapter.IncidenciasViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Incidencia model) {
        //comprobar si el usuario es administrador
        DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(user.getUid());

        adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //si es administrador
                    if(!model.getValidada()) {
                        holder.bind(model);
                        holder.itemView.setOnClickListener(view -> mostrarDialogPendientes(model, position));
                    }
                    else {
                        holder.bind(model);
                        holder.itemView.setOnClickListener(view -> mostrarDialogValidadas(model, position));
                    }
                }
                else {
                    //si no es administrador
                    holder.bind(model);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class IncidenciasViewHolder extends RecyclerView.ViewHolder {

        private TextView in_nombre, in_descripcion, in_nomUsuario, in_fecha;
        private ImageView in_imagen;

        public IncidenciasViewHolder(@NonNull View itemView) {
            super(itemView);
            in_nombre = itemView.findViewById(R.id.incidencia_nombre);
            in_descripcion = itemView.findViewById(R.id.incidencia_descripcion);
            in_nomUsuario = itemView.findViewById(R.id.incidencia_creador);
            in_fecha = itemView.findViewById(R.id.incidencia_fecha);
            in_imagen = itemView.findViewById(R.id.incidencia_imagen);
        }

        public void bind(Incidencia incidencia) {
            in_nombre.setText(incidencia.getNombre().toUpperCase(Locale.ROOT));
            in_descripcion.setText(incidencia.getDescripcion().toUpperCase(Locale.ROOT));
            in_nomUsuario.setText(incidencia.getUsuarioNombre().toUpperCase(Locale.ROOT));
            in_fecha.setText(incidencia.getFecha().substring(0, 10));
            Glide.with(itemView.getContext()).load(incidencia.getImagen()).into(in_imagen);
        }
    }

    public void mostrarDialogPendientes(Incidencia incidencia, int position){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Validar incidencia");
        builder.setMessage("¿Desea validar esta incidencia?");
        builder.setPositiveButton("Validar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cambiar estado de validada a true en la base de datos
                FirebaseDatabase.getInstance().getReference().child("Incidencias").child(getRef(position).getKey()).child("validada").setValue(true);
                FirebaseDatabase.getInstance().getReference()
                        .child("Incidencias")
                        .child(getRef(position).getKey())
                        .child("cod_validada")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String codValidada = snapshot.getValue(String.class);
                                String newCodValidada = codValidada.replace("_false", "_true");
                                FirebaseDatabase.getInstance().getReference().child("Incidencias").child(getRef(position).getKey()).child("cod_validada").setValue(newCodValidada);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        }
                );
            }
        });
        builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Eliminar incidencia de la base de datos
                FirebaseDatabase.getInstance().getReference().child("Incidencias").child(getRef(position).getKey()).removeValue();
            }
        });
        builder.show();
    }

    public void mostrarDialogValidadas(Incidencia incidencia, int position){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Gestionar Incidencia");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // Editar incidencia
            }
        });
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Eliminar incidencia de la base de datos
                FirebaseDatabase.getInstance().getReference().child("Incidencias").child(getRef(position).getKey()).removeValue();
            }
        });
        builder.show();
    }
}
