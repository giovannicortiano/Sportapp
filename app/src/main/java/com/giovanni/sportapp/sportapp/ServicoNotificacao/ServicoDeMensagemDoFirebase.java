package com.giovanni.sportapp.sportapp.ServicoNotificacao;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.giovanni.sportapp.sportapp.Model.UsuarioDaoFirebase;
import com.giovanni.sportapp.sportapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class ServicoDeMensagemDoFirebase extends FirebaseMessagingService {

    private static final int ID_NOTIFICACAO_MENSAGEM = 0;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        ///Caso o Token mude enquanto o usuário está logado, este token já é atualizado aqui.
        ///Caso seja o primiero acesso, o token chega antes do usuário logar, então grava no sharedPreferences
        ////para depois que o usuário logar, atribuir o token ao usuário na no banco de dados.
        UsuarioDaoFirebase usuarioDaoFirebase = new UsuarioDaoFirebase();
        usuarioDaoFirebase.AtualizarTokenUsuarioLogado(s);


        SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.ChaveSharedPreferences),MODE_PRIVATE).edit();
        editor.putString(getResources().getString(R.string.ChaveToken), s);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        MostrarNotificacao(remoteMessage.getNotification());
    }

    public void MostrarNotificacao(RemoteMessage.Notification notificacao){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),getResources().getString(R.string.Id_canal_de_notificacao));
        builder.setSmallIcon(R.drawable.ic_comment_white_24dp);
        builder.setContentTitle(notificacao.getTitle());
        builder.setContentText(notificacao.getBody());
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(ID_NOTIFICACAO_MENSAGEM,builder.build());
    }

}
