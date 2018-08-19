package com.giovanni.sportapp.sportapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.Observable;

public class CaptorDeLocalizacao extends Observable {

    private FusedLocationProviderClient mFusedLocationClient;
    private Location localAtualDispositivo;

    public Task<Location> BuscarLocalizacaoDoDispositivo(Activity activity){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        return mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                localAtualDispositivo = location;
                setChanged();
                notifyObservers();
            }
        });
    }

    public Location getLocalAtualDispositivo(){
        return localAtualDispositivo;
    }

}
