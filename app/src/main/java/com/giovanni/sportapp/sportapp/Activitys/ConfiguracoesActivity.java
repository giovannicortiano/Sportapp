package com.giovanni.sportapp.sportapp.Activitys;

import android.Manifest;
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
import android.widget.TextView;

import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.Utils.ValidadorDePermissao;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Toolbar ToolBarConfiguracoes;
    private CircleImageView ImagemPerfil;
    private ImageButton BtnCamera;
    private ImageButton BtnGaleriaFoto;
    private Button BtnAtualizar;
    private TextView TextViewCancelarConta;
    private EditText EdtNome;
    private EditText EdtSobre;
    private EditText EdtEsportes;
    private static final int SELECECAO_CAMERA = 100;
    private static final int SELECECAO_GALERIA = 200;
    private String[] PermissoesNecesssarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        ToolBarConfiguracoes = findViewById(R.id.toolBarPrincipal);
        ToolBarConfiguracoes.setTitle(R.string.Configuracoes);
        setSupportActionBar(ToolBarConfiguracoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ValidadorDePermissao.ValidarPermissoes(this,PermissoesNecesssarias);
        RecuperarViews();
        ConfigurarBtnCameraClick();
        ConfigurarBtnGaleriaFotoClick();
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

    public void RecuperarViews(){
        ImagemPerfil = findViewById(R.id.ImagemPerfil);
        BtnCamera = findViewById(R.id.btnCamera);
        BtnGaleriaFoto = findViewById(R.id.btnGaleria);
    }

    public void ConfigurarBtnCameraClick(){
        BtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,SELECECAO_CAMERA);
                }

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
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o Sportapp é necessário aceitar as permissões");
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
                    Uri LocalDaImagem = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),LocalDaImagem);
                }
                else if (requestCode == SELECECAO_CAMERA){
                    imagem = (Bitmap) data.getExtras().get("data");
                }

                if (imagem != null){
                    ImagemPerfil.setImageBitmap(imagem);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
