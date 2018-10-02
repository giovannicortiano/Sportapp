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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.Model.UsuarioDao;
import com.giovanni.sportapp.sportapp.Model.UsuarioDaoFirebase;
import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.Utils.ValidadorDePermissao;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity implements Observer {

    private Toolbar ToolBarConfiguracoes;
    private CircleImageView ImagemPerfil;
    private ImageButton BtnCamera;
    private ImageButton BtnGaleriaFoto;
    private Button BtnAtualizar;
    private TextView TextViewCancelarConta;
    private EditText EdtNome;
    private EditText EdtSobre;
    private EditText EdtEsportes;
    private ProgressBar ProgressBarConfig;
    private Uri LocalFotoPerfil;
    private ProgressBar ProgressBarImagem;
    private TextView TextViewNomeConfig;
    private TextView TextViewDescricaoConfig;
    private TextView TextViewEsportesConfig;
    private static final int SELECECAO_CAMERA = 100;
    private static final int SELECECAO_GALERIA = 200;
    private Usuario usuarioLogado;
    private UsuarioDao usuarioDaoFireBase;
    private SeekBar seekBarRaioKm;
    private TextView textViewDistanciaMaxima;
    private static final String FRASE_DISTANCIA_MAXIMA = "Distância Máxima";
    private static final String KM = "KM";
    private static final String OK = "OK";
    private static final String FOTO_PERFIL_SPORTAPP = "foto_perfil_sportapp";
    private static final String FOTO_DA_CAMERA = "Foto da Câmera";


    private String[] PermissoesNecesssarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        RecuperarViews();

        usuarioDaoFireBase = new UsuarioDaoFirebase();
        usuarioDaoFireBase.AddObserver(this);
        usuarioDaoFireBase.ConsultarUsuarioPorId(usuarioDaoFireBase.RetornarIdUsuarioLogado());

        ToolBarConfiguracoes.setTitle(R.string.Configuracoes);
        setSupportActionBar(ToolBarConfiguracoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ValidadorDePermissao.ValidarPermissoes(this, PermissoesNecesssarias);

        ProgressBarConfig.setVisibility(View.VISIBLE);
        ConfigurarVisibilidadeDosComponentes(View.INVISIBLE);

        ConfigurarBtnCameraClick();
        ConfigurarBtnGaleriaFotoClick();
        ConfigurarBtnAtualizarDadosClick();

        seekBarRaioKm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewDistanciaMaxima.setText(FRASE_DISTANCIA_MAXIMA + " "  + Integer.toString(progress) + KM);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UsuarioDaoFirebase){
            if ((arg != null) && (arg instanceof Usuario)){
                usuarioLogado = (Usuario) arg;
                ConfigurarVisibilidadeDosComponentes(View.VISIBLE);
                PreencherDadosUsuarioLogado();
                ProgressBarConfig.setVisibility(View.INVISIBLE);
            }

            if ((arg != null) && (arg instanceof String)){
                Toast.makeText(ConfiguracoesActivity.this,(String) arg,Toast.LENGTH_SHORT).show();
                ProgressBarImagem.setVisibility(View.INVISIBLE);
                ProgressBarConfig.setVisibility(View.INVISIBLE);
            }
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
    private void PreencherDadosUsuarioLogado(){
        if (usuarioLogado.getFotoUrl() != null){
            Glide.with(ConfiguracoesActivity.this).load(usuarioLogado.getFotoUrl()).into(ImagemPerfil);
        }
        else{
            ImagemPerfil.setImageResource(R.drawable.perfil_padrao);
        }

        EdtNome.setText(usuarioLogado.getNome());
        EdtSobre.setText(usuarioLogado.getSobre());
        EdtEsportes.setText(usuarioLogado.getEsportes());
        if (usuarioLogado.getRaioDeKm() != 0){
            seekBarRaioKm.setProgress((int) usuarioLogado.getRaioDeKm());
        }
        else{
            seekBarRaioKm.setProgress(1);
        }
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
        ToolBarConfiguracoes = findViewById(R.id.toolBarPrincipal);
        seekBarRaioKm = findViewById(R.id.seekBarRaioDeKm);
        textViewDistanciaMaxima = findViewById(R.id.textDistanciaMaxima);
    }

    public void ConfigurarBtnCameraClick(){
        BtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues parametroParaCapturaDeFotoDaCamera = new ContentValues();
                parametroParaCapturaDeFotoDaCamera.put(MediaStore.Images.Media.TITLE, FOTO_PERFIL_SPORTAPP);
                parametroParaCapturaDeFotoDaCamera.put(MediaStore.Images.Media.DESCRIPTION, FOTO_DA_CAMERA);
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
        builder.setPositiveButton(OK, new DialogInterface.OnClickListener() {
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

    private void SalvarImagemPerfilFireBase() {
        if (LocalFotoPerfil != null){
            ProgressBarImagem.setVisibility(View.VISIBLE);
            InputStream imagemStream = null;
            try {
                imagemStream = getContentResolver().openInputStream(LocalFotoPerfil);
                usuarioDaoFireBase.AtualizarImagemPerfilUsuario(imagemStream, usuarioLogado);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void AtualizarDadosUsuario(){
        usuarioLogado.setNome(EdtNome.getText().toString());
        usuarioLogado.setSobre(EdtSobre.getText().toString());
        usuarioLogado.setEsportes(EdtEsportes.getText().toString());
        usuarioLogado.setRaioDeKm(seekBarRaioKm.getProgress());
        ProgressBarConfig.setVisibility(View.VISIBLE);
        usuarioDaoFireBase.GravarUsuarioNoBancoDeDados(usuarioLogado,true);

    }

}
