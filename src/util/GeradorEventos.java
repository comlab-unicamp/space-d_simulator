/*
 * criado em 09/09/2008 
 */
package util;

import java.io.IOException;
import java.io.Serializable;

import distribution.Distribution;
import event.Event;
import event.SourceGenerator;

/**
 * Gera os eventos para o escalonador.
 * @author Rodrigo
 * @version 1.0
 */

public class GeradorEventos implements Serializable, Comparable<GeradorEventos>{
	private static final long serialVersionUID = 1L;
	/**
	 * Tempo do inicio de gera��o do evento.
	 */
	protected double tempoPartida;

	/**
	 * Tempo do pr�ximo evento a ser gerado.
	 */
	protected double tempoProximo;

	/**
	 * Distribui��o que rege a gera��o dos eventos.
	 */
	protected Distribution distribuicao;

	/**
	 * Fonte usada no gerador de eventos.
	 */
	protected SourceGenerator fonte;


	/**
	 * Cria um gerador de eventos com a seguinte distribui��o.
	 * @param distrib
	 */
	public GeradorEventos(Distribution distrib)throws IOException {
		this(distrib,0.0);
		
	}

	/**
	 * Cria um gerador de eventos com a seguinte distribui��o iniciando a partir do instante partida.
	 * @param distrib
	 * @param partida
	 */
	public GeradorEventos(Distribution distrib, double partida)throws IOException {
		this.distribuicao = distrib;
		this.tempoPartida = partida;
		this.tempoProximo = partida;
	}

	/**
	 * Compara o pr�ximo tempo de gera��o do evento com pr�ximo tempo especificado.
	 */
	@Override
	public int compareTo(GeradorEventos gerador) {
		if (gerador.getTempoProximo() < this.getTempoProximo())
			return 1;
		else if (gerador.getTempoProximo() > this.getTempoProximo())
			return -1;
		else return 0;	
	}

	/**
	 * @return the distribuicao
	 */
	public Distribution getDistribuicao() {
		return distribuicao;
	}

	/**
	 * @param distribuicao the distribuicao to set
	 */
	public void setDistribuicao(Distribution distribuicao) {
		this.distribuicao = distribuicao;
	}

	/**
	 * @return the fonte
	 */
	public SourceGenerator getFonte() {
		return fonte;
	}

	/**
	 * @param fonte the fonte to set
	 */
	public void setFonte(SourceGenerator fonte) {
		this.fonte = fonte;
		
	}

	/**
	 * Retorna o intervalo da pr�xima chegada.
	 * @return
	 */
	public double getIntervaloProximaChegada(){
		return distribuicao.getTimeBetweenArrivals();
	}

	/**
	 * Retorna o tempo de Partida
	 * @return the tempoPartida
	 */
	public double getTempoPartida() {
		return tempoPartida;
	}

	/**
	 * Atualiza o tempo de partida.
	 * @param tempoPartida the tempoPartida to set
	 */
	public void setTempoPartida(double tempoPartida) {
		this.tempoPartida = tempoPartida;
	}


	/**
	 * Retorna o tempo pr�ximo.
	 * @return the tempoProximo
	 */
	public double getTempoProximo() {
		return tempoProximo;
	}

	/**
	 * Atualiza o tempo pr�ximo.
	 * @param tempoProximo o pr�ximo tempo do evento atualizado.
	 */
	public void setTempoProximo(double tempoProximo) {
		this.tempoProximo = tempoProximo;
	}

	public Event criaEvento(double tempo) {
		return new Event(tempo, fonte.getEventType(), fonte.getContent());	
	}
	
	public Event criaEvento(double tempo, Object conteudo){
		return new Event(tempo, fonte.getEventType(), conteudo);	
	}
	
	public Event criaEvento(double tempo, Event.Type tipo, Object conteudo){
		return new Event(tempo, tipo, conteudo);	
	}
}
