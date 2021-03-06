package com.giovanni.sportapp.sportapp.Activitys;

import android.content.Intent;
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
import com.giovanni.sportapp.sportapp.Model.Conversa;
import com.giovanni.sportapp.sportapp.Model.ConversaDao;
import com.giovanni.sportapp.sportapp.Model.ConversaDaoFirebase;
import com.giovanni.sportapp.sportapp.Model.Mensagem;
import com.giovanni.sportapp.sportapp.Model.MensagemDao;
import com.giovanni.sportapp.sportapp.Model.MensagemDaoFirebase;
import com.giovanni.sportapp.sportapp.Model.Usuario;
import com.giovanni.sportapp.sportapp.Model.UsuarioDao;
import com.giovanni.sportapp.sportapp.Model.UsuarioDaoFirebase;
import com.giovanni.sportapp.sportapp.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import de.hdodenhof.circleimageview.CircleImageView;

public class MensagemActivity extends AppCompatActivity implements Observer {

    private String                idUsuarioLogado;
    private Usuario               usuarioDestinatario;
    private Usuario               usuarioLogado;
    private TextView              textViewNome;
    private CircleImageView       imagemPerfil;
    private FloatingActionButton  btnEnviarConversa;
    private EditText              edtMensagem;
    private RecyclerView          recyclerViewConversa;
    private LinearLayoutManager   layoutManager;
    private MensagensAdapter      adapter;
    private ArrayList<Mensagem>   listaDeMensagens = new ArrayList<>();
    private Toolbar               toolbar;
    private MensagemDao           mensagemDao;
    private ConversaDao           conversaDao;
    private UsuarioDao            usuarioDao;
    private static final String   USUARIO = "Usuario";
    private static final String   FOMATATO_DATA = "dd/MM/yyyy HH:mm";
    private static final String   LATITUDE_USUARIO_LOGADO = "LatitudeUsuarioLogado";
    private static final String   LONGITUDE_USUARIO_LOGADO = "LongitudeUsuarioLogado";
    private static final String MOSTRAR_BOTAO_MENSAGEM = "mostrarBotaoMensagem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagem);

        mensagemDao = new MensagemDaoFirebase(listaDeMensagens);
        conversaDao = new ConversaDaoFirebase();
        usuarioDao  = new UsuarioDaoFirebase();
        idUsuarioLogado = usuarioDao.RetornarIdUsuarioLogado();
        usuarioDao.AddObserver(this);
        usuarioDao.ConsultarUsuarioPorId(idUsuarioLogado);

        RecuperarViews();
        ConfigurarToolBar();
        MostrarDadosDoUsuarioDestinatario();
        ConfigurarRecyclerViewMensagens();
        ConfigurarCliqueBotaoEnviarMensagem();

        imagemPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                intent.putExtra(USUARIO,usuarioDestinatario);
                intent.putExtra(MOSTRAR_BOTAO_MENSAGEM,false);
                if (usuarioLogado != null && usuarioLogado.getL() != null) {
                    intent.putExtra(LATITUDE_USUARIO_LOGADO, usuarioLogado.getL().get(0));
                    intent.putExtra(LONGITUDE_USUARIO_LOGADO, usuarioLogado.getL().get(1));
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mensagemDao.AdicionarObserver(this);
        RecuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagemDao.RemoverObserver(this);
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
            usuarioDestinatario = (Usuario) bundle.getSerializable(USUARIO);
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
         if ((! edtMensagem.getText().toString().isEmpty()) && (usuarioLogado != null)) {
             Mensagem msg = new Mensagem();
             msg.setIdUsuarioRemetente(usuarioLogado.getId());
             msg.setMensagem(edtMensagem.getText().toString());
             msg.setIdUsuarioDestinatario(usuarioDestinatario.getId());
             msg.setMensagemLida(false);
             SimpleDateFormat dateFormat = new SimpleDateFormat(FOMATATO_DATA);
             Date data = new Date();
             String DataAtual = dateFormat.format(data);
             msg.setDataEHora(DataAtual);
             mensagemDao.GravarMensagem(msg);
             GravarConversa(msg);
             edtMensagem.setText("");
         }
    }

    private void GravarConversa(Mensagem msg){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdDestinatario(usuarioDestinatario.getId());
        conversaRemetente.setIdRemetente(usuarioLogado.getId());
        conversaRemetente.setUltimaMensagem(msg.getMensagem());
        conversaRemetente.setUsuarioExibicao(usuarioDestinatario);

        Conversa conversaDestinatario = new Conversa();
        conversaDestinatario.setIdDestinatario(usuarioLogado.getId());
        conversaDestinatario.setIdRemetente(usuarioDestinatario.getId());
        conversaDestinatario.setUltimaMensagem(msg.getMensagem());
        conversaDestinatario.setUsuarioExibicao(usuarioLogado);

        conversaDao.GravarConversaNoBancoDeDados(conversaRemetente,conversaDestinatario);
    }

    private void RecuperarMensagens(){
        mensagemDao.RecuperarMensagens(idUsuarioLogado, usuarioDestinatario.getId());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Mensagem){
            adapter.notifyDataSetChanged();
            recyclerViewConversa.scrollToPosition(listaDeMensagens.size()-1);
        }
        else if (arg instanceof Usuario){
            usuarioLogado = (Usuario) arg;
        }

    }
}
