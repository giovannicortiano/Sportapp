package com.giovanni.sportapp.sportapp.Model;


public class Mensagem {
    private String IdMensagem;
    private String IdUsuarioRemetente;
    private String Mensagem;
    private String IdUsuarioDestinatario;
    private boolean MensagemLida;
    private String DataEHora;

    public String getIdMensagem() {
        return IdMensagem;
    }

    public void setIdMensagem(String idMensagem) {
        IdMensagem = idMensagem;
    }

    public boolean isMensagemLida() {
        return MensagemLida;
    }

    public void setMensagemLida(boolean mensagemLida) {
        MensagemLida = mensagemLida;
    }

    public String getDataEHora() {
        return DataEHora;
    }

    public void setDataEHora(String dataEHora) {
        DataEHora = dataEHora;
    }

    public String getIdUsuarioRemetente() {
        return IdUsuarioRemetente;
    }

    public Mensagem() {
    }

    public void setIdUsuarioRemetente(String idUsuarioRemetente) {
        IdUsuarioRemetente = idUsuarioRemetente;
    }

    public String getMensagem() {
        return Mensagem;
    }

    public void setMensagem(String mensagem) {
        Mensagem = mensagem;
    }

    public String getIdUsuarioDestinatario() {
        return IdUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(String idUsuarioDestinatario) {
        IdUsuarioDestinatario = idUsuarioDestinatario;
    }
}
