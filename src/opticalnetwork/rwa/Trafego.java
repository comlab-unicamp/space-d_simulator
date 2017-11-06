/*
 * criado em 09/09/2008 
 */
package opticalnetwork.rwa;

import java.io.Serializable;
import java.util.HashMap;

import distribution.Distribution;
import distribution.Uniforme;
import event.SourceGenerator;
import event.Event.Type;
import graph.AbstractGrafo;
import graph.Caminho;
import graph.Grafo;
import graph.No;

/**
 * @author Rodrigo
 * @version 1.0
 */
public class Trafego implements Serializable, SourceGenerator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Type tipo;
	private int contador = 0;
	private Uniforme uniforme;
	private AbstractGrafo grafo;
	private int stop;
	private Distribution distribuicao;
	private HashMap<String, Caminho> tabelaDeRotas;

	/** Cria trafego na escalonador.
	 * @see event.SourceGenerator#getContent()
	 */
	
	
	public Trafego(int semente, Grafo grafo,Distribution distribuicao, int stop){ 
		this.tipo = Type.NEW_REQUEST;
		this.grafo = grafo;
		this.stop = stop;
		this.uniforme = new Uniforme(semente);
		this.distribuicao = distribuicao;
		
	}
	
	public void setTabelaDeRotas(HashMap<String,Caminho> tabelaDeRotas){
		this.tabelaDeRotas = tabelaDeRotas;
	}
	
	Caminho getCaminho(String key){
		return tabelaDeRotas.get(key); 
	}
	
	public Object getContent() {
		
		Integer no1 = uniforme.sorteiaNo(grafo.getNos().tamanho());
		Integer no2 = uniforme.sorteiaNo(grafo.getNos().tamanho(), no1);
		No origem = grafo.getNo(no1.toString());
		No destino = grafo.getNo(no2.toString());
		
		if(getContador()<stop){
			contador++;
			Requisicao req = new Requisicao(origem,destino,getContador(),distribuicao.getHoldingTime());
			return req;
		}
		
		return null;
		
	}
	

	/**
	 * @return the contador
	 */
	public int getContador() {
		return contador;
	}

	/* (non-Javadoc)
	 * @see evento.Fonte#getTipo()
	 */
	@Override
	public Type getEventType() {
		return this.tipo;
	}

	@Override
	public void setEventType(Type eventType) {
		this.tipo = eventType;
		
	}

	

	

}
