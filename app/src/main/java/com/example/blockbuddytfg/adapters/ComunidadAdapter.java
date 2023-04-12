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
import com.example.blockbuddytfg.entities.Comunidad;
import com.example.blockbuddytfg.entities.Usuario;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ComunidadAdapter extends FirebaseRecyclerAdapter<Comunidad, ComunidadAdapter.CommunityViewHolder> {

    public ComunidadAdapter(@NonNull FirebaseRecyclerOptions<Comunidad> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommunityViewHolder holder, int position, @NonNull Comunidad model) {
        holder.bind(model);
    }
    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comcard, parent, false);
        return new CommunityViewHolder(view);
    }

    public static class CommunityViewHolder extends RecyclerView.ViewHolder {

        private TextView cmNombre, cmDireccion, cmCP, cmViviendas, cmCodigo;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            cmNombre = itemView.findViewById(R.id.comunidad_nombre);
            cmDireccion = itemView.findViewById(R.id.comunidad_direccion);
            cmCP = itemView.findViewById(R.id.comunidad_cp);
            cmViviendas = itemView.findViewById(R.id.comunidad_viviendas);
            cmCodigo = itemView.findViewById(R.id.comunidad_codigo);
        }

        public void bind(Comunidad community) {
            cmNombre.setText(community.getNombre());
            cmDireccion.setText(community.getDireccion());
            cmCP.setText(community.getCodigoPostal());
            cmViviendas.setText("Viviendas - " + community.getViviendas());
            cmCodigo.setText("C - " + community.getCodigoComunidad());
        }
    }
}