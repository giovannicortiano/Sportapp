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
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class PessoasAdapter extends RecyclerView.Adapter<PessoasAdapter.ViewHolder>{
    private List<Usuario> ListaDePessoas;
    private Context Context;
    private LatLng posicaoUsuarioLogado;

    public PessoasAdapter(List<Usuario> Pessoas, Context context) {
        this.ListaDePessoas = Pessoas;
        this.Context = context;
    }

    public void setPosicaoUsuarioLogado(double latitude, double longitude){
        posicaoUsuarioLogado = new LatLng(latitude,longitude);
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
        holder.EsportesContato.setText(usuario.getEsportes());

        if (usuario.getL() != null && posicaoUsuarioLogado != null){
            LatLng posicaoUsuarioLista = new LatLng(usuario.getL().get(0),usuario.getL().get(1));
            double distancia = SphericalUtil.computeDistanceBetween(posicaoUsuarioLogado,posicaoUsuarioLista);
            int km  = (int) distancia / 1000;
            if (km > 0) {
                holder.DistanciaContato.setText("Aprox. " + String.valueOf(km) + "KM");
            }
            else{
                holder.DistanciaContato.setText("Menos de 1KM ");
            }
        }


    }

    @Override
    public int getItemCount() {
        return ListaDePessoas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView FotoPerfilContato;
        TextView NomeContato;
        TextView EsportesContato;
        TextView DistanciaContato;

        public ViewHolder(View itemView) {
            super(itemView);
            FotoPerfilContato = itemView.findViewById(R.id.ImagemListaPessoa);
            NomeContato = itemView.findViewById(R.id.TextNomeListaPessoa);
            EsportesContato = itemView.findViewById(R.id.TextEsportesListaPessoa);
            DistanciaContato = itemView.findViewById(R.id.TextDistanciaListaPessoas);
        }
    }
}
