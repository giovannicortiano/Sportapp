'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.EnviarNotificacaoNovaMensagem = functions.database.ref('/notificacoes/{IdNotificacao}/{IdDestinatario}/{IdRemetente}')
	.onCreate(async (change, context) => {
		const IdDestinatario = context.params.IdDestinatario;
		const IdRemetente = context.params.IdRemetente;
		const IdNotificacao = context.params.IdNotificacao;
		
		///Pega o token do usuário destinatario
		const TokenDestinatarioPromise = admin.database()
			.ref(`/tokens/${IdDestinatario}`).once('value');
			
		///Pega os dados do usuário remetente
		const RemetentePromise = admin.database()
			.ref(`/Usuarios/${IdRemetente}`).once('value');
		
		const results = await Promise.all([TokenDestinatarioPromise, RemetentePromise]);
		const tokenSnapshot = results[0];
		const remetente = results[1];
		  
		const TokenDetinatario = tokenSnapshot.val().tokenUsuario;
		const NomeRemetente = remetente.val().nome;
		const fotoUrl = remetente.val().fotoUrl;
		  
		///Monta uma notificação
		const payload = {
			notification: {
			title: `Sportapp`,
			body: `${NomeRemetente} enviou uma nova mensagem.`
			}
		  };
	  
	    ///Manda a notificação para o token do destinatario
	    const response = await  admin.messaging().sendToDevice(TokenDetinatario, payload);  
	      response.results.forEach((result, index) => {
          const error = result.error;
          if (error) {
             console.error('Failure sending notification to', error);
          }else{
				///Remove da lista de notifcações, pois já notificou, não tem mais pq manter.
				admin.database().ref(`/notificacoes/${IdNotificacao}`).remove();
		  }
        });
	});
