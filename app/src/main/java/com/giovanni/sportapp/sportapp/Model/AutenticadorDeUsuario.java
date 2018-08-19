package com.giovanni.sportapp.sportapp.Model;


import java.util.Observer;

public interface AutenticadorDeUsuario {

    void EnviarEmailDeConfirmacao();

    void EnviarEmailRecuperacaoDeSenha(String email);

    void AutenticarUsuario(String email, String senha);

    boolean VerificarSeUsuarioAutenticado();

    boolean VerificarSeEmailConfirmado();

    void AddObserver(Observer observer);

    void RemoveObserver(Observer observer);

    String getMsgEmailSenhaSucesso();

    String getMsgEmailConfirmacaoSucesso();

    String RetornarEmailUsuarioLogado();

    void FazerLogoffUsuarioLogado();

}
