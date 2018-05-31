package com.giovanni.sportapp.sportapp.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.giovanni.sportapp.sportapp.Fragments.AnunciosFragment;
import com.giovanni.sportapp.sportapp.Fragments.ConversasFragment;
import com.giovanni.sportapp.sportapp.Fragments.PessoasFragment;
import com.giovanni.sportapp.sportapp.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private Toolbar ToolBarPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ToolBarPrincipal = findViewById(R.id.toolBarPrincipal);
        ToolBarPrincipal.setTitle(R.string.app_name);
        setSupportActionBar(ToolBarPrincipal);
        ConfigurarViewPager();
    }

    private void ConfigurarViewPager(){
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add(R.string.Anuncios, AnunciosFragment.class)
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSair){
            ConfiguradorFireBase.getAutenticadorFireBase().signOut();
            finish();
        }
        else if (item.getItemId() == R.id.menuConfiguracoes){
            startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
