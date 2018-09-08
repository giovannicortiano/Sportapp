package com.giovanni.sportapp.sportapp.Fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.giovanni.sportapp.sportapp.Adapters.PessoasAdapter;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.Activitys.PerfilActivity;
import com.giovanni.sportapp.sportapp.Model.UsuarioDao;
import com.giovanni.sportapp.sportapp.Model.UsuarioDaoFirebase;
import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.Utils.CaptorDeLocalizacao;
import com.giovanni.sportapp.sportapp.Utils.RecyclerItemClickListener;
import com.giovanni.sportapp.sportapp.Utils.ValidadorDePermissao;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class PessoasFragment extends Fragment implements Observer {

    private RecyclerView        RecyclerViewPessoas;
    private PessoasAdapter      pessoasAdapter;
    private ArrayList<Usuario>  listaDePessoas = new ArrayList<>();
    private ArrayList<Usuario>  listaDePessoasBackGround = new ArrayList<>();
    private static final int    NUMERO_PAGINACAO_DE_PESSOAS = 30;
    private int                 ultimoIndiceAddedNaListaPrincial = 0;
    private LinearLayoutManager layoutManager;
    private UsuarioDao          usuarioDao;
    private Usuario             usuarioLogado;
    private CaptorDeLocalizacao captorDeLocalizacao;
    private String              idUsuarioLogado;
    private boolean             atualizarLocalizacao;

    private String[] PermissoesNecesssarias = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };


    public PessoasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pessoas, container, false);

        ValidadorDePermissao.ValidarPermissoes(getActivity(), PermissoesNecesssarias);

        atualizarLocalizacao = true;
        captorDeLocalizacao = new CaptorDeLocalizacao();
        captorDeLocalizacao.addObserver(this);

        usuarioDao = new UsuarioDaoFirebase();
        idUsuarioLogado = usuarioDao.RetornarIdUsuarioLogado();

        RecyclerViewPessoas = view.findViewById(R.id.recyclerViewPessoas);

        pessoasAdapter = new PessoasAdapter(listaDePessoas, getActivity());
        layoutManager = new LinearLayoutManager(getActivity());

        ConfigurarRecyclerView();
        return view;
    }

    public void TratarUsuario(){
        if (usuarioLogado != null) {
            if ((atualizarLocalizacao) || usuarioLogado.getL() == null) {
                BuscarLocalizacaoAtualDoDispostivo();
            }
            else {
                pessoasAdapter.setPosicaoUsuarioLogado(usuarioLogado.getL().get(0),usuarioLogado.getL().get(1));
                ConsultarUsuariosProximosPorLocalizacao();
            }
        }
        else {
            usuarioDao.ConsultarUsuarioPorId(idUsuarioLogado);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        usuarioDao.AddObserver(this);
        TratarUsuario();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioDao.RemoverObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UsuarioDaoFirebase) {
            if (arg != null && arg instanceof String){
                Toast.makeText(getActivity(),(String) arg,Toast.LENGTH_SHORT).show();
            }
            else if (arg != null && arg instanceof Usuario){
                usuarioLogado = (Usuario) arg;
                TratarUsuario();
            }
            else if (arg != null && arg instanceof ArrayList){{
                listaDePessoasBackGround =  (ArrayList) arg;
                AdicionarPessoasNaListaPrincipal();
            }}
        }
        else if (o instanceof CaptorDeLocalizacao){
            AtualizarLocalizacaoUsuarioLogado(captorDeLocalizacao.getLocalAtualDispositivo());
            TratarUsuario();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i : grantResults){
            if (i == PackageManager.PERMISSION_DENIED){
                AlertarPermissoesNegadas();
            }
        }
    }

    private void AtualizarLocalizacaoUsuarioLogado(Location location){
        if ((location != null) && usuarioLogado != null){
            Double [] latitudeLongitude = new Double[2];
            latitudeLongitude[0] = location.getLatitude();
            latitudeLongitude[1] = location.getLongitude();
            usuarioLogado.setL(Arrays.asList(latitudeLongitude));
            usuarioDao.GravarUsuarioNoBancoDeDados(usuarioLogado,false);
            atualizarLocalizacao = false;
        }
    }

    private void BuscarLocalizacaoAtualDoDispostivo() {
        captorDeLocalizacao.BuscarLocalizacaoDoDispositivo(getActivity());
    }

    public void PesquisarPessoas(String textoPesquisa){
        listaDePessoas.clear();
        textoPesquisa = textoPesquisa.toUpperCase();
        for (Usuario pessoa : listaDePessoasBackGround) {
            if (pessoa.getEsportes() != null) {
                String esportes = pessoa.getEsportes().toUpperCase();

                if (esportes.contains(textoPesquisa)) {
                    listaDePessoas.add(pessoa);
                }
                if (listaDePessoas.size() >= NUMERO_PAGINACAO_DE_PESSOAS) {
                    break;
                }

            }
        }
        pessoasAdapter.notifyDataSetChanged();
    }

    public void ConsultarUsuariosProximosPorLocalizacao(){
        usuarioDao.ConsultarUsuariosPorLocalizacao(usuarioLogado.getL().get(0),usuarioLogado.getL().get(1),usuarioLogado.getRaioDeKm());
    }

    private void AdicionarMaisPessoasNaListaPrincipal(){
        int i = ultimoIndiceAddedNaListaPrincial;
        int ate = i + NUMERO_PAGINACAO_DE_PESSOAS;
        while (listaDePessoas.size() <= ate){
            if (i >= listaDePessoasBackGround.size()){
                break;
            }

            listaDePessoas.add(listaDePessoasBackGround.get(i));
            i ++;
        }
        ultimoIndiceAddedNaListaPrincial = i;
        pessoasAdapter.notifyDataSetChanged();
    }

    public void AdicionarPessoasNaListaPrincipal(){
        int i = 0;
        listaDePessoas.clear();
        while (listaDePessoas.size() <= NUMERO_PAGINACAO_DE_PESSOAS){
            if (i >= listaDePessoasBackGround.size()){
                break;
            }

            listaDePessoas.add(listaDePessoasBackGround.get(i));
            i ++;
        }
        ultimoIndiceAddedNaListaPrincial = i;
        pessoasAdapter.notifyDataSetChanged();
    }

    public void AlertarPermissoesNegadas(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.PermissoesNegadas);
        builder.setMessage(R.string.ParaUsarOSportappPrecisaPermissao);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    private void ConfigurarRecyclerView(){
        RecyclerViewPessoas.setLayoutManager(layoutManager);
        RecyclerViewPessoas.setHasFixedSize(true);
        RecyclerViewPessoas.setAdapter(pessoasAdapter);
        RecyclerViewPessoas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), RecyclerViewPessoas, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Usuario usuarioClicado = listaDePessoas.get(position);
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

        RecyclerViewPessoas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (listaDePessoas.size() >= NUMERO_PAGINACAO_DE_PESSOAS &&
                          layoutManager.findLastVisibleItemPosition() == ultimoIndiceAddedNaListaPrincial) {
                    AdicionarMaisPessoasNaListaPrincipal();
                }
            }
        });
    }
}

