/**
 *
 */
package opticalnetwork.elastica;

import graph.Enlace;
import graph.No;

/**
 * @deprecated incomplete class implementation
 * Created in 24/08/2015
 * By @author Alaelson Jatoba
 * @version 1.0
 */
@SuppressWarnings("unused")
public abstract class Link implements Enlace{

	/**the id as formed by source and destination. Ex: 1-2*/
	private String id;
	/**a {@link No} object used as source*/
	private No source;
	/** a {@link No} object used as destination*/
	private No destination;
	/**The number cores in a multicore fiber or modes in a multimode fiber*/
	private int numberOfModes;
	/** the max number of spectral slots supported*/
	private int numSpectralSlots;
	/** the weight that would be used by the route algorithm*/
	private double weight;
	/**the total capacity of the link in Tbps*/
	private double capacity;
	/**the distance between the source and destination*/
	private double distance;
	/**the propagation time*/
	private double propagationTime;
	/**the {@link LinkType} used to specity if this link is monomode or multimode*/
	private LinkType linkType;
	/**If this attribute is set to <code>true</code> then this link is actived in the network, otherwise, it isn't actived (<code>false</code>)*/
	private boolean active;




	/** Return the Enlaces's ID
	 * @see grafo.Enlace#getId()
	 */

	@Override
	public String getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#getNoEsquerda()
	 */
	@Override
	public No getNoEsquerda() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#getNoDireita()
	 */
	@Override
	public No getNoDireita() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#getPeso()
	 */
	@Override
	public double getPeso() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#setNoEsquerda(grafo.No)
	 */
	@Override
	public void setNoEsquerda(No esq) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#setNoDireita(grafo.No)
	 */
	@Override
	public void setNoDireita(No dir) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#setPeso(double)
	 */
	@Override
	public void setPeso(double peso) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#isAtivado()
	 */
	@Override
	public boolean isAtivado() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#setAtivado(boolean)
	 */
	@Override
	public void setAtivado(boolean ativado) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#setDistancia(double)
	 */
	@Override
	public void setDistancia(double distancia) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see grafo.Enlace#getDistancia()
	 */
	@Override
	public double getDistancia() {
		// TODO Auto-generated method stub
		return 0;
	}

}


