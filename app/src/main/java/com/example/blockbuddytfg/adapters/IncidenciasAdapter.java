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
import com.example.blockbuddytfg.entities.Incidencia;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Locale;

public class IncidenciasAdapter extends FirebaseRecyclerAdapter<Incidencia, IncidenciasAdapter.IncidenciasViewHolder> {

    public IncidenciasAdapter(@NonNull FirebaseRecyclerOptions<Incidencia> options) {
        super(options);
    }

    @NonNull
    @Override
    public IncidenciasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incidenciacard, parent, false);
        return new IncidenciasViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull IncidenciasAdapter.IncidenciasViewHolder holder, int position, @NonNull Incidencia model) {
        holder.bind(model);
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
}
