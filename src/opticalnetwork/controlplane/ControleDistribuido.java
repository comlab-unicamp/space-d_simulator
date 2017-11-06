package opticalnetwork.controlplane;

import event.Event;
import event.EventList;
import graph.Caminho;
import graph.Enlace;
import graph.Grafo;
import graph.No;
import opticalnetwork.EnlaceOptico;
import opticalnetwork.rwa.PacoteRsvp;
import opticalnetwork.rwa.Requisicao;
import opticalnetwork.rwa.PacoteRsvp.Tipo;

public class ControleDistribuido extends Controle{


	public ControleDistribuido( Grafo grafo, int numLambdas, int limiteDeRequisicoes, boolean usarRotasExplicitas) {
		super(numLambdas, limiteDeRequisicoes);
		setGrafo(grafo);		
		setUsarRotasExplicitas(usarRotasExplicitas);
		iniciaTablelaDeEstados();
	}



	public EventList receberEvento(Event evento) throws Exception{

		if(evento.getConteudo() != null){
			if(evento.getType()==null){
				throw new ExcecaoControle(evento.getTime()+" "+evento.getConteudo().toString());
			}
		} else {
			return listaEventos;
		}

		switch (evento.getType()){

		case PACKET_FORWARDING:
			if ((evento.getConteudo()) instanceof PacoteRsvp ) {
				PacoteRsvp pacote = ((PacoteRsvp)evento.getConteudo());
				
				switch (pacote.getTipo()){
				case PATH:
					executaPath(evento);
					break;
				case RESV:
					executaResv(evento);
					break;

				case PATH_TEAR:
					executaPathTear(evento);
					break;

				case PATH_ERR_LABEL_SET_VAZIO:
					executaPathErrLabelSetVazio(evento);
					break;

				case PATH_ERR_LAMBDA_INDISPONIVEL:
					executaPathErrLambdaIndisponivel(evento);
					break;

				case RESV_ERR_LAMBDA_INDISPONIVEL:
					executaResvErrLambdaIndisponivel(evento);
					break;
				default:
					break;

				} // Fim switch(pacote.getTipo)
				
			} //Fim if(evento.getConteudo() instanceof PacoteRsvp)
			

			break;

		case NEW_REQUEST:	
			Requisicao requisicao = null;
			PacoteRsvp pacote = null;

			if( evento.getConteudo() instanceof Requisicao ){
				requisicao = (Requisicao)evento.getConteudo();

				switch (requisicao.getTipo()){
				case ABRIR_CONEXAO:

					setPadroesDeTrafego(evento.getTime(), requisicao.getDuracao());

					pacote = new PacoteRsvp(requisicao, PacoteRsvp.Tipo.PATH, getMascara() );
					evento.setConteudo(pacote);
					evento.setTipo(Event.Type.PACKET_FORWARDING);
					listaEventos.insert(evento);
					break;



				case FECHAR_CONEXAO:

					break;

				} // Fim switch(requisicao.getTipo)
				
			} //Fim if(evento.getConteudo() instanceof Requisicao)
		default:
			break;


		}//Fim switch (evento.getTipo())
		
		return listaEventos;

	}

