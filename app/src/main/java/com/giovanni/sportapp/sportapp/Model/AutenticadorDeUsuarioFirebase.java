package com.giovanni.sportapp.sportapp.Model;


import android.support.annotation.NonNull;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import java.util.Observable;
import java.util.Observer;

public class AutenticadorDeUsuarioFirebase extends Observable implements AutenticadorDeUsuario {

    private static final String MSG_EMAIL_SENHA_SUCESSO = "E-mail para recuperação de senha enviado. Verifique sua caixa de entrada.";
    private static final String MSG_EMAIL_NAO_CADASTRADO = "E-mail não cadastrado.";
    private static final String MSG_ERRO_DESCONHECIDO = "Erro desconhecido. Tente novamente mais tarde.";
    private static final String MSG_USUARIO_NAO_CADASTRADO = "Usuário não cadastado.";
    private static final String MSG_SENHA_INCORRETA = "Senha incorreta.";
    private static final String MSG_EMAIL_CONFIRMACAO_SUCESSO = "E-mail de confirmação enviado. Verifique sua caixa de entrada.";

    @Override
    public void EnviarEmailDeConfirmacao() {
        FirebaseUser usuarioLogado = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser();
        if (usuarioLogado != null){
            usuarioLogado.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        setChanged();
                        notifyObservers(MSG_EMAIL_CONFIRMACAO_SUCESSO);
                    }
                    else{
                        try{
                            throw task.getException();
                        }
                        catch (FirebaseAuthInvalidUserException e){
                            setChanged();
                            notifyObservers(MSG_EMAIL_NAO_CADASTRADO);
                        }
                        catch (Exception e){
                            setChanged();
                            notifyObservers(MSG_ERRO_DESCONHECIDO);
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void EnviarEmailRecuperacaoDeSenha(String email) {
        ConfiguradorFireBase.getAutenticadorFireBase().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    setChanged();
                    notifyObservers(MSG_EMAIL_SENHA_SUCESSO);
                }
                else {
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        setChanged();
                        notifyObservers(MSG_EMAIL_NAO_CADASTRADO);
                    }
                    catch (Exception e){
                        setChanged();
                        notifyObservers(MSG_ERRO_DESCONHECIDO);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void AutenticarUsuario(String email, String senha) {
        ConfiguradorFireBase.getAutenticadorFireBase().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setChanged();
                            notifyObservers();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                setChanged();
                                notifyObservers(MSG_USUARIO_NAO_CADASTRADO);
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                setChanged();
                                notifyObservers(MSG_SENHA_INCORRETA);
                            } catch (Exception e) {
                                setChanged();
                                notifyObservers(MSG_ERRO_DESCONHECIDO);
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public boolean VerificarSeUsuarioAutenticado() {
        if (ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser() != null){
            return true;
        }
        return false;
    }

    @Override
    public boolean VerificarSeEmailConfirmado() {
        if (VerificarSeUsuarioAutenticado()) {
            if (ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser().isEmailVerified()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void AddObserver(Observer observer) {
        addObserver(observer);
    }

    @Override
    public void RemoveObserver(Observer observer) {
        RemoveObserver(observer);
    }

    @Override
    public String getMsgEmailSenhaSucesso() {
        return MSG_EMAIL_SENHA_SUCESSO;
    }

    @Override
    public String getMsgEmailConfirmacaoSucesso() {
        return MSG_EMAIL_CONFIRMACAO_SUCESSO;
    }

    @Override
    public String RetornarEmailUsuarioLogado() {
        String email = "";
        if (ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser() != null){
            email = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser().getEmail();
        }
        return email;
    }

    @Override
    public void FazerLogoffUsuarioLogado() {
        ConfiguradorFireBase.getAutenticadorFireBase().signOut();
    }

}
