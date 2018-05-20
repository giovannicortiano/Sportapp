package com.giovanni.sportapp.sportapp.Activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText edtNomeCadastroUsuario;
    private EditText edtSenhaCadastroUsuario;
    private EditText edtEmailCadastroUsuario;
    private Button btnCadastrarUsuario;
    private FirebaseAuth AutenticadorFireBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        RecuperarViews();

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

    private void EnviarEmailConfirmacao(){
        FirebaseUser UsuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        UsuarioAtual.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CadastroUsuarioActivity.this,R.string.UsuarioCadastradoComSucesso,Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else{
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthEmailException e){
                                Toast.makeText(CadastroUsuarioActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                Toast.makeText(CadastroUsuarioActivity.this,R.string.ErroDesconhecido + " " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void CadastrarUsuario(){
        Usuario NovoUsuario = new Usuario();
        NovoUsuario.setNome(edtNomeCadastroUsuario.getText().toString());
        NovoUsuario.setSenha(edtSenhaCadastroUsuario.getText().toString());
        NovoUsuario.setEmail(edtEmailCadastroUsuario.getText().toString());

        AutenticadorFireBase = ConfiguradorFireBase.getAutenticadorFireBase();
        AutenticadorFireBase.createUserWithEmailAndPassword(NovoUsuario.getEmail(),NovoUsuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    EnviarEmailConfirmacao();
                }
                else{
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        Toast.makeText(getApplicationContext(),R.string.DigiteSenhaMaisForte ,Toast.LENGTH_SHORT).show();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(getApplicationContext(),R.string.EmailInvalido ,Toast.LENGTH_SHORT).show();
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        Toast.makeText(getApplicationContext(),R.string.EmailJaCadastrado ,Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),R.string.ErroDesconhecido + e.getMessage() ,Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
