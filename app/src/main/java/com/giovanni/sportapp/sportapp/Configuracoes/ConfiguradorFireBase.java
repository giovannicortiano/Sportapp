package com.giovanni.sportapp.sportapp.Configuracoes;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguradorFireBase {
    private static DatabaseReference BancoDeDadosFireBase;
    private static FirebaseAuth AutenticadorFireBase;

    public static DatabaseReference getBancoDeDadosFireBase(){
        if (BancoDeDadosFireBase == null) {
             BancoDeDadosFireBase = FirebaseDatabase.getInstance().getReference();
        }
        return  BancoDeDadosFireBase;
    }

    public static FirebaseAuth getAutenticadorFireBase(){
        if (AutenticadorFireBase == null){
            AutenticadorFireBase = FirebaseAuth.getInstance();
        }

        return AutenticadorFireBase;
    }
}
