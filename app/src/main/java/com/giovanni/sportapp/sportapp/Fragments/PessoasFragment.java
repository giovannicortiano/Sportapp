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

import com.giovanni.sportapp.sportapp.Adapters.PessoasAdapter;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.Activitys.PerfilActivity;
import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.Utils.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PessoasFragment extends Fragment {

    private RecyclerView RecyclerViewPessoas;
    private PessoasAdapter pessoasAdapter;
    private ArrayList<Usuario> ListaDePessoas = new ArrayList<>();
    private DatabaseReference ReferenciaUsuarios;
    private ValueEventListener EventListnerDeContatos;


    public PessoasFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_pessoas, container, false);
        RecyclerViewPessoas = view.findViewById(R.id.recyclerViewPessoas);
        ReferenciaUsuarios = ConfiguradorFireBase.getBancoDeDadosFireBase().child("Usuarios");

        pessoasAdapter = new PessoasAdapter(ListaDePessoas, getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerViewPessoas.setLayoutManager(layoutManager);
        RecyclerViewPessoas.setHasFixedSize(true);
        RecyclerViewPessoas.setAdapter(pessoasAdapter);
        RecyclerViewPessoas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), RecyclerViewPessoas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioClicado = ListaDePessoas.get(position);
                Intent intent = new Intent(getActivity(), PerfilActivity.class);
                intent.putExtra("Usuario",usuarioClicado);
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
        RecuperarUsuarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        ReferenciaUsuarios.removeEventListener(EventListnerDeContatos);
    }

    public void RecuperarUsuarios(){
        EventListnerDeContatos =  ReferenciaUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListaDePessoas.clear();
              for (DataSnapshot dados : dataSnapshot.getChildren()){
                  Usuario usuario = dados.getValue(Usuario.class);
                  ListaDePessoas.add(usuario);
              }

              pessoasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
