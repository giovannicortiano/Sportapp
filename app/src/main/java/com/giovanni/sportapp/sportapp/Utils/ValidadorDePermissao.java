package com.giovanni.sportapp.sportapp.Utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class ValidadorDePermissao {

    public static boolean ValidarPermissoes(Activity activity, String[]Permissoes){

        ///só entra se for 23 ou superior (Android Marchmellow ou superior).
        if (Build.VERSION.SDK_INT >= 23){
            List<String> listaDeItensNaoPermitidos = new ArrayList<>();
            for (String permissao : Permissoes){
                if (ContextCompat.checkSelfPermission(activity,permissao) != PackageManager.PERMISSION_GRANTED){
                    listaDeItensNaoPermitidos.add(permissao);
                }
            }

            String [] ArrayDeItensNaoPermitidos = new String[listaDeItensNaoPermitidos.size()];
            listaDeItensNaoPermitidos.toArray(ArrayDeItensNaoPermitidos);

            if (! listaDeItensNaoPermitidos.isEmpty()){
                ActivityCompat.requestPermissions(activity,ArrayDeItensNaoPermitidos,1);
                return true;
            }
        }
        return true;
    }
}
