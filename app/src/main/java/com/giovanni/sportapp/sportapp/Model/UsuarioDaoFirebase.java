package com.giovanni.sportapp.sportapp.Model;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.core.GeoHash;
import com.giovanni.sportapp.sportapp.Configuracoes.ConfiguradorFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class UsuarioDaoFirebase extends Observable implements UsuarioDao {

    private static final String NO_DE_USUARIOS = "Usuarios";
    private static final String NO_DE_IMAGENS = "imagens";
    private static final String NO_DE_IMAGENS_PERFIL = "perfil";
    private static final String FORMATO_IMAGEM = ".jpeg";
    private static final String MSG_SENHA_MAIS_FORTE = "Digite uma senha mais forte.";
    private static final String MSG_EMAIL_INVALIDO = "E-mail inválido.";
    private static final String MSG_EMAIL_JA_CADASTRADO = "E-mail já cadastrado.";
    private static final String MSG_ERRO_DESCONHECIDO = "Erro desconhecido. Tente novamente mais tarde.";
    private static final String MSG_SUCESSO_SALVAR_USUARIO = "Sucesso ao gravar dados do usuário.";
    private static final String MSG_ERRO_SALVAR_USUARIO = "Falha ao gravar dados do usuário.";
    private static final String MSG_SUCESSO_ATUALIZAR_IMAGEM = "Imagem atualizada com sucesso.";
    private static final String MSG_FALHA_ATUALIZAR_IMAGEM = "Falha ao atualizar imagem do perfil.";
    private static final String NO_DE_TOKENS = "tokens";
    private static final String NO_TOKEN_USUARIO = "tokenUsuario";

    private Usuario                   usuarioInterno;
    private ArrayList<Usuario>        listaDeUsuariosInterna;
    private DatabaseReference         referenciaFireBaseUsuarios;
    private DatabaseReference         referenciaFirebaseTokenUsuarios;
    private GeoQueryDataEventListener enventListnerUsuariosPorLocalizacao;
    private GeoQuery                  geoQuery;
    private GeoFire                   geoFire;
    private StorageReference          referenciaStorage;
    private boolean                   notificarInterno;

    public UsuarioDaoFirebase(){
        referenciaFireBaseUsuarios = ConfiguradorFireBase.getBancoDeDadosFireBase()
                .child(NO_DE_USUARIOS);

        referenciaFirebaseTokenUsuarios = ConfiguradorFireBase.getBancoDeDadosFireBase()
                .child(NO_DE_TOKENS);

        geoFire = new GeoFire(referenciaFireBaseUsuarios);
        referenciaStorage = ConfiguradorFireBase.getArmazenadorDeImagensFireBase();

        listaDeUsuariosInterna = new ArrayList<Usuario>();
    }

    @Override
    public void ConsultarUsuarioPorId(String id) {
        referenciaFireBaseUsuarios.child(id)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange( DataSnapshot dataSnapshot) {
                    Usuario usuarioConsultado = dataSnapshot.getValue(Usuario.class);
                    if (countObservers() > 0) {
                        setChanged();
                        notifyObservers(usuarioConsultado);
                    }
                }

                @Override
                public void onCancelled( DatabaseError databaseError) {

                }
            });
    }

    @Override
    public void GravarUsuarioNoBancoDeDados(Usuario usuario, boolean notificar) {
        notificarInterno = notificar;
        if (usuario.getL() != null){
            GeoHash geoHash = new GeoHash(usuario.getL().get(0),usuario.getL().get(1));
            usuario.setG(geoHash.getGeoHashString());
        }

        referenciaFireBaseUsuarios.child(usuario.getId()).setValue(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (notificarInterno) {
                    setChanged();
                    notifyObservers(MSG_SUCESSO_SALVAR_USUARIO);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (notificarInterno){
                    setChanged();
                    notifyObservers(MSG_ERRO_SALVAR_USUARIO);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void ConsultarUsuariosPorLocalizacao(double latitude, double longitude, double raioDeKm) {
        geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude,longitude),raioDeKm);
        enventListnerUsuariosPorLocalizacao = new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                if (RetornarIndiceDoUsuarioNaLista(usuario) == -1 && !usuario.getId().toString().equals(RetornarIdUsuarioLogado())){
                    listaDeUsuariosInterna.add(usuario);
                }
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                int indice = RetornarIndiceDoUsuarioNaLista(usuario);
                if (indice != -1){
                    listaDeUsuariosInterna.remove(indice);
                }
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                int indice = RetornarIndiceDoUsuarioNaLista(usuario);
                if (indice != -1){
                    listaDeUsuariosInterna.set(indice,usuario);
                }
            }

            @Override
            public void onGeoQueryReady() {
                setChanged();
                notifyObservers(listaDeUsuariosInterna);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        } ;
        geoQuery.addGeoQueryDataEventListener(enventListnerUsuariosPorLocalizacao);
    }

    @Override
    public void AddObserver(Object observer) {
        if (observer instanceof Observer){
            addObserver((Observer) observer);
        }
    }

    @Override
    public void RemoverObserver(Object observer) {
        if (observer instanceof Observer){
            deleteObserver((Observer) observer);
        }

        if (countObservers() == 0){
            if ((enventListnerUsuariosPorLocalizacao != null) && (geoQuery != null)){
                geoQuery.removeGeoQueryEventListener(enventListnerUsuariosPorLocalizacao);
            }
        }
    }

    @Override
    public String getMsgSucessoSalvarUsuario() {
        return MSG_SUCESSO_SALVAR_USUARIO;
    }

    @Override
    public String RetornarIdUsuarioLogado() {
        if (ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser() == null){
            return null;
        }
        return ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser().getUid();
    }

    @Override
    public void AtualizarImagemPerfilUsuario(Object imagem, Usuario usuario) {
        if (imagem instanceof InputStream){
            InputStream imagemStream = (InputStream) imagem;

            usuarioInterno = usuario;
            final StorageReference ImagemPerfilFireBase = referenciaStorage.child(NO_DE_IMAGENS)
                    .child(NO_DE_IMAGENS_PERFIL)
                    .child(usuario.getId() + FORMATO_IMAGEM);

            ImagemPerfilFireBase.putStream(imagemStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImagemPerfilFireBase.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            usuarioInterno.setFotoUrl(uri.toString());
                            GravarUsuarioNoBancoDeDados(usuarioInterno,false);
                            setChanged();
                            notifyObservers(MSG_SUCESSO_ATUALIZAR_IMAGEM);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( Exception e) {
                    setChanged();
                    notifyObservers(MSG_FALHA_ATUALIZAR_IMAGEM);
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void CriarNovoUsuario(Usuario novoUsuario) {
        usuarioInterno = novoUsuario;
        ConfiguradorFireBase.getAutenticadorFireBase()
                .createUserWithEmailAndPassword(usuarioInterno.getEmail(),usuarioInterno.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser() != null) {
                        usuarioInterno.setId(ConfiguradorFireBase.getAutenticadorFireBase().getCurrentUser().getUid());
                        GravarUsuarioNoBancoDeDados(usuarioInterno, true);
                    }
                }
                else{
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e){
                        setChanged();
                        notifyObservers(MSG_SENHA_MAIS_FORTE);
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        setChanged();
                        notifyObservers(MSG_EMAIL_INVALIDO);
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        setChanged();
                        notifyObservers(MSG_EMAIL_JA_CADASTRADO);
                    }
                    catch (Exception e){
                        setChanged();
                        notifyObservers(MSG_ERRO_DESCONHECIDO);
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private int RetornarIndiceDoUsuarioNaLista(Usuario u){
        for (int i = 0; i < listaDeUsuariosInterna.size(); i++){
            if (listaDeUsuariosInterna.get(i).getId().equals(u.getId())){
                return i;
            }
        }
        return -1;
    }

    public void AtualizarTokenUsuarioLogado(String token){
        String IdUsuarioLogado = RetornarIdUsuarioLogado();
        if ((IdUsuarioLogado != null) && (! token.isEmpty())) {
            referenciaFirebaseTokenUsuarios.child(IdUsuarioLogado).child(NO_TOKEN_USUARIO).setValue(token).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
