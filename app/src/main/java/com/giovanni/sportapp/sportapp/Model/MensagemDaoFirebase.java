package com.giovanni.sportapp.sportapp.Model;

import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MensagemDaoFirebase extends Observable implements MensagemDao{

    private DatabaseReference bancoDeDados;
    private ChildEventListener childEventListenerMensagens;
    private final static String NO_DE_MENSAGENS = "mensagens";
    private ArrayList<Mensagem> listaDeMensagens;


    public MensagemDaoFirebase(ArrayList l) {
        listaDeMensagens = l;
    }

    public MensagemDaoFirebase(){listaDeMensagens = new ArrayList<>();}

    @Override
    public void RecuperarMensagens(String idUsuarioLogado, String IdUsuarioDestinatario) {
        bancoDeDados = ConfiguradorFireBase.getBancoDeDadosFireBase();
        DatabaseReference mensagensReferencia = bancoDeDados.child(NO_DE_MENSAGENS)
                .child(idUsuarioLogado)
                .child(IdUsuarioDestinatario);

        childEventListenerMensagens = mensagensReferencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem msg = dataSnapshot.getValue(Mensagem.class);
                if (RetornarIndiceDaMensagemNaLista(msg) == - 1){
                    listaDeMensagens.add(msg);
                    setChanged();
                    notifyObservers(msg);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Mensagem msg = dataSnapshot.getValue(Mensagem.class);
                int PosicaoMensagem = RetornarIndiceDaMensagemNaLista(msg);

                if (PosicaoMensagem != -1){
                    setChanged();
                    notifyObservers(msg);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Mensagem msg = dataSnapshot.getValue(Mensagem.class);
                int PosicaoMensagem = RetornarIndiceDaMensagemNaLista(msg);

                if (PosicaoMensagem != -1){
                    setChanged();
                    notifyObservers(msg);
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
    public void GravarMensagem(Object msg) {
        if (msg instanceof Mensagem){
            Mensagem mensagem = (Mensagem) msg;
            bancoDeDados = ConfiguradorFireBase.getBancoDeDadosFireBase();
            DatabaseReference MensagemReferencia = bancoDeDados.child(NO_DE_MENSAGENS);
            mensagem.setIdMensagem(MensagemReferencia.child(mensagem.getIdUsuarioRemetente()).child(mensagem.getIdUsuarioDestinatario()).push().getKey());

            MensagemReferencia.child(mensagem.getIdUsuarioRemetente()).child(mensagem.getIdUsuarioDestinatario()).child(mensagem.getIdMensagem()).setValue(mensagem);

            MensagemReferencia = bancoDeDados.child(NO_DE_MENSAGENS);
            MensagemReferencia.child(mensagem.getIdUsuarioDestinatario()).child(mensagem.getIdUsuarioRemetente()).child(mensagem.getIdMensagem()).setValue(mensagem);
        }
    }

    @Override
    public void RemoverMensagem(String sIdMensagem, String IdUsuarioDestinatario, String IdUsuarioRementente) {
        bancoDeDados = ConfiguradorFireBase.getBancoDeDadosFireBase();
        DatabaseReference MensagemReferencia = bancoDeDados.child(NO_DE_MENSAGENS);
        MensagemReferencia.child(IdUsuarioRementente).child(IdUsuarioDestinatario).child(sIdMensagem).removeValue();
        MensagemReferencia.child(IdUsuarioDestinatario).child(IdUsuarioRementente).child(sIdMensagem).removeValue();
    }

    @Override
    public void AtualizarMensagemParaLida(Object msg){
        if (msg instanceof Mensagem) {
            Mensagem mensagem = (Mensagem) msg;
            mensagem.setMensagemLida(true);
            bancoDeDados = ConfiguradorFireBase.getBancoDeDadosFireBase();
            DatabaseReference MensagemReferencia = bancoDeDados.child(NO_DE_MENSAGENS);
            MensagemReferencia.child(mensagem.getIdUsuarioRemetente()).child(mensagem.getIdUsuarioDestinatario()).child(mensagem.getIdMensagem()).setValue(mensagem);

            MensagemReferencia = bancoDeDados.child(NO_DE_MENSAGENS);
            MensagemReferencia.child(mensagem.getIdUsuarioDestinatario()).child(mensagem.getIdUsuarioRemetente()).child(mensagem.getIdMensagem()).setValue(mensagem);
        }
    }

    @Override
    public void AdicionarObserver(Object observer) {
        if (observer instanceof Observer) {
            addObserver((Observer) observer);
        }
    }

    @Override
    public void RemoverObserver(Object observer) {
        if (observer instanceof Observer) {
            deleteObserver((Observer) observer);

            if (countObservers() <= 0) {
                if (childEventListenerMensagens != null){
                    ConfiguradorFireBase.getBancoDeDadosFireBase().removeEventListener(childEventListenerMensagens);
                }
            }
        }
    }

    private int RetornarIndiceDaMensagemNaLista(Mensagem m){
        for (int i = 0; i < listaDeMensagens.size(); i++){
            if (listaDeMensagens.get(i).getIdMensagem().equals(m.getIdMensagem())){
                return i;
            }
        }
        return -1;
    }

}
