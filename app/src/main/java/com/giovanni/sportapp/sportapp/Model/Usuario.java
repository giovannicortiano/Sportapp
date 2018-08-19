package com.giovanni.sportapp.sportapp.Model;


import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.List;

public class Usuario implements Serializable{
    private String Id;
    private String Nome;
    private String Email;
    private String Senha;
    private String Sobre;
    private String Esportes;
    private String FotoUrl;
    private String g;///padrão para nome do HashGeo
    private List<Double> l;///padrão para gravar longitude/latitude
    private double RaioDeKm;

    public Usuario() {
    }

    public double getRaioDeKm() {
        return RaioDeKm;
    }

    public void setRaioDeKm(double raioDeKm) {
        RaioDeKm = raioDeKm;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public List<Double> getL() {
        return l;
    }

    public void setL(List<Double> l) {
        this.l = l;
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
