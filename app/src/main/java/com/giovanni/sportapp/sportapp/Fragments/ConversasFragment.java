package com.giovanni.sportapp.sportapp.Fragments;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.giovanni.sportapp.sportapp.Activitys.MensagemActivity;
import com.giovanni.sportapp.sportapp.Adapters.ConversasAdapter;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Conversa;
import com.giovanni.sportapp.sportapp.Model.ConversaDao;
import com.giovanni.sportapp.sportapp.Model.ConversaDaoFirebase;
import com.giovanni.sportapp.sportapp.Model.Mensagem;
import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.Utils.RecyclerItemClickListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ConversasFragment extends Fragment implements Observer{

    private RecyclerView     RecyclerViewListaConversas;
    private ArrayList<Conversa>  ListaDeConversas = new ArrayList<>();
    private ConversasAdapter Adapter;
    private String           IdUsuarioLogado;
    private static final String USUARIO = "Usuario";

    public ConversasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
        RecyclerViewListaConversas = view.findViewById(R.id.idRecyclerViewListaConversas);

        Adapter = new ConversasAdapter(ListaDeConversas,getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerViewListaConversas.setLayoutManager(layoutManager);
        RecyclerViewListaConversas.setHasFixedSize(true);
        RecyclerViewListaConversas.setAdapter(Adapter);

        IdUsuarioLogado = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser().getUid();
        ConversaDao conversaDao = new ConversaDaoFirebase(ListaDeConversas);
        conversaDao.AdicionarObserver(this);
        conversaDao.ListarConversasDoUsuario(IdUsuarioLogado);

        RecyclerViewListaConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), RecyclerViewListaConversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Conversa ConversaClicada  = ListaDeConversas.get(position);
                Intent intent = new Intent(getActivity(), MensagemActivity.class);
                intent.putExtra(USUARIO,ConversaClicada.getUsuarioExibicao());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        return view;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ConversaDaoFirebase){
            if (arg instanceof Conversa){
                Adapter.notifyDataSetChanged();
            }
        }
    }





}
