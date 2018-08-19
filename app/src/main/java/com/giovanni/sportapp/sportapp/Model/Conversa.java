package com.giovanni.sportapp.sportapp.Model;


import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {

    private String IdRemetente;
    private String IdDestinatario;
    private String UltimaMensagem;
    private Usuario usuarioExibicao;

    public Conversa() {
    }

    public String getIdRemetente() {
        return IdRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        IdRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return IdDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        IdDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return UltimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        UltimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuario) {
        this.usuarioExibicao = usuario;
    }

    public void SalvarConversa(){
        DatabaseReference BancoDeDados = ConfiguradorFireBase.getBancoDeDadosFireBase();
        DatabaseReference MensagemReferencia = BancoDeDados.child("conversas");

        MensagemReferencia.child(this.getIdRemetente()).child(this.getIdDestinatario()).setValue(this);

        // MensagemReferencia = BancoDeDados.child("mensagens");
        ///MensagemReferencia.child(this.getIdDestinatario()).child(this.getIdRemetente()).setValue(this);
    }
}
