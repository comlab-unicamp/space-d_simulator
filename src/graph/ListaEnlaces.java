package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Alaelson Jatob�
 * @version 1.0
 * */
public class ListaEnlaces {

	private Map<String, Enlace> enlaces;

	public ListaEnlaces(){
		enlaces = new LinkedHashMap<String,Enlace>();
	}


	/**
	 * Adiciona um enlace na lista
	 * @param enlace � o enlace a ser adicionado
	 * */
	public void adicionarEnlace(Enlace enlace) throws ExcecaoGrafo{
		if(enlace == null)
			throw new ExcecaoGrafo("Enlace nulo!");
		if(enlaces.containsKey(enlace.getId())){
			throw new ExcecaoGrafo("Enlace (" + enlace.getId() + ") ja existe!");
		}
		enlaces.put(enlace.getId(), enlace);
	}

	/**
	 * Adiciona uma cole��o de enlaces na lista existente
	 * @param enlaces � uma cole��o de enlaces a ser adicionada
	 * */
	public void adicionarColecaoEnlaces(Map<String,? extends Enlace> enlaces){
		this.enlaces.putAll(enlaces);
	}

	/**
	 * Retorna a lista de enlaces
	 * @return A lista de enlaces
	 * */
	public Map<String,Enlace> getEnlaces() {
		return enlaces;
	}

	/**
	 * configura a lista de enlaces
	 * @param enlaces � uma lista de enlaces
	 * */
	public void setEnlaces(Map<String,Enlace> enlaces) {
		this.enlaces = enlaces;
	}


	public boolean contemEnlace(Enlace enlace) {
		return enlaces.containsKey(enlace.getId());
	}

	public boolean contemEnlace(No origem, No destino) {
		StringBuilder builder = new StringBuilder();
		builder.append(origem.getId());
		builder.append("-");
		builder.append(destino.getId());
		return enlaces.containsKey(builder.toString());
	}


	public Enlace getEnlace(No origem, No destino) {
		StringBuilder builder = new StringBuilder();
		if (origem != null ) {
			builder.append(origem.getId());
			builder.append("-");
		}

		if (destino != null) {
			builder.append(destino.getId());
		}
		return enlaces.get(builder.toString());
	}


	public Enlace remover(Enlace enlace) throws ExcecaoGrafo{
		return enlaces.remove(enlace.getId());

	}

	@Override
	public String toString(){
		return getEnlaces().keySet().toString();
	}

	public int tamanho(){
		return enlaces.size();
	}

	public Collection<Enlace> valores(){
		return enlaces.values();
	}

	public Set<String> chaves(){
		return enlaces.keySet();
	}
	public void limpar(){
		enlaces.clear();
	}

	public void adicionarEnlaces(Collection<Enlace> _enlaces){
		for(Enlace e : _enlaces){
			enlaces.put(e.getId(), e);
		}
	}

	public void inverter(){
		ArrayList<Enlace> _enlaces = new ArrayList<Enlace>(valores());
		Collections.reverse(_enlaces);
		limpar();
		adicionarEnlaces(_enlaces);

	}


}
