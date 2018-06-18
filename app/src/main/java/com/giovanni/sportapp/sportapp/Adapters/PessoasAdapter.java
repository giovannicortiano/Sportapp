package com.giovanni.sportapp.sportapp.Adapters;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PessoasAdapter extends RecyclerView.Adapter<PessoasAdapter.ViewHolder>{
    private List<Usuario> ListaDePessoas;
    private Context Context;

    public PessoasAdapter(List<Usuario> Pessoas, Context context) {
        this.ListaDePessoas = Pessoas;
        this.Context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ItemDaLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pessoas,parent,false);
        return new ViewHolder(ItemDaLista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Usuario  usuario  = ListaDePessoas.get(position);
        holder.NomeContato.setText(usuario.getNome());

        if (usuario.getFotoUrl() != null){
            Uri uri = Uri.parse(usuario.getFotoUrl());
            Glide.with(Context).load(uri).into(holder.FotoPerfilContato);
        }
        else{
            holder.FotoPerfilContato.setImageResource(R.drawable.perfil_padrao);
        }
    }

    @Override
    public int getItemCount() {
        return ListaDePessoas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView FotoPerfilContato;
        TextView NomeContato;

        public ViewHolder(View itemView) {
            super(itemView);
            FotoPerfilContato = itemView.findViewById(R.id.ImagemListaPessoa);
            NomeContato = itemView.findViewById(R.id.TextNomeListaPessoa);



        }
    }
}
