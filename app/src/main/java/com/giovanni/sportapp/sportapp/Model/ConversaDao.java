package com.giovanni.sportapp.sportapp.Model;

public interface ConversaDao {

    void ListarConversasDoUsuario(String idUsuario);

    void GravarConversaNoBancoDeDados(Conversa conversaRemetente, Conversa conversaDestinatario);

    void AdicionarObserver(Object observer);

    void RemoverObserver(Object observer);
}