	public void executaPath(Event evento)throws Exception{
		PacoteRsvp pacote = (PacoteRsvp)evento.getConteudo();

		Boolean[] estado = null; //estado do enlace
		Boolean[] mascara = pacote.getLabelSet(); 
		Caminho rota = null;
		Enlace enlace = null;

		if(pacote.getHopAtual().equals(pacote.getOrigem()) && pacote.getHopAtual().equals(pacote.getDestino())){
			throw new ExcecaoControle("Origem:" + pacote.getOrigem()+ " igual ao Destino: "+pacote.getDestino());
		}

		if(pacote.getHopAtual().equals(pacote.getDestino())){
			if (!isUsarRotasExplicitas()) {
				pacote.getRro().adicionarNo(pacote.getHopAtual());
			} 
			int lambda = -1;
			/* Pega o primeiro lambda com estado de utiliza��o "false" */
			for(int i = 0 ; i < mascara.length ; i++){
				if(mascara[i]){ 
					lambda = i;
					break;
				}				
			}

			pacote.setRotulo(lambda);
			pacote.getHopAtual().addPathHopAnterior(pacote.getId(),pacote.getHopAnterior());
			pacote.setTipo(Tipo.RESV);
			pacote.setHopAnterior(null);
			
			listaEventos.insert(evento);
			return;

		} else {

			String chave = pacote.getChaveOrigemDestino();
			if(isUsarRotasExplicitas()){
				rota = getRota(chave);
				pacote.setRro(rota);
				enlace = pacote.getHopAtual().getProximoEnlace(rota);
				
				
			}else{
				enlace = pacote.getHopAtual().getProximoEnlace(pacote.getDestino());
				pacote.adicionarNo(pacote.getHopAtual());
				pacote.getRro().adicionarEnlace(enlace);
			}

			estado = getTabelaDeEstados().get(enlace.getId()).clone();

			/* Para cada posic�o da m�scara, faz o "ou" l�gico o estado do enlace*/
			for (int i = 0 ; i < mascara.length ; i++){
				mascara[i] = mascara[i] && estado[i];
			}	

			int lambda = -1;

			/* Pega o primeiro lambda com estado de utiliza��o "false" */
			for(int i = 0 ; i < mascara.length ; i++){
				if(mascara[i]){ 
					lambda = i;
					break;
				}				
			}

			pacote.getHopAtual().addPathHopAnterior(pacote.getId(),pacote.getHopAnterior());
			/* Se o Lambda n�o foi modificado, ent�o bloqueia a conex�o*/
			if(lambda == -1){ 
				pacote.setTipo(Tipo.PATH_ERR_LABEL_SET_VAZIO);
				incrementaBloqueios();
				numbloqueiosLabelSetVazio++;
				listaEventos.insert(evento);
				return;
			}


			
			pacote.setHopAnterior(pacote.getHopAtual());
			pacote.setHopAtual(enlace.getNoDireita());
			
			evento.setTempo(evento.getTime()+((EnlaceOptico)enlace).getTempoPropagacao());
			listaEventos.insert(evento);
			return;

		}
	}

	public void executaResv(Event evento)throws Exception{
		PacoteRsvp pacote = (PacoteRsvp)evento.getConteudo();
		Boolean[] estado = null; //estado do enlace
		Enlace enlace = null;
		Caminho rota = null;

		if(pacote.getHopAtual().equals(pacote.getOrigem())){
			pacote.getHopAtual().addResvHopAnterior(pacote.getId(),pacote.getHopAnterior());
			//		pacote.getHopAtual().addFluxoLabel(pacote.getIdFluxo(), pacote.getRotulo());

			pacote.setTipo(Tipo.PATH_TEAR);
			pacote.setHopAnterior(null);
			pacote.setHopAtual(pacote.getOrigem());

			incrementaCaminhosEstabelecidos();
//			listaEventos.inserir(getGerador().criaEvento((getGerador().getTempoProximo() + pacote.getDuracao()),Evento.Tipo.SAIDA_PACOTE, pacote));
			evento.setTempo(evento.getTime()+pacote.getDuracao());
			listaEventos.insert(evento);
			return;

		}else{
			No anterior = pacote.getHopAtual().getPathHopAnterior(pacote.getId());
			pacote.getHopAtual().addResvHopAnterior(pacote.getId(),pacote.getHopAnterior());
			//		pacote.getHopAtual().addFluxoLabel(pacote.getIdFluxo(), pacote.getRotulo());
			
			rota = pacote.getRro();
			enlace = rota.getEnlace(anterior,pacote.getHopAtual());
			estado = getTabelaDeEstados().get(enlace.getId());

			if(estado[pacote.getRotulo()] == true){
				estado[pacote.getRotulo()] = false;
				pacote.setHopAnterior(pacote.getHopAtual());
				pacote.setHopAtual(anterior);
//				listaEventos.inserir(getGerador().criaEvento(getGerador().getTempoProximo() + ((EnlaceOptico)enlace).getTempoPropagacao(),Evento.Tipo.SAIDA_PACOTE, pacote));
				evento.setTempo(evento.getTime()+((EnlaceOptico)enlace).getTempoPropagacao());
				listaEventos.insert(evento);
				return;

			} else {
//				throw new ExcecaoControle("Erro conten��o");
				
				incrementaBloqueios();
				numbloqueiosLambdaIndisponivel++;
				pacote.setTipo(Tipo.RESV_ERR_LAMBDA_INDISPONIVEL);
				evento.setTempo(evento.getTime()+((EnlaceOptico)enlace).getTempoPropagacao());
				listaEventos.insert(evento);
				
			
				
				Event novoEvento = evento.clone();
				PacoteRsvp pacotePath = pacote.clone();
				pacotePath.setTipo(Tipo.PATH_ERR_LAMBDA_INDISPONIVEL);
				novoEvento.setConteudo(pacotePath);
				listaEventos.insert(novoEvento);
				

				return;
			}

		}
	}

