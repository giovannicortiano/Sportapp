package com.giovanni.sportapp.sportapp.Activitys;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.Utils.ValidadorDePermissao;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Toolbar          ToolBarConfiguracoes;
    private CircleImageView  ImagemPerfil;
    private ImageButton      BtnCamera;
    private ImageButton      BtnGaleriaFoto;
    private Button           BtnAtualizar;
    private TextView         TextViewCancelarConta;
    private EditText         EdtNome;
    private EditText         EdtSobre;
    private EditText         EdtEsportes;
    private Usuario          UsuarioAtual;
    private ProgressBar      ProgressBarConfig;
    private Uri              LocalFotoPerfil;
    private StorageReference storageReference;
    private ProgressBar      ProgressBarImagem;
    private TextView         TextViewNomeConfig;
    private TextView         TextViewDescricaoConfig;
    private TextView         TextViewEsportesConfig;
    private static final int SELECECAO_CAMERA = 100;
    private static final int SELECECAO_GALERIA = 200;
    private String[]         PermissoesNecesssarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        ToolBarConfiguracoes = findViewById(R.id.toolBarPrincipal);
        ToolBarConfiguracoes.setTitle(R.string.Configuracoes);
        storageReference = ConfiguradorFireBase.getArmazenadorDeImagensFireBase();
        setSupportActionBar(ToolBarConfiguracoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ValidadorDePermissao.ValidarPermissoes(this,PermissoesNecesssarias);
        RecuperarViews();
        ProgressBarConfig.setVisibility(View.VISIBLE);
        ConfigurarVisibilidadeDosComponentes(View.INVISIBLE);
        BuscarUsuarioAtual();
        ConfigurarBtnCameraClick();
        ConfigurarBtnGaleriaFotoClick();
        ConfigurarBtnAtualizarDadosClick();
    }

    private void BuscarUsuarioAtual (){
        FirebaseUser UsuarioFireBase = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser();
        ConfiguradorFireBase.getBancoDeDadosFireBase().child("Usuarios").child(UsuarioFireBase.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       UsuarioAtual = dataSnapshot.getValue(Usuario.class);
                       ConfigurarVisibilidadeDosComponentes(View.VISIBLE);
                       PreencherDadosUsuarioAtual();
                       ProgressBarConfig.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

    private void ConfigurarVisibilidadeDosComponentes(int visivel){
        ImagemPerfil.setVisibility(visivel);
        EdtNome.setVisibility(visivel);
        EdtSobre.setVisibility(visivel);
        EdtEsportes.setVisibility(visivel);
        BtnAtualizar.setVisibility(visivel);
        BtnCamera.setVisibility(visivel);
        BtnGaleriaFoto.setVisibility(visivel);
        TextViewCancelarConta.setVisibility(visivel);
        TextViewNomeConfig.setVisibility(visivel);
        TextViewDescricaoConfig.setVisibility(visivel);
        TextViewEsportesConfig.setVisibility(visivel);
    }

    private void ConfigurarBtnAtualizarDadosClick(){
        BtnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidarPreenchimentoCampos()){
                    AtualizarDadosUsuario();
                    Toast.makeText(ConfiguracoesActivity.this,R.string.SucessoAtualizar,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean ValidarPreenchimentoCampos(){
        if (EdtNome.getText().toString().isEmpty()){
            Toast.makeText(ConfiguracoesActivity.this,R.string.CampoNomeObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (EdtEsportes.getText().toString().isEmpty()){
            Toast.makeText(ConfiguracoesActivity.this,R.string.EsporteObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void PreencherDadosUsuarioAtual(){
        if (UsuarioAtual.getFotoUrl() != null){
            Glide.with(ConfiguracoesActivity.this).load(UsuarioAtual.getFotoUrl()).into(ImagemPerfil);
        }
        else{
            ImagemPerfil.setImageResource(R.drawable.perfil_padrao);
        }

        EdtNome.setText(UsuarioAtual.getNome());
        EdtSobre.setText(UsuarioAtual.getSobre());
        EdtEsportes.setText(UsuarioAtual.getEsportes());
    }

    public void RecuperarViews(){
        ImagemPerfil = findViewById(R.id.ImagemPerfil);
        BtnCamera = findViewById(R.id.btnCamera);
        BtnGaleriaFoto = findViewById(R.id.btnGaleria);
        EdtNome = findViewById(R.id.edtNomeConfig);
        EdtEsportes = findViewById(R.id.edtEsportesConfig);
        EdtSobre = findViewById(R.id.edtSobreConfig);
        TextViewCancelarConta = findViewById(R.id.textViewApagarConta);
        BtnAtualizar = findViewById(R.id.btnAtualizarConfig);
        ProgressBarConfig = findViewById(R.id.progressBarConfig);
        ProgressBarImagem = findViewById(R.id.progressBarImagem);
        TextViewNomeConfig = findViewById(R.id.textDescricaoNomeConfig);
        TextViewDescricaoConfig = findViewById(R.id.TextSobreConfig);
        TextViewEsportesConfig = findViewById(R.id.TextEsportesConfig);
    }

    public void ConfigurarBtnCameraClick(){
        BtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues parametroParaCapturaDeFotoDaCamera = new ContentValues();
                parametroParaCapturaDeFotoDaCamera.put(MediaStore.Images.Media.TITLE, "foto_perfil_sportapp");
                parametroParaCapturaDeFotoDaCamera.put(MediaStore.Images.Media.DESCRIPTION, "Foto da Câmera");
                LocalFotoPerfil = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, parametroParaCapturaDeFotoDaCamera);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, LocalFotoPerfil);
                startActivityForResult(intent,SELECECAO_CAMERA);
            }
        });
    }

    public void ConfigurarBtnGaleriaFotoClick(){
        BtnGaleriaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,SELECECAO_GALERIA);
                }

            }
        });
    }

    public void AlertarPermissoesNegadas(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.PermissoesNegadas);
        builder.setMessage(R.string.ParaUsarOSportappPrecisaPermissao);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                if (requestCode == SELECECAO_GALERIA){
                    LocalFotoPerfil = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),LocalFotoPerfil);
                }
                else if (requestCode == SELECECAO_CAMERA){
                    imagem = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), LocalFotoPerfil);
                }

                if (imagem != null){
                    ImagemPerfil.setImageBitmap(imagem);
                    ProgressBarImagem.setVisibility(View.VISIBLE);
                    SalvarImagemPerfilFireBase();
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void SalvarImagemPerfilFireBase(){
        if (LocalFotoPerfil != null){
            StorageReference ImagemPerfilFireBase = storageReference.child("imagens")
                    .child("perfil")
                    .child(UsuarioAtual.getId() + ".jpeg");

            ImagemPerfilFireBase.putFile(LocalFotoPerfil).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    UsuarioAtual.setFotoUrl(taskSnapshot.getDownloadUrl().toString());
                    UsuarioAtual.GravarNoBancoDeDados();
                    ProgressBarImagem.setVisibility(View.INVISIBLE);
                    Toast.makeText(ConfiguracoesActivity.this,R.string.SucessoAoAtualizarImagem,Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ConfiguracoesActivity.this,R.string.FalhaAoAtualizarImagem,Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void AtualizarDadosUsuario(){
        UsuarioAtual.setNome(EdtNome.getText().toString());
        UsuarioAtual.setSobre(EdtSobre.getText().toString());
        UsuarioAtual.setEsportes(EdtEsportes.getText().toString());
        UsuarioAtual.GravarNoBancoDeDados();
    }
}
