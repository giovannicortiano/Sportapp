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
import com.giovanni.sportapp.sportapp.R;
import java.util.Observable;
import java.util.Observer;

public class RecuperarSenhaActivity extends AppCompatActivity implements Observer{
    private EditText              edtEmailRecuperarSenha;
    private Button                btnRecuperarSenha;
    private ProgressBar           progressBarEmail;
    private AutenticadorDeUsuario autenticadorDeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);
        RecuperarViews();
        autenticadorDeUsuario = new AutenticadorDeUsuarioFirebase();
        autenticadorDeUsuario.AddObserver(this);
        btnRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidarPreechimentoDosCampos()){
                    EnviarEmailRecuperacaoSenha();
                }
            }
        });
    }

    private void RecuperarViews(){
        edtEmailRecuperarSenha = findViewById(R.id.edtEmailRecuperarSenha);
        btnRecuperarSenha = findViewById(R.id.btnRecuperarSenha);
        progressBarEmail = findViewById(R.id.progressEmail);
    }

    private boolean ValidarPreechimentoDosCampos(){
        if (edtEmailRecuperarSenha.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.CampoEmailObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void EnviarEmailRecuperacaoSenha(){
        progressBarEmail.setVisibility(View.VISIBLE);
        autenticadorDeUsuario.EnviarEmailRecuperacaoDeSenha(edtEmailRecuperarSenha.getText().toString());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof AutenticadorDeUsuarioFirebase){
            progressBarEmail.setVisibility(View.INVISIBLE);
            if (arg instanceof String){
                Toast.makeText(getApplicationContext(),(String) arg,Toast.LENGTH_SHORT).show();
                if (autenticadorDeUsuario.getMsgEmailSenhaSucesso().equals(arg)){
                    finish();
                }
            }
        }
    }
}
