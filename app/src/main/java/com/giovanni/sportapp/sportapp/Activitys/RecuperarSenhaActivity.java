package com.giovanni.sportapp.sportapp.Activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class RecuperarSenhaActivity extends AppCompatActivity {
    private EditText edtEmailRecuperarSenha;
    private Button btnRecuperarSenha;
    private ProgressBar ProgressBarEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);
        RecuperarViews();
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
        ProgressBarEmail = findViewById(R.id.progressEmail);
    }

    private boolean ValidarPreechimentoDosCampos(){
        if (edtEmailRecuperarSenha.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.CampoEmailObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void EnviarEmailRecuperacaoSenha(){
        FirebaseAuth AutenticadorFireBase = ConfiguradorFireBase.getAutenticadorFireBase();
        ProgressBarEmail.setVisibility(View.VISIBLE);
        AutenticadorFireBase.sendPasswordResetEmail(edtEmailRecuperarSenha.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),R.string.EmailEnviadoPara + edtEmailRecuperarSenha.getText().toString(),Toast.LENGTH_SHORT).show();
                    ProgressBarEmail.setVisibility(View.INVISIBLE);
                    finish();
                }
                else {
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        Toast.makeText(RecuperarSenhaActivity.this, R.string.EmailNaoCastrado,Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),R.string.ErroDesconhecido + ". " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    ProgressBarEmail.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

}
