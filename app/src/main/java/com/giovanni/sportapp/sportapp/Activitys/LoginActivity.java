package com.giovanni.sportapp.sportapp.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.giovanni.sportapp.sportapp.Model.AutenticadorDeUsuario;
import com.giovanni.sportapp.sportapp.Model.AutenticadorDeUsuarioFirebase;
import com.giovanni.sportapp.sportapp.R;

import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements Observer {

    private Button                btnEntrar;
    private TextView              textViewCadastreSe;
    private TextView              textViewRecuperarSenha;
    private EditText              edtEmailLogin;
    private EditText              edtSenhaLogin;
    private TextView              textViewConfirmarEmail;
    private ProgressBar           progressBarLogin;
    private AutenticadorDeUsuario autenticadorDeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RecuperarViews();
        autenticadorDeUsuario = new AutenticadorDeUsuarioFirebase();
        autenticadorDeUsuario.AddObserver(this);
        VincularTextViewCadastreSeOnClick();
        VincularTextViewRecuperarSenhaOnClick();
        VincularBtnEntrarOnClick();
        VincularTextViewEmailNaoConfirmadoOnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        VerificarUsuarioLogado();
    }

    private void RecuperarViews(){
        btnEntrar = findViewById(R.id.btnEntrar);
        textViewCadastreSe = findViewById(R.id.TextViewCadastreSe);
        textViewRecuperarSenha = findViewById(R.id.TextViewEsqueceuSenha);
        edtEmailLogin = findViewById(R.id.edtEmailLogin);
        edtSenhaLogin = findViewById(R.id.edtSenhaLogin);
        textViewConfirmarEmail = findViewById(R.id.TextViewConfirmarEmail);
        progressBarLogin = findViewById(R.id.progressLogin);
        edtEmailLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtEmailLogin.getText().toString().isEmpty()) {
                    textViewConfirmarEmail.setVisibility(View.INVISIBLE);
                    autenticadorDeUsuario.FazerLogoffUsuarioLogado();
                }
            }
        });
    }

    private void VincularTextViewEmailNaoConfirmadoOnClick(){
        textViewConfirmarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarLogin.setVisibility(View.VISIBLE);
                autenticadorDeUsuario.EnviarEmailDeConfirmacao();
            }
        });
    }

    private void VincularTextViewCadastreSeOnClick(){
        textViewCadastreSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CadastroUsuarioActivity.class));
            }
        });
    }

    private void VincularTextViewRecuperarSenhaOnClick(){
        textViewRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RecuperarSenhaActivity.class));
            }
        });
    }

    private void AutenticarUsuario(){
        progressBarLogin.setVisibility(View.VISIBLE);
        autenticadorDeUsuario.AutenticarUsuario(edtEmailLogin.getText().toString(),
                edtSenhaLogin.getText().toString());
    }

    private void VincularBtnEntrarOnClick(){
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidarPreenchimentoDosCampos()) {
                    AutenticarUsuario();
                }
            }
        });
    }

    private boolean ValidarPreenchimentoDosCampos(){
        if (edtEmailLogin.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.CampoEmailObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (edtSenhaLogin.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.CampoSenhaObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void VerificarUsuarioLogado(){
        if (autenticadorDeUsuario.VerificarSeUsuarioAutenticado()){
            if (autenticadorDeUsuario.VerificarSeEmailConfirmado()){
                textViewConfirmarEmail.setVisibility(View.INVISIBLE);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                progressBarLogin.setVisibility(View.INVISIBLE);
            }
            else{
                textViewConfirmarEmail.setVisibility(View.VISIBLE);
                edtEmailLogin.setText(autenticadorDeUsuario.RetornarEmailUsuarioLogado());
                progressBarLogin.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        progressBarLogin.setVisibility(View.INVISIBLE);
        if (o instanceof AutenticadorDeUsuarioFirebase){
            if (arg != null && arg instanceof String){
                Toast.makeText(getApplicationContext(),(String) arg,Toast.LENGTH_SHORT).show();
            }
            VerificarUsuarioLogado();
        }

    }
}
