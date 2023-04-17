package com.example.blockbuddytfg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.blockbuddytfg.R;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Locale;

public class VecinosAdapter extends FirebaseRecyclerAdapter<Usuario, VecinosAdapter.VecinosViewHolder> {

    public VecinosAdapter(@NonNull FirebaseRecyclerOptions<Usuario> options) {
        super(options);
    }

    @NonNull
    @Override
    public VecinosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vecinoscard, parent, false);
        return new VecinosViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull VecinosAdapter.VecinosViewHolder holder, int position, @NonNull Usuario model) {
        holder.bind(model);
    }

    public static class VecinosViewHolder extends RecyclerView.ViewHolder {

        private TextView cmNombre, cmTelefono, cmPiso;
        private ImageView cmImagen;

        public VecinosViewHolder(@NonNull View itemView) {
            super(itemView);
            cmNombre = itemView.findViewById(R.id.vecino_nombre);
            cmTelefono = itemView.findViewById(R.id.vecino_telefono);
            cmPiso = itemView.findViewById(R.id.vecino_piso);
            cmImagen = itemView.findViewById(R.id.vecino_imagen);
        }

        public void bind(Usuario usuario) {
            cmNombre.setText(usuario.getNombre().toUpperCase(Locale.ROOT));
            cmTelefono.setText(usuario.getTelefono());
            cmPiso.setText("PISO: "+ usuario.getPiso() + " " + usuario.getPuerta());
            Glide.with(itemView.getContext()).load(usuario.getImagen()).into(cmImagen);
        }
    }
}
