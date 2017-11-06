package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */

public class ListaNos {

	private Map<String,No> nos;

	public ListaNos(){
		nos = new LinkedHashMap<String, No>();
	}


	/**
	 * Adiciona um No na lista
	 * @param no Objeto {@link No} a ser adicionado
	 * */
	public void adicionarNo(No no) throws ExcecaoGrafo{
		if(no == null)
			throw new ExcecaoGrafo("No nulo!");
		nos.put(no.getId(),no);
	}

	/**
	 * Adiciona uma coleção de Nós na lista existente
	 * @param nos Coleção de Nós a ser adicionada
	 * */
	public void adicionarColecaoNos(Map<String,? extends No> nos){
		this.nos.putAll(nos);
	}

	/**
	 * Retorna a lista de Nos
	 * @return A lista de Nos
	 * */
	public Map<String, No> getNos() {
		return nos;
	}

	public No getNo(String id){
		return getNos().get(id);
	}

	public No getNo(No no){
		return getNos().get(no.getId());
	}

	/**
	 * Configura a lista de Nos
	 * @param nos � uma lista de Nos
	 * */
	public void setNos(Map<String,No> nos) {
		this.nos = nos;
	}


	public boolean contem(No no) {
		return nos.containsKey(no.getId());

	}

	public boolean contem(String id) {
		return nos.containsKey(id);

	}


	public No remover(No no) {
		return nos.remove(no.getId());
	}

	@Override
	public String toString(){
		return chaves().toString();
	}

	public int tamanho(){
		return nos.size();
	}

	public Collection<No> valores(){
		return nos.values();
	}

	public Set<String> chaves(){
		return nos.keySet();
	}

	public void limpar(){
		nos.clear();
	}

	public void adicionarNos(Collection<No> _nos){
		for(No no : _nos){
			nos.put(no.getId(), no);
		}
	}

	public void inverter(){
		ArrayList<No> _nos = new ArrayList<No>(valores());
		Collections.reverse(_nos);
		limpar();
		adicionarNos(_nos);

	}

	public Iterator<No> getIterator(){
		Iterator<No> iterator= nos.values().iterator();
		return iterator;
	}

	@Override
	public Object clone () {
		ListaNos list = new ListaNos();
		for (Iterator<No> iterator = nos.values().iterator(); iterator.hasNext();) {
			No no = iterator.next();
			list.nos.put(no.getId(),(No)no.clone());

		}
		return list;
	}

}
