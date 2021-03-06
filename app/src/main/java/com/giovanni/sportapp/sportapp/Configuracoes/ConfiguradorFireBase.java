package com.giovanni.sportapp.sportapp.Configuracoes;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguradorFireBase {
    private static DatabaseReference BancoDeDadosFireBase;
    private static FirebaseAuth AutenticadorFireBase;
    private static StorageReference ArmazenadorDeImagensFireBase;

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

    public static StorageReference getArmazenadorDeImagensFireBase(){
        if (ArmazenadorDeImagensFireBase == null){
            ArmazenadorDeImagensFireBase = FirebaseStorage.getInstance().getReference();
        }
        return ArmazenadorDeImagensFireBase;
    }
}
