/**
 * Created on 15/09/2015
 */
package graph;

import opticalnetwork.elastica.ElasticLink;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class OpticalGraph extends AbstractGrafo{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void adicionarEnlace(No esq, No dir) throws ExcecaoGrafo {
		Enlace enlace = new ElasticLink(esq,dir);
		esq.adicionarEnlace(enlace);
		getEnlaces().adicionarEnlace(enlace);
	}

	@Override
	public void adicionarEnlace(No esq, No dir, double peso)
	throws ExcecaoGrafo { // implementar o construtor abaixo de enlace ótico
//		Enlace enlace = new EnlaceOptico(esq,dir,peso);
//		esq.adicionarEnlace(enlace);
//		getEnlaces().adicionarEnlace(enlace);
	}
	@Override
	public void adicionarEnlace(Enlace enlace) throws ExcecaoGrafo {
		getEnlaces().adicionarEnlace(enlace);
	}



	@Override
	public Grafo clone() {
		Grafo novo = new Grafo();
		novo.setEnlaces(this.getEnlaces());
		novo.setNos(this.getNos());
		return novo;
	}

	@Override
	public String toString (){
		StringBuffer buffer = new StringBuffer();
		for (String s : this.getEnlaces().chaves()) {
			buffer.append(s).append("\n");
		}
		return buffer.toString();

	}


}
