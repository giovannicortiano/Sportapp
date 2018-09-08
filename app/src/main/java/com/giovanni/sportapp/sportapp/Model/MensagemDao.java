package com.giovanni.sportapp.sportapp.Model;


public interface MensagemDao {

    void RecuperarMensagens(String idUsuarioLogado, String IdUsuarioDestinatario);

    void GravarMensagem(Object msg);

    void RemoverMensagem(String sIdMensagem, String IdUsuarioDestinatario, String IdUsuarioRementente);

    void AtualizarMensagemParaLida(Object msg);

    void AdicionarObserver(Object observer);

    void RemoverObserver(Object observer);
}
