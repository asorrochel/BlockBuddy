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
import com.example.blockbuddytfg.entities.Contacto;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ContactoAdapter extends FirebaseRecyclerAdapter<Contacto, ContactoAdapter.ContactoViewHolder> {

    private DatabaseReference dbRef;
    private FirebaseUser user;
    private Context context;
    String codComunidad, filtro;
    private static TextView cmNombre, cmTelefono;

    public ContactoAdapter(@NonNull FirebaseRecyclerOptions<Contacto> options, DatabaseReference dbRef, FirebaseUser user, Context context, String codComunidad, String filtro) {
        super(options);
        this.dbRef = dbRef;
        this.user = user;
        this.context = context;
        this.codComunidad = codComunidad;
        this.filtro = filtro;
    }

    @Override
    protected void onBindViewHolder(@NonNull ContactoViewHolder holder, int position, @NonNull Contacto model) {
        if(filtro.equals("usuario")){
           holder.bind(model);
        } else {
            holder.bind(model);
            holder.itemView.setOnClickListener(view -> mostrarDialogo(model));
        }
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactocard, parent, false);
        return new ContactoViewHolder(view);
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            cmNombre = itemView.findViewById(R.id.contacto_nombre);
            cmTelefono = itemView.findViewById(R.id.contacto_telefono);
        }

        public void bind(Contacto contacto) {
            cmNombre.setText(contacto.getNombre());
            cmTelefono.setText(contacto.getTelefono());
        }
    }

    private void mostrarDialogo(Contacto contacto) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Selecciona una opción:");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Código para editar el contactoo
            }
        });
        builder.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarContacto(contacto);
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

    private void eliminarContacto(Contacto contacto) {
        dbRef.child(codComunidad+"_"+cmNombre.getText().toString()).removeValue();
    }
}
