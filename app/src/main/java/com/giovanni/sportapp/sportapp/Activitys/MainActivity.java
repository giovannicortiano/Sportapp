package com.giovanni.sportapp.sportapp.Activitys;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.giovanni.sportapp.sportapp.Fragments.ConversasFragment;
import com.giovanni.sportapp.sportapp.Fragments.PessoasFragment;
import com.giovanni.sportapp.sportapp.Model.AutenticadorDeUsuario;
import com.giovanni.sportapp.sportapp.Model.AutenticadorDeUsuarioFirebase;
import com.giovanni.sportapp.sportapp.Model.UsuarioDao;
import com.giovanni.sportapp.sportapp.Model.UsuarioDaoFirebase;
import com.giovanni.sportapp.sportapp.R;
import com.giovanni.sportapp.sportapp.ServicoNotificacao.ServicoDeMensagemDoFirebase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity{

    private Toolbar ToolBarPrincipal;
    private AutenticadorDeUsuario autenticadorDeUsuario;
    private MaterialSearchView materialSearchViewPrincipal;
    private FragmentPagerItemAdapter adapter;
    private UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToolBarPrincipal = findViewById(R.id.toolBarPrincipal);
        ToolBarPrincipal.setTitle(R.string.app_name);
        setSupportActionBar(ToolBarPrincipal);
        ConfigurarViewPager();
        autenticadorDeUsuario = new AutenticadorDeUsuarioFirebase();

        usuarioDao = new UsuarioDaoFirebase();
        SharedPreferences sharedPreferences =  getSharedPreferences(getResources().getString(R.string.ChaveSharedPreferences),MODE_PRIVATE);
        String Token = sharedPreferences.getString(getResources().getString(R.string.ChaveToken),"");
        usuarioDao.AtualizarTokenUsuarioLogado(Token);

        materialSearchViewPrincipal = findViewById(R.id.materialSearch_viewPrincipal);
        materialSearchViewPrincipal.setHint("Pesquisar");
        CriarCanalDeNotificacao();
        materialSearchViewPrincipal.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                PessoasFragment pessoasFragment = (PessoasFragment) adapter.getPage(0);
                if (query != null && ! query.isEmpty()){
                    pessoasFragment.PesquisarPessoas(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                PessoasFragment pessoasFragment = (PessoasFragment) adapter.getPage(0);
                if (newText != null && ! newText.isEmpty()){
                    pessoasFragment.PesquisarPessoas(newText);
                }
                else{
                    pessoasFragment.AdicionarPessoasNaListaPrincipal();
                }
                return false;
            }
        });
    }

    private void ConfigurarViewPager(){
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add(R.string.Pessoas, PessoasFragment.class)
                        .add(R.string.Conversas, ConversasFragment.class).create()
        );

        ViewPager viewPager = findViewById(R.id.ViewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.ViewPagerTab);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        MenuItem item  = menu.findItem(R.id.menuPesquisar);
        materialSearchViewPrincipal.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSair){
            autenticadorDeUsuario.FazerLogoffUsuarioLogado();
            finish();
        }
        else if (item.getItemId() == R.id.menuConfiguracoes){
            startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void CriarCanalDeNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String IdCanalNotificacao = getResources().getString(R.string.Id_canal_de_notificacao);
            CharSequence nome = "Sportapp";
            String descricao = "Notificações do Sportapp";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(IdCanalNotificacao, nome, importancia);
            notificationChannel.setDescription(descricao);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
