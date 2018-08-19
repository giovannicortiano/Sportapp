package com.giovanni.sportapp.sportapp.Activitys;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.giovanni.sportapp.sportapp.Adapters.MensagensAdapter;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Model.Conversa;
import com.giovanni.sportapp.sportapp.Model.Mensagem;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;


public class ConversaActivity extends AppCompatActivity {

    private String               idUsuarioLogado;
    private Usuario              usuarioDestinatario;
    private TextView             textViewNome;
    private CircleImageView      imagemPerfil;
    private FloatingActionButton btnEnviarConversa;
    private EditText             edtMensagem;
    private RecyclerView         recyclerViewConversa;
    private LinearLayoutManager  layoutManager;
    private MensagensAdapter     adapter;
    private ArrayList<Mensagem>  listaDeMensagens = new ArrayList<>();
    private DatabaseReference    bancoDeDados;
    private DatabaseReference    mensagensReferencia;
    private ChildEventListener   childEventListenerMensagens;
    private Toolbar              toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);
        RecuperarViews();
        ConfigurarToolBar();
        MostrarDadosDoUsuarioDestinatario();
        ConfigurarRecyclerViewMensagens();
        ConfigurarCliqueBotaoEnviarMensagem();
        idUsuarioLogado = ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser().getUid();
        bancoDeDados = ConfiguradorFireBase.getBancoDeDadosFireBase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensReferencia.removeEventListener(childEventListenerMensagens);
    }

    private void ConfigurarRecyclerViewMensagens(){
        adapter = new MensagensAdapter(listaDeMensagens, getApplicationContext());
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewConversa.setLayoutManager(layoutManager);
        recyclerViewConversa.setHasFixedSize(true);
        recyclerViewConversa.setAdapter(adapter);

        ///Quando abrir o teclado continua mostrando a última mensagem
        recyclerViewConversa.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerViewConversa.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewConversa.scrollToPosition(recyclerViewConversa.getAdapter().getItemCount() - 1);
                        }
                    });
                }
            }
        });
    }

    private void ConfigurarCliqueBotaoEnviarMensagem(){
        btnEnviarConversa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarMensagem();
            }
        });
    }

    private void MostrarDadosDoUsuarioDestinatario(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioDestinatario = (Usuario) bundle.getSerializable("Usuario");
        }

        if (usuarioDestinatario != null){
            textViewNome.setText(usuarioDestinatario.getNome());

            if (usuarioDestinatario.getFotoUrl() != null){
                Uri uri = Uri.parse(usuarioDestinatario.getFotoUrl());
                Glide.with(this).load(uri).into(imagemPerfil);
            }
            else{
                imagemPerfil.setImageResource(R.drawable.perfil_padrao);
            }
        }
    }

    private void RecuperarViews(){
        toolbar              = findViewById(R.id.toolbar);
        textViewNome         = findViewById(R.id.TextNomeTelaConversa);
        imagemPerfil         = findViewById(R.id.ImagemTelaConversa);
        btnEnviarConversa    = findViewById(R.id.btnEnviarConversa);
        edtMensagem          = findViewById(R.id.edtMensagem);
        recyclerViewConversa = findViewById(R.id.RecyclerViewConversa);
    }

    private void ConfigurarToolBar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void EnviarMensagem(){
         if (! edtMensagem.getText().toString().isEmpty()) {
             Mensagem msg = new Mensagem();
             msg.setIdUsuarioRemetente(idUsuarioLogado);
             msg.setMensagem(edtMensagem.getText().toString());
             msg.setIdUsuarioDestinatario(usuarioDestinatario.getId());
             msg.setMensagemLida(false);
             SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
             Date data = new Date();
             String DataAtual = dateFormat.format(data);
             msg.setDataEHora(DataAtual);
             msg.SalvarMensagem();
             GravarConversa(msg);
             edtMensagem.setText("");

             ////Se chegou em 100 mensagens a mensagem mais antiga de todas é excluída para
             ////que sempre matenha um histórico de no máximo 100 mensagens
             if (listaDeMensagens.size() >= 100){
                 msg.RemoverMensagem(listaDeMensagens.get(0).getIdMensagem());
             }
         }
    }

    private void GravarConversa(Mensagem msg){
        Conversa conversa = new Conversa();
        conversa.setIdDestinatario(usuarioDestinatario.getId());
        conversa.setIdRemetente(idUsuarioLogado);
        conversa.setUltimaMensagem(msg.getMensagem());
        conversa.setUsuarioExibicao(usuarioDestinatario);
        conversa.SalvarConversa();
    }

    private void RecuperarMensagens(){
        mensagensReferencia = bancoDeDados.child("mensagens")
                .child(idUsuarioLogado)
                .child(usuarioDestinatario.getId());

        childEventListenerMensagens = mensagensReferencia.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem msg = dataSnapshot.getValue(Mensagem.class);
                if (RetornarIndiceDaMensagemNaLista(msg) == - 1){
                    listaDeMensagens.add(msg);
                    adapter.notifyDataSetChanged();
                    recyclerViewConversa.scrollToPosition(listaDeMensagens.size()-1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Mensagem msg = dataSnapshot.getValue(Mensagem.class);
                int PosicaoMensagem = RetornarIndiceDaMensagemNaLista(msg);

                if (PosicaoMensagem != -1){
                    listaDeMensagens.get(PosicaoMensagem).setMensagemLida(msg.isMensagemLida());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Mensagem msg = dataSnapshot.getValue(Mensagem.class);
                int PosicaoMensagem = RetornarIndiceDaMensagemNaLista(msg);

                if (PosicaoMensagem != -1){
                    listaDeMensagens.remove(PosicaoMensagem);
                    adapter.notifyDataSetChanged();
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

    private int RetornarIndiceDaMensagemNaLista(Mensagem m){
        for (int i = 0; i < listaDeMensagens.size(); i++){
            if (listaDeMensagens.get(i).getIdMensagem().equals(m.getIdMensagem())){
                return i;
            }
        }
        return -1;
    }

}
