package com.giovanni.sportapp.sportapp.Model;

public interface UsuarioDao  {

    void ConsultarUsuarioPorId(String id);

    void GravarUsuarioNoBancoDeDados(Usuario usuario, boolean notificar);

    void ConsultarUsuariosPorLocalizacao(double latitude, double longitude, double raioDeKm);

    String RetornarIdUsuarioLogado();

    void AtualizarImagemPerfilUsuario(Object imagem, Usuario usuario);

    void CriarNovoUsuario(Usuario novoUsuario);

    void AddObserver(Object observer);

    void RemoverObserver(Object observer);

    String getMsgSucessoSalvarUsuario();
}
