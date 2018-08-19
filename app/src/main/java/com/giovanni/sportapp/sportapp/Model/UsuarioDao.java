package com.giovanni.sportapp.sportapp.Model;


import java.io.InputStream;
import java.util.Observer;

public interface UsuarioDao  {

    void ConsultarUsuarioPorId(String id);

    void GravarUsuarioNoBancoDeDados(Usuario usuario, boolean notificar);

    void ConsultarUsuariosPorLocalizacao(double latitude, double longitude, double raioDeKm);

    String RetornarIdUsuarioLogado();

    void AtualizarImagemPerfilUsuario(InputStream imagem, Usuario usuario);

    void CriarNovoUsuario(Usuario novoUsuario);

    void AddObserver(Observer observer);

    void RemoverObserver(Observer observer);

    String getMsgSucessoSalvarUsuario();
}
