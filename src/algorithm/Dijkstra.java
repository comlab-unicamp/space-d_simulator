package algorithm;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.No;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;


/**
 * @author Alaelson Jatob�
 * @version 2.0
 * modificado em 14/01/09
 * */
public class Dijkstra {
	private static final double INFINITO = Double.MAX_VALUE;

	private static AbstractGrafo grafo;

	private static PriorityQueue<No> nosNaoSelecionados = new PriorityQueue<No>(20);
//	private HashMap<String,No> nosSelecionados;
	private static ArrayList<No> nosSelecionados = new ArrayList<No>();
	private static HashMap<No,No> antecessores = new HashMap<No, No>() ;
	private static HashMap<No,No> sucessor = new HashMap<No, No>();
	private static HashMap<No,Double> menoresDistancias = new HashMap<No, Double>();


	/**
	 * inicia as estruturas utilizadas
	 * @param origem n� fonte utilizado
	 * */
	private static void iniciar(No origem, No destino){
//		this.origem = origem;
//		this.destino = destino;

		nosNaoSelecionados.clear();
		nosSelecionados.clear();
		antecessores.clear();
		menoresDistancias.clear();
		sucessor.clear();

		menoresDistancias.put(origem, 0.0);
		for (No n : grafo.getNos().valores()){
			if(!n.equals(origem)){
				menoresDistancias.put(n, INFINITO);
				n.setPesoDijkstra(getMenorDistancia(n));
			} else {
				n.setPesoDijkstra(0);
			}
		}

		setPredecessor(origem, null);
		sucessor.put(destino, null);
		nosNaoSelecionados.add(origem);

	}
	/**
	 * @return o n� de menor peso
	 * */
	private static No extraiMin(){
		return nosNaoSelecionados.poll();
    }
	/**
	 * Executa o Dijkstra de um n� fonte para um n� destino
	 * @param origem N� fonte
	 * @param destino N� de destino
	 * */
	private static void executar(No origem, No destino){
		iniciar(origem, destino);
		No u ;

		while(!nosNaoSelecionados.isEmpty()){

			u = extraiMin();
			if(!isSelecionado(u)){
				if (u == destino){
//					nosSelecionados.put(u.getNome(),u);
					nosSelecionados.add(u);

				} else {
					nosSelecionados.add(u);
//					nosSelecionados.put(u.getNome(),u);
					relaxaVizinhos(u);
				}
			}
		}

	}

	/**
	 * Verifica a menor distancia do pr�ximo salto do n� u
	 * @param noAtual n� inspecionado
	 * */
	private static void relaxaVizinhos(No noAtual){


		for  ( Enlace e : noAtual.getEnlaces().values() ) {
			if(e.isAtivado()){
				No noAdjacente = e.getNoDireita();
//				if(!nosSelecionados.containsKey(noAdjacente.getNome())){
				if(!nosSelecionados.contains(noAdjacente)){
//					double distancia = getMenorDistancia(noAtual) + e.getPeso();
					double distancia = getMenorDistancia(noAtual) + e.getDistancia();

					if(getMenorDistancia(noAdjacente) > distancia){
						setMenorDistancia(noAdjacente, distancia);
						noAdjacente.setPesoDijkstra(distancia);
						setPredecessor(noAdjacente, noAtual);
						sucessor.put(noAtual, noAdjacente);
					}

				}
			}

		}
	}

	/**
	 * Verifica se � um n� j� selecionado
	 * @param u n� inspecionado
	 * */
	private static boolean isSelecionado(No u){
//		return (nosSelecionados.containsKey(u.getNome()));
		return nosSelecionados.contains(u);
	}
	/**
	 * @return a menor distancia para chegar no n� u
	 * @param u n� inspecionado
	 * */
	private static double getMenorDistancia(No u) {
		double distancia = menoresDistancias.get(u);
		if (distancia == INFINITO){
			return INFINITO;
		} else {
			return distancia;
		}
	}

	/**
	 * configura o n� e seu predecessor
	 * @param no � o n� a ser configurado
	 * @param antecessores � o n� predecessor ao n� configurado
	 * */
	private static void setPredecessor(No no, No antecessor) {
		antecessores.put(no, antecessor);
	}

	/**
	 * Configura o n� e a menor distancia da fonte at� ele
	 * @param no � o n�
	 * @param distancia � a dist�ncia da fonte at� o n�
	 * */
	private static void setMenorDistancia(No no, double distancia) {
		nosNaoSelecionados.remove(no);
		if (distancia < getMenorDistancia(no)){
			menoresDistancias.put(no, distancia);
		}
		nosNaoSelecionados.add(no);
	}



    private static No getAntecessor(No no){
    	return antecessores.get(no);
    }


    public static synchronized Caminho getMenorCaminho(AbstractGrafo graph, No origem, No destino) throws ExcecaoGrafo{
    	grafo = graph;
    	executar(origem, destino);

    	Caminho caminho = new Caminho(origem, destino);
    	caminho.setDistancia(getMenorDistancia(destino));

    	No antecessor = getAntecessor(destino);

    	//conditional statement added in Sep 15, 2015.
    	if (antecessor == null) {
    		return caminho;
    	}

    	caminho.adicionarNo(destino);
    	caminho.adicionarEnlace(grafo.getEnlace(antecessor, destino));

       	while (antecessor!=null){
    		if(antecessor != null ){
    			caminho.adicionarNo(antecessor);
    			if(getAntecessor(antecessor)!=null)
    				caminho.adicionarEnlace(grafo.getEnlace(getAntecessor(antecessor), antecessor));
    		}
    		antecessor = getAntecessor(antecessor);
    	}
       	caminho.inverter();
    	return caminho;

    }





}
