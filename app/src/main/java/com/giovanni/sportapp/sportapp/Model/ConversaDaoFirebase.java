package com.giovanni.sportapp.sportapp.Model;


import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ConversaDaoFirebase extends Observable implements ConversaDao {

    private static final String NO_DE_CONVERSAS = "conversas";
    private static final String NO_DE_NOTIFICACOES = "notificacoes";
    private static final String NO_DE_MENSAGEM = "mensagem";
    private ChildEventListener childEventListenerConversas;
    private DatabaseReference conversasRef;
    private ArrayList<Conversa> listaDeConversaInterna;

    public ConversaDaoFirebase(ArrayList l) {
        listaDeConversaInterna = l;
    }

    public ConversaDaoFirebase(){listaDeConversaInterna = new ArrayList<Conversa>();}

    @Override
    public void ListarConversasDoUsuario(String idUsuario) {
        conversasRef = ConfiguradorFireBase.getBancoDeDadosFireBase().child(NO_DE_CONVERSAS).child(idUsuario);
        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversa conversa  = dataSnapshot.getValue(Conversa.class);
                if (RetornarIndiceDaConversaoNaLista(conversa) == -1){
                    listaDeConversaInterna.add(conversa);
                    setChanged();
                    notifyObservers(conversa);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Conversa conversa  = dataSnapshot.getValue(Conversa.class);
                int indiceMensagem = RetornarIndiceDaConversaoNaLista(conversa);
                if (indiceMensagem != -1){
                    listaDeConversaInterna.set(indiceMensagem,conversa);
                    setChanged();
                    notifyObservers(conversa);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Conversa conversa  = dataSnapshot.getValue(Conversa.class);
                int indiceMensagem = RetornarIndiceDaConversaoNaLista(conversa);
                if (indiceMensagem != -1){
                    listaDeConversaInterna.remove(indiceMensagem);
                    setChanged();
                    notifyObservers(conversa);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void GravarConversaNoBancoDeDados(Conversa conversaRemetente, Conversa conversaDestinatario) {
        DatabaseReference BancoDeDados = ConfiguradorFireBase.getBancoDeDadosFireBase();
        DatabaseReference MensagemReferencia = BancoDeDados.child(NO_DE_CONVERSAS);
        MensagemReferencia.child(conversaRemetente.getIdRemetente()).child(conversaRemetente.getIdDestinatario()).setValue(conversaRemetente);

        MensagemReferencia = BancoDeDados.child(NO_DE_CONVERSAS);
        MensagemReferencia.child(conversaDestinatario.getIdRemetente()).child(conversaDestinatario.getIdDestinatario()).setValue(conversaDestinatario);

        String idNotificacao = BancoDeDados.child(NO_DE_NOTIFICACOES).push().getKey();
        BancoDeDados.child(NO_DE_NOTIFICACOES).child(idNotificacao).child(conversaDestinatario
                .getIdRemetente()).child(conversaDestinatario.getIdDestinatario()).child(NO_DE_MENSAGEM)
                .setValue(conversaDestinatario.getUltimaMensagem());

    }

    @Override
    public void AdicionarObserver(Object observer) {
        if (observer instanceof Observer){
            addObserver((Observer) observer);
        }
    }

    @Override
    public void RemoverObserver(Object observer) {
        if (observer instanceof Observer){
            deleteObserver((Observer) observer);
        }

        if (countObservers() == 0) {
            if (conversasRef != null && childEventListenerConversas != null){
                conversasRef.removeEventListener(childEventListenerConversas);
            }
        }
    }

    private int RetornarIndiceDaConversaoNaLista(Conversa c){
        for (int i = 0; i < listaDeConversaInterna.size(); i++){
            if (listaDeConversaInterna.get(i).getIdDestinatario().equals(c.getIdDestinatario())){
                return i;
            }
        }
        return -1;
    }

}
