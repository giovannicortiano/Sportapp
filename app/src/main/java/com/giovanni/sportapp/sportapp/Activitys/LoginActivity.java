package com.giovanni.sportapp.sportapp.Activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button btnEntrar;
    private TextView TextViewCadastreSe;
    private TextView TextViewRecuperarSenha;
    private EditText EdtEmailLogin;
    private EditText EdtSenhaLogin;
    private TextView TextViewConfirmarEmail;
    private FirebaseAuth AutenticadorFireBase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RecuperarViews();
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
        TextViewCadastreSe = findViewById(R.id.TextViewCadastreSe);
        TextViewRecuperarSenha = findViewById(R.id.TextViewEsqueceuSenha);
        EdtEmailLogin = findViewById(R.id.edtEmailLogin);
        EdtSenhaLogin = findViewById(R.id.edtSenhaLogin);
        TextViewConfirmarEmail = findViewById(R.id.TextViewConfirmarEmail);
    }

    private void VincularTextViewEmailNaoConfirmadoOnClick(){
        TextViewConfirmarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser UsuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
                if (UsuarioAtual == null){
                    return;
                }

                UsuarioAtual.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,R.string.EmailReenviado,Toast.LENGTH_LONG).show();
                        }
                        else{
                            try{
                                throw task.getException();
                                        }
                            catch (FirebaseAuthEmailException e){
                                Toast.makeText(LoginActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                Toast.makeText(LoginActivity.this,R.string.ErroDesconhecido + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

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

    private void AutenticarUsuario(){
        AutenticadorFireBase = ConfiguradorFireBase.getAutenticadorFireBase();

        AutenticadorFireBase.signInWithEmailAndPassword(EdtEmailLogin.getText().toString(),EdtSenhaLogin.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser UsuarioAtual = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser();
                            if (UsuarioAtual.isEmailVerified()) {
                                TextViewConfirmarEmail.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            else{
                                TextViewConfirmarEmail.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(getApplicationContext(),R.string.UsuarioNaoCadastrado ,Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(getApplicationContext(),R.string.UsuarioOuSenhaInva ,Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(),R.string.ErroDesconhecido + ". " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                });
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
        if (EdtEmailLogin.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.CampoEmailObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (EdtSenhaLogin.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.CampoSenhaObrigatorio,Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void VerificarUsuarioLogado(){
        FirebaseUser UsuarioAtual;
        AutenticadorFireBase = ConfiguradorFireBase.getAutenticadorFireBase();

        UsuarioAtual = AutenticadorFireBase.getCurrentUser();

        if (UsuarioAtual != null){
            if (UsuarioAtual.isEmailVerified()) {
                TextViewConfirmarEmail.setVisibility(View.INVISIBLE);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
            else{
                TextViewConfirmarEmail.setVisibility(View.VISIBLE);
                EdtEmailLogin.setText(UsuarioAtual.getEmail());
            }
        }
    }
}
