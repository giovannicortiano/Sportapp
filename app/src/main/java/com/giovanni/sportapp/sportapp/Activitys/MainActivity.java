package com.giovanni.sportapp.sportapp.Activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSair = findViewById(R.id.btnSair);
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser();
                ConfiguradorFireBase.getAutenticadorFireBase().signOut();
                finish();
            }
        });
    }
}
