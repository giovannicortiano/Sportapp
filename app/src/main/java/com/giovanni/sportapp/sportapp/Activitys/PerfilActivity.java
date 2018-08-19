package com.giovanni.sportapp.sportapp.Activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Mensagem;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {
    private Usuario UsuarioPerfil;
    private TextView TextViewNome;
    private TextView TextViewEsportes;
    private TextView TextViewSobre;
    private CircleImageView ImagemPerfil;
    private FloatingActionButton BtnEnviarMensagem;

    private void RecuperarViews(){
        TextViewNome = findViewById(R.id.textNomeTelaPerfil);
        TextViewEsportes = findViewById(R.id.TextEsportesPerfil);
        TextViewSobre = findViewById(R.id.TextSobrePerfil);
        ImagemPerfil = findViewById(R.id.imagemTelaPerfil);
        BtnEnviarMensagem = findViewById(R.id.btnEnviarMensagem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);


        RecuperarViews();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            UsuarioPerfil = (Usuario) bundle.getSerializable("Usuario");
        }

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BtnEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilActivity.this, ConversaActivity.class);
                intent.putExtra("Usuario",UsuarioPerfil);
                startActivity(intent);
            }
        });

    }

}
