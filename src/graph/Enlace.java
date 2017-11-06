package graph;

import java.util.HashMap;

import opticalnetwork.LinkStateTable;

/**
 *@author Alaelson Jatob�
 *@version 1.0
 */

public interface Enlace {

	public String getId();
	/**
	 * @return n� origem do enlace
	 * */
	public No getNoEsquerda();

	/**
	 * @return n� destino do enlace
	 * */
	public No getNoDireita();

	/**
	 * @return o peso do enlace
	 * */
	public double getPeso();

	/**
	 * @param esq � o n� origem do enlace
	 * */
	public void setNoEsquerda(No esq);

	/**
	 * @param dir � o n� destino do enlace
	 * */
	public void setNoDireita(No dir);

	/**
	 * @param peso � o peso do enlace
	 * */
	public void setPeso(double peso);


	/**
	 * Verifica o estado do enlace, ou seja, se este enlace est� ativado ou n�o.
	 * @return <tt>true</tt> se o enlace estiver ativado
	 * sen�o retorn "false"
	 * */
	public boolean isAtivado();

	/**
	 * Configura o estado do enlace
	 * @param ativado <tt>true</tt> para ativar ou <tt>false</tt> para desativar.
	 * */
	public void setAtivado(boolean ativado);

	/**
	 * Sets the distance between the source and destination node;
	 * @param distancia the distance
	 * */
	public void setDistancia(double distancia);

	/**
	 * Returns the distance between the source and destination node;
	 *
	 * */
	public double getDistancia();

	/**
	 * Starts a state table for each spectrum array in each dimension
	 * @param dimensions a int value which representing the number of dimensions in the link. Eg. The number of fibers.
	 * @param mask the mask as a boolean array be installed in each row of the state table.
	 *
	 * */
	public void installStateTable ( int dimensions, Boolean[] mask ) ;

	/**
	 * Returns a boolean array representing the spectral slots of a dimension (Eg. SMF fiber or mode)
	 * @param dimension is the index of the dimension
	 * @return {@link Boolean}[] a boolean array
	 * */
	public Boolean[] getStateSpectralArray (int dimension) ;
	
	/**
	 * @return the linkStateTable
	 */
	public LinkStateTable getLinkStateTable() ;

	/**
	 * @param linkStateTable the linkStateTable to set
	 */
	public void setLinkStateTable(LinkStateTable linkStateTable) ;

	/**
	 * @return the dimensionPathTable
	 */
	public HashMap<Integer,Caminho> getDimensionPathTable();
	
	


	/**
	 * @param dimensionPathTable the dimensionPathTable to set
	 */
	public void setDimensionPathTable(HashMap<Integer,Caminho> dimensionPathTable);
	
	
	/**
	 * Adds the dimension as primary key and the path in a hashmap
	 * @param dimension
	 * @param path
	 */
	public void addDimensionInPathTable(Integer dimension, Caminho path);
	
	/**
	 * Returns the path which is using the dimension specified
	 * @param dimension
	 * @return
	 */
	public Caminho getPathInDimensionPathTable(int dimension);
	
	public boolean isDimensionBelongsPath (int dimension, Caminho path);

}
