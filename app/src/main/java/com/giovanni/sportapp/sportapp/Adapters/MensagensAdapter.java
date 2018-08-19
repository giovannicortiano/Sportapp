package com.giovanni.sportapp.sportapp.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Mensagem;
import com.giovanni.sportapp.sportapp.R;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.ViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = null;
        if(viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente, parent, false);
        }
        else if (viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario, parent, false);
        }

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mensagem mensagem = mensagens.get(position);
        holder.Mensagem.setText(mensagem.getMensagem());
        holder.DataHoraConversa.setText(mensagem.getDataEHora());
        if (holder.getItemViewType() == TIPO_REMETENTE){
            if (mensagem.isMensagemLida()){
                holder.DataHoraConversa.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_double_check_white_24dp,0);
            }else{
                holder.DataHoraConversa.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_white_24dp,0);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagens.get(position);
        String IdRemetente = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser().getUid().toString();
        if (IdRemetente.equals(mensagem.getIdUsuarioRemetente())){
            return TIPO_REMETENTE;
        }

        return TIPO_DESTINATARIO;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        private TextView Mensagem;
        private TextView DataHoraConversa;
        public ViewHolder (View itemView){
            super(itemView);
            Mensagem = itemView.findViewById(R.id.textViewMensagemConversa);
            DataHoraConversa  = itemView.findViewById(R.id.textDataHoraConversa);
        }
    }
}
