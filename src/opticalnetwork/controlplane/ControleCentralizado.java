package opticalnetwork.controlplane;

import graph.Caminho;
import graph.Enlace;
import graph.Grafo;
import opticalnetwork.rwa.Requisicao;
import algorithm.Dijkstra;
import event.Event;
import event.EventList;

public class ControleCentralizado extends Controle{



	public ControleCentralizado(Grafo grafo, int numLambdas, int limiteDeRequisicoes, boolean usarRotasExplicitas){
		super(numLambdas, limiteDeRequisicoes);
		setGrafo(grafo);	
		setUsarRotasExplicitas(usarRotasExplicitas);
		iniciaTablelaDeEstados();

	}

	
	
	public EventList receberEvento(Event evento) throws Exception{
//		System.out.println(evento);
		if(evento.getConteudo() != null){
			if(evento.getType()==null){
				throw new ExcecaoControle(evento.getTime()+" "+evento.getConteudo().toString());
			}
		} else {
			listaEventos.insert(null);
			return listaEventos;
		}
		
		switch (evento.getType()){
		
		case NEW_REQUEST:	
			
			Requisicao requisicao = (Requisicao)evento.getConteudo();
			
//			CaminhoOptico caminhoOptico = null;
			Caminho rota = null;

			switch (requisicao.getTipo()){
			case ABRIR_CONEXAO:

				setPadroesDeTrafego(evento.getTime(), requisicao.getDuracao());
				
				Boolean[] estado = null; //estado do enlace
				Boolean[] mascara = getMascara(); 
		
				String chave = requisicao.getChaveOrigemDestino();
				if(isUsarRotasExplicitas()){
					rota = getRota(chave);
				} else if(getTabelaDeRotas().containsKey(chave)){
					rota = getRota(chave);
				}else{
					rota = Dijkstra.getMenorCaminho(getGrafo(), requisicao.getOrigem(), requisicao.getDestino());
					getTabelaDeRotas().put(chave, rota);
				}
				
				/* Verifica os estados dos enlaces da rota */
				for(Enlace e : rota.getEnlaces().valores()){
					
					estado = getTabelaDeEstados().get(e.getId()).clone();
					
					/* Para cada posic�o da m�scara, faz o "ou" l�gico o estado do enlace*/
					for (int i = 0 ; i < mascara.length ; i++){
						mascara[i] = mascara[i] && estado[i];
					}	
					
				}
//				
				int lambda = -1;
				
				/* Pega o primeiro lambda com estado de utiliza��o "false" */
				for(int i = 0 ; i < mascara.length ; i++){
					if(mascara[i]){ 
						lambda = i;
						break;
					}				
				}
			
				
				/* Se o Lambda n�o foi modificado, ent�o bloqueia a conex�o*/
				if(lambda == -1){ 
					incrementaBloqueios();
					break;
				}
				
				/* Atribui o estado de utiliza��o True em cada lambda dos enlaces da rota */
				for(Enlace e : rota.getEnlaces().valores()){
					estado = getTabelaDeEstados().get(e.getId());
					estado[lambda] = false;
				}
				
				incrementaCaminhosEstabelecidos();
				
				//caminhoOptico = new CaminhoOptico(rota,lambda);
				//this.tabelaDeCaminhosOpticos.put(requisicao.getIdRequisicao(), caminhoOptico);

				requisicao.setTipo(Requisicao.Tipo.FECHAR_CONEXAO);
				requisicao.setLambda(lambda);
//				listaEventos.inserir(getGerador().criaEvento((getGerador().getTempoProximo() + requisicao.getDuracao()), requisicao));
				evento.setTempo(evento.getTime()+requisicao.getDuracao());
				listaEventos.insert(evento);
				break;

			case FECHAR_CONEXAO:
//				int id = requisicao.getIdRequisicao();
				Caminho caminho = getRota(requisicao.getChaveOrigemDestino());
				estado = null;
				lambda = requisicao.getLambda();
				
				for(Enlace e : caminho.getEnlaces().valores()){
					estado = getTabelaDeEstados().get(e.getId());
//					System.out.println(e.getId());
//					mostrarEstado(estado);
					
					estado[lambda] = true;
//					mostrarEstado(estado);
					
					
				}

				break;

			}
		default:
			break;


		}

		return listaEventos;
		
	}


	
	
}