	public void executaPathTear(Event evento)throws Exception{
		PacoteRsvp pacote = (PacoteRsvp)evento.getConteudo();
		Caminho rota = null;
		Enlace enlace = null;
		if(pacote.getHopAtual().equals(pacote.getDestino())){
			pacote.getHopAtual().removerStates(pacote.getId());
			return;
		} else {

			No sucessor = pacote.getHopAtual().getResvHopAnterior(pacote.getId());
			rota = pacote.getRro();
			enlace = rota.getEnlace(pacote.getHopAtual(), sucessor);
			Boolean[] estado = getTabelaDeEstados().get(enlace.getId());
			estado[pacote.getRotulo()] = true;
			pacote.getHopAtual().removerStates(pacote.getId());
			pacote.setHopAtual(sucessor);
			evento.setTempo(evento.getTime()+((EnlaceOptico)enlace).getTempoPropagacao());
			listaEventos.insert(evento);
			return;
		}


	}

	public void executaPathErrLabelSetVazio(Event evento)throws Exception{
		PacoteRsvp pacote = (PacoteRsvp)evento.getConteudo();
		Caminho rota = null;
		Enlace enlace = null;
		if(pacote.getHopAtual().equals(pacote.getOrigem())){
			pacote.getHopAtual().removerPathHopAnterior(pacote.getId());
			return;

		} else {
			rota = pacote.getRro();
			No anterior = pacote.getHopAtual().getPathHopAnterior(pacote.getId());
			if(anterior == null){
				System.out.println(rota);
				throw new ExcecaoControle(pacote + "\nN� anterior ao n� " + pacote.getHopAtual() + " est� nulo "); 
			}

			pacote.getHopAtual().removerPathHopAnterior(pacote.getId());
			
			enlace = rota.getEnlace(anterior,pacote.getHopAtual());
			pacote.setHopAtual(anterior);
			evento.setTempo(evento.getTime()+((EnlaceOptico)enlace).getTempoPropagacao());
			listaEventos.insert(evento);
			return;
		}
	}

	public void executaPathErrLambdaIndisponivel(Event evento)throws Exception{
		PacoteRsvp pacote = (PacoteRsvp)evento.getConteudo();

		Caminho rota = null;
		Enlace enlace = null;
		if(pacote.getHopAtual().equals(pacote.getOrigem())){
			pacote.getHopAtual().removerPathHopAnterior(pacote.getId());
			return;
		} else {
			No anterior = pacote.getHopAtual().getPathHopAnterior(pacote.getId());
			pacote.getHopAtual().removerPathHopAnterior(pacote.getId());

			rota = pacote.getRro();
			enlace = rota.getEnlace(anterior,pacote.getHopAtual());
			pacote.setHopAtual(anterior);
			evento.setTempo(evento.getTime()+((EnlaceOptico)enlace).getTempoPropagacao());
			listaEventos.insert(evento);
			return;
		}

	}
	public void executaResvErrLambdaIndisponivel(Event evento)throws Exception{
		PacoteRsvp pacote = (PacoteRsvp)evento.getConteudo();
		Caminho rota = null;
		Enlace enlace = null;
		if(pacote.getHopAtual().equals(pacote.getDestino())){

			pacote.getHopAtual().removerResvHopAnterior(pacote.getId());
			pacote.getHopAtual().removerFluxoLabel(pacote.getId());
			return;
		} else {
			No sucessor = pacote.getHopAtual().getResvHopAnterior(pacote.getId());
			rota = pacote.getRro();
			enlace = rota.getEnlace(pacote.getHopAtual(), sucessor);
			Boolean[] estado = getTabelaDeEstados().get(enlace.getId());
			estado[pacote.getRotulo()] = true;
			pacote.getHopAtual().removerResvHopAnterior(pacote.getId());
			pacote.setHopAtual(sucessor);
			evento.setTempo(evento.getTime()+((EnlaceOptico)enlace).getTempoPropagacao());
			listaEventos.insert(evento);
			return;
		}
	}

}
