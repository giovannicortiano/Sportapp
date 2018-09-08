package com.giovanni.sportapp.sportapp.Adapters;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Model.Conversa;
import com.giovanni.sportapp.sportapp.R;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.ViewHolder>{

    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversasAdapter(ArrayList<Conversa> ListaConversas, Context c) {
        this.conversas = ListaConversas;
        this.context = c;

    }

    @Override
    public ConversasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ItemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas, parent, false);
        return new ViewHolder(ItemLista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);
        holder.Nome.setText(conversa.getUsuarioExibicao().getNome());
        holder.UltimaMensagem.setText(conversa.getUltimaMensagem());

        if (conversa.getUsuarioExibicao().getFotoUrl() != null){
            Uri uri = Uri.parse(conversa.getUsuarioExibicao().getFotoUrl());
            Glide.with(context).load(uri).into(holder.Foto);
        }
        else{
            holder.Foto.setImageResource(R.drawable.perfil_padrao);
        }

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        CircleImageView Foto;
        TextView Nome;
        TextView UltimaMensagem;
        public ViewHolder (View itemView){
            super(itemView);

            Foto = itemView.findViewById(R.id.ImagemListaConversa);
            Nome = itemView.findViewById(R.id.TextNomeListaConversa);
            UltimaMensagem = itemView.findViewById(R.id.TextUltimaMensagemConversa);
        }
    }
}
