package com.giovanni.sportapp.sportapp.Activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {
    private Usuario UsuarioPerfil;
    private TextView TextViewNome;
    private TextView TextViewEsportes;
    private TextView TextViewSobre;
    private CircleImageView ImagemPerfil;
    private ImageButton BtnEnviarMensagem;
    private static final String PERFIL = "Perfil";
    private static final String USUARIO = "Usuario";
    private TextView TextViewDistanciaPerfil;
    private static final String APROX = "Aprox.";
    private static final String KM = "KM";
    private static final String MENOS_DE_UM_KM = "Menos de 1KM";
    private LatLng posicaoUsuarioLogado;
    private double LatitudeUsuarioLogado;
    private double LongitudeUsuarioLogado;
    private static final String LATITUDE_USUARIO_LOGADO = "LatitudeUsuarioLogado";
    private static final String LONGITUDE_USUARIO_LOGADO = "LongitudeUsuarioLogado";
    private static final String DISTANCIA_INDISPONIVEL = "Distância indisponível";
    private boolean mostrarBotaoMensagem;
    private static final String MOSTRAR_BOTAO_MENSAGEM = "mostrarBotaoMensagem";
    private ImageView ImagemVoltar;

    private void RecuperarViews(){
        TextViewNome = findViewById(R.id.textNomeTelaPerfil);
        TextViewEsportes = findViewById(R.id.TextEsportesPerfil);
        TextViewSobre = findViewById(R.id.TextSobrePerfil);
        ImagemPerfil = findViewById(R.id.imagemTelaPerfil);
        BtnEnviarMensagem = findViewById(R.id.BtnEnviarMensagem);
        TextViewDistanciaPerfil = findViewById(R.id.textDistanciaPerfil);
        ImagemVoltar = findViewById(R.id.imageViewVoltar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarPerfil);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        RecuperarViews();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            UsuarioPerfil = (Usuario) bundle.getSerializable(USUARIO);
            mostrarBotaoMensagem = (boolean) bundle.getSerializable(MOSTRAR_BOTAO_MENSAGEM);
            LatitudeUsuarioLogado = (double) bundle.getSerializable(LATITUDE_USUARIO_LOGADO);
            LongitudeUsuarioLogado = (double) bundle.getSerializable(LONGITUDE_USUARIO_LOGADO);
            posicaoUsuarioLogado = new LatLng(LatitudeUsuarioLogado,LongitudeUsuarioLogado);
            PreencherDadosUsuarioPerfil();
        }

        ImagemVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        BtnEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, MensagemActivity.class);
                intent.putExtra(USUARIO,UsuarioPerfil);
                startActivity(intent);
            }
        });

        if (mostrarBotaoMensagem) {
            BtnEnviarMensagem.setVisibility(View.VISIBLE);
        }else
        {
            BtnEnviarMensagem.setVisibility(View.INVISIBLE);
        }

    }

    private void PreencherDadosUsuarioPerfil(){
        TextViewNome.setText(UsuarioPerfil.getNome());
        TextViewEsportes.setText(UsuarioPerfil.getEsportes());
        TextViewSobre.setText(UsuarioPerfil.getSobre());

        if (UsuarioPerfil.getFotoUrl() != null){
            Uri uri = Uri.parse(UsuarioPerfil.getFotoUrl());
            Glide.with(this).load(uri).into(ImagemPerfil);
        }
        else{
            ImagemPerfil.setImageResource(R.drawable.perfil_padrao);
        }

        if (UsuarioPerfil.getL() != null && posicaoUsuarioLogado != null){
            LatLng posicaoUsuarioLista = new LatLng(UsuarioPerfil.getL().get(0),UsuarioPerfil.getL().get(1));
            double distancia = SphericalUtil.computeDistanceBetween(posicaoUsuarioLogado,posicaoUsuarioLista);
            int km  = (int) distancia / 1000;
            if (km > 0) {
                TextViewDistanciaPerfil.setText(APROX + " " + String.valueOf(km) + KM);
            }
            else{
                TextViewDistanciaPerfil.setText(MENOS_DE_UM_KM);
            }
        }
        else{
            TextViewDistanciaPerfil.setText(DISTANCIA_INDISPONIVEL);
        }
    }

}
