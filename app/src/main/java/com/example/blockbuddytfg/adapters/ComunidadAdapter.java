package com.example.blockbuddytfg.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blockbuddytfg.MainAdministradorActivity;
import com.example.blockbuddytfg.MainUserActivity;
import com.example.blockbuddytfg.R;
import com.example.blockbuddytfg.entities.Administrador;
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

public class ComunidadAdapter extends FirebaseRecyclerAdapter<Comunidad, ComunidadAdapter.CommunityViewHolder> {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private Context context;
    private static TextView cmNombre, cmDireccion, cmCP, cmViviendas, cmCodigo;

    public ComunidadAdapter(@NonNull FirebaseRecyclerOptions<Comunidad> options, DatabaseReference dbRef, FirebaseUser user, Context context) {
        super(options);
        this.dbRef = dbRef;
        this.user = user;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull CommunityViewHolder holder, int position, @NonNull Comunidad model) {
        holder.bind(model);
        holder.itemView.setOnClickListener(view -> mostrarDialogo(model));

    }
    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comcard, parent, false);
        return new CommunityViewHolder(view);
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder {



        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            cmNombre = itemView.findViewById(R.id.comunidad_nombre);
            cmDireccion = itemView.findViewById(R.id.comunidad_direccion);
            cmCP = itemView.findViewById(R.id.comunidad_cp);
            cmViviendas = itemView.findViewById(R.id.comunidad_viviendas);
            cmCodigo = itemView.findViewById(R.id.comunidad_codigo);
        }

        public void bind(Comunidad community) {
            cmNombre.setText(community.getNombre().toUpperCase(Locale.ROOT));
            cmDireccion.setText(community.getDireccion().toUpperCase(Locale.ROOT));
            cmCP.setText(community.getCodigoPostal());
            cmViviendas.setText("VIVIENDAS - " + community.getViviendas());
            cmCodigo.setText("C - " + community.getCodigoComunidad());
        }
    }

    private void mostrarDialogo(Comunidad comunidad) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Selecciona una opción:");

        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Código para editar la comunidad
            }
        });

        builder.setPositiveButton("Gestionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (context != null) {
                    Intent intent = new Intent(context, MainUserActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("comunidad", cmCodigo.getText().toString());
                    context.startActivity(intent);
                } else {
                }
            }
        });
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarComunidad(comunidad);
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

    private void eliminarComunidad(Comunidad com) {
        dbRef.child(com.getCodigoComunidad()).removeValue();
    }
}