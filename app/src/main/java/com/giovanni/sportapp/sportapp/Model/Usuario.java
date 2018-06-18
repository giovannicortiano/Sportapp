package com.giovanni.sportapp.sportapp.Model;

import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Usuario implements Serializable{
    private String Id;
    private String Nome;
    private String Email;
    private String Senha;
    private String Sobre;
    private String Esportes;
    private String FotoUrl;

    public Usuario() {
    }

    public void GravarNoBancoDeDados(){
        DatabaseReference referenciaFireBase = ConfiguradorFireBase.getBancoDeDadosFireBase();
        referenciaFireBase.child("Usuarios").child(getId()).setValue(this);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSobre() {
        return Sobre;
    }

    public void setSobre(String sobre) {
        Sobre = sobre;
    }

    public String getEsportes() {
        return Esportes;
    }

    public void setEsportes(String esportes) {
        Esportes = esportes;
    }

    public String getNome() {
        return Nome;
    }

    public String getEmail() {
        return Email;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public void setEmail(String email) {
        Email = email;
    }


    public void setSenha(String senha) {
        Senha = senha;
    }

    @Exclude
    public String getSenha() {
        return Senha;
    }


    public String getFotoUrl() {
        return FotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        FotoUrl = fotoUrl;
    }

}
