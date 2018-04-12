package com.giovanni.sportapp.sportapp.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.giovanni.sportapp.sportapp.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnEntrar;
    private TextView TextViewCadastreSe;
    private TextView TextViewRecuperarSenha;

    private void RecuperarViews(){
        btnEntrar = findViewById(R.id.btnEntrarId);
        TextViewCadastreSe = findViewById(R.id.TextViewCadastreSeId);
        TextViewRecuperarSenha = findViewById(R.id.TextViewEsqueceuSenhaId);
    }

    private void VincularTextViewCadastreSeOnClick(){
        TextViewCadastreSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CadastroUsuarioActivity.class));
            }
        });
    }

    private void VincularTextViewRecuperarSenhaOnClick(){
        TextViewRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RecuperarSenhaActivity.class));
            }
        });
    }

    private void VincularBtnEntrarOnClick(){
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Login Clique",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RecuperarViews();
        VincularTextViewCadastreSeOnClick();
        VincularTextViewRecuperarSenhaOnClick();
        VincularBtnEntrarOnClick();
    }
}
