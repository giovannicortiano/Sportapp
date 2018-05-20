package com.giovanni.sportapp.sportapp.Model;

public class Usuario {
    private String Nome;
    private String Email;
    private String Senha;

    public Usuario() {
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

    public String getSenha() {
        return Senha;
    }
}
