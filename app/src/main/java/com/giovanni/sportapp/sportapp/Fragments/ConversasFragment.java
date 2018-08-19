package com.giovanni.sportapp.sportapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.giovanni.sportapp.sportapp.Activitys.ConversaActivity;
import com.giovanni.sportapp.sportapp.Activitys.PerfilActivity;
import com.giovanni.sportapp.sportapp.Adapters.ConversasAdapter;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Conversa;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.Utils.RecyclerItemClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


///TODO FALTA FAZER GRAVAR A CONVERSA PARA O DESTINATARIO; POIS SÓ O REMETENTE TA VENDO A CONVERSA

public class ConversasFragment extends Fragment {

    private RecyclerView RecyclerViewListaConversas;
    private List<Conversa> ListaDeConversas = new ArrayList<>();
    private ConversasAdapter Adapter;
    private String IdUsuarioLogado;
    private DatabaseReference DataBase;
    private DatabaseReference ConversasRef;
    private ChildEventListener childEventListenerConversas;


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
        DataBase = ConfiguradorFireBase.getBancoDeDadosFireBase();
        ConversasRef =  DataBase.child("conversas").child(IdUsuarioLogado);

        RecyclerViewListaConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), RecyclerViewListaConversas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Conversa ConversaClicada  = ListaDeConversas.get(position);
                Intent intent = new Intent(getActivity(), ConversaActivity.class);
                intent.putExtra("Usuario",ConversaClicada.getUsuarioExibicao());
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
    public void onStart() {
        super.onStart();
        RecuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        ConversasRef.removeEventListener(childEventListenerConversas);
    }

    public void RecuperarConversas(){


        childEventListenerConversas = ConversasRef.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               Conversa conversa  = dataSnapshot.getValue(Conversa.class);
               ListaDeConversas.add(conversa);
               Adapter.notifyDataSetChanged();
           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });



    }

}
