package com.giovanni.sportapp.sportapp.Activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.giovanni.sportapp.sportapp.Model.AutenticadorDeUsuario;
import com.giovanni.sportapp.sportapp.Model.AutenticadorDeUsuarioFirebase;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.Model.UsuarioDao;
import com.giovanni.sportapp.sportapp.Model.UsuarioDaoFirebase;
import com.giovanni.sportapp.sportapp.R;
import java.util.Observable;
import java.util.Observer;

public class CadastroUsuarioActivity extends AppCompatActivity implements Observer {

    private EditText              edtNomeCadastroUsuario;
    private EditText              edtSenhaCadastroUsuario;
    private EditText              edtEmailCadastroUsuario;
    private Button                btnCadastrarUsuario;
    private ProgressBar           progressBarCadastro;
    private Usuario               novoUsuario;
    private UsuarioDao            usuarioDao;
    private AutenticadorDeUsuario autenticadorDeUsuario;
    private static final int      RAIO_DE_KM_PADRAO = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        RecuperarViews();
        usuarioDao = new UsuarioDaoFirebase();
        usuarioDao.AddObserver(this);

        autenticadorDeUsuario = new AutenticadorDeUsuarioFirebase();
        autenticadorDeUsuario.AddObserver(this);

        btnCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidarPreenchimentoDosCampos()){
                    CadastrarUsuario();
                }
            }
        });
    }

    private void RecuperarViews(){
        edtNomeCadastroUsuario = findViewById(R.id.edtNomeCadastro);
        edtSenhaCadastroUsuario = findViewById(R.id.edtSenhaCadastro);
        edtEmailCadastroUsuario = findViewById(R.id.edtEmailCadastro);
        btnCadastrarUsuario = findViewById(R.id.btnCadastrar);
        progressBarCadastro = findViewById(R.id.progressCadastro);
    }

    public boolean ValidarPreenchimentoDosCampos(){
        if (edtNomeCadastroUsuario.getText().toString().isEmpty()){
            Toast.makeText(CadastroUsuarioActivity.this,R.string.CampoNomeObrigatorio,Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if (edtEmailCadastroUsuario.getText().toString().isEmpty()){
            Toast.makeText(CadastroUsuarioActivity.this,R.string.CampoEmailObrigatorio,Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if (edtSenhaCadastroUsuario.getText().toString().isEmpty()){
            Toast.makeText(CadastroUsuarioActivity.this,R.string.CampoSenhaObrigatorio,Toast.LENGTH_SHORT).show();
            return  false;
        }
        return  true;
    }

    private void CadastrarUsuario(){
        novoUsuario = new Usuario();
        novoUsuario.setNome(edtNomeCadastroUsuario.getText().toString());
        novoUsuario.setSenha(edtSenhaCadastroUsuario.getText().toString());
        novoUsuario.setEmail(edtEmailCadastroUsuario.getText().toString());
        novoUsuario.setRaioDeKm(RAIO_DE_KM_PADRAO);
        progressBarCadastro.setVisibility(View.VISIBLE);
        usuarioDao.CriarNovoUsuario(novoUsuario);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UsuarioDaoFirebase){
            progressBarCadastro.setVisibility(View.INVISIBLE);
            if (arg != null &&  arg instanceof String){
                Toast.makeText(CadastroUsuarioActivity.this,(String) arg,Toast.LENGTH_SHORT).show();
                if (usuarioDao.getMsgSucessoSalvarUsuario().equals(arg)){
                    progressBarCadastro.setVisibility(View.VISIBLE);
                    autenticadorDeUsuario.EnviarEmailDeConfirmacao();
                }

            }
        }
        else if (o instanceof  AutenticadorDeUsuarioFirebase){
            progressBarCadastro.setVisibility(View.INVISIBLE);
            if (arg != null &&  arg instanceof String){
                Toast.makeText(CadastroUsuarioActivity.this,(String) arg,Toast.LENGTH_SHORT).show();
                if(autenticadorDeUsuario.getMsgEmailConfirmacaoSucesso().equals(arg)){
                    finish();
                }
            }
        }
    }
}
