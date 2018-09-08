package com.giovanni.sportapp.sportapp.Model;

public interface AutenticadorDeUsuario {

    void EnviarEmailDeConfirmacao();

    void EnviarEmailRecuperacaoDeSenha(String email);

    void AutenticarUsuario(String email, String senha);

    boolean VerificarSeUsuarioAutenticado();

    boolean VerificarSeEmailConfirmado();

    void AddObserver(Object observer);

    void RemoverObserver(Object observer);

    String getMsgEmailSenhaSucesso();

    String getMsgEmailConfirmacaoSucesso();

    String RetornarEmailUsuarioLogado();

    void FazerLogoffUsuarioLogado();

}
