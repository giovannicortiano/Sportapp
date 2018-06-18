package com.giovanni.sportapp.sportapp.Activitys;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {
    private Usuario UsuarioPerfil;
    private TextView TextViewNome;
    private TextView TextViewEsportes;
    private TextView TextViewSobre;
    private CircleImageView ImagemPerfil;

    private void RecuperarViews(){
        TextViewNome = findViewById(R.id.textNomeTelaPerfil);
        TextViewEsportes = findViewById(R.id.TextEsportesPerfil);
        TextViewSobre = findViewById(R.id.TextSobrePerfil);
        ImagemPerfil = findViewById(R.id.imagemTelaPerfil);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
