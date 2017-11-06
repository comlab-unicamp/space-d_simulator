package opticalnetwork;

import java.util.HashMap;

import graph.Caminho;
import graph.Enlace;
import graph.No;

/**
*@author Alaelson Jatobá
*@version 1.0
*updated in February 2, 2016
*/
public class EnlaceOptico implements Enlace {

	private String id;
	private No noEsquerda;
	private No noDireita;
	private double peso;
	private double tempoPropagacao;
	private boolean ativado;
	private double distancia;
	private LinkStateTable linkStateTable;
	/**Saves the dimension as primary key and the path*/
	private HashMap<Integer,Caminho> dimensionPathTable;

	public EnlaceOptico (){
		this.dimensionPathTable = new HashMap<Integer, Caminho>();
	}

	/**
	 * The constructor
	 * @param esq left node of the edge
	 * @param dir right node of the edge
	 */
	public EnlaceOptico ( No esq, No dir ) {
		this();
		this.id =criaID(esq, dir);
		this.noEsquerda = esq;
		this.noDireita = dir;
		this.peso = 1.0;
		this.tempoPropagacao = 0.0;
		this.ativado = true;
		this.distancia = 1.0;
		this.linkStateTable = new LinkStateTable();
	}

	private String criaID( No esq, No dir ){
		StringBuilder builder = new StringBuilder();
		builder.append(esq.getId());
		builder.append("-");
		builder.append(dir.getId());
		return builder.toString();
	}
	/**
	 * @param origem No origem
	 * @param destino No de destino
	 * @param distancia Distancia em Km
	 * */
	public EnlaceOptico (double distancia, No origem, No destino) {
		this(origem,destino);
		setDistancia(distancia);
		setTempoPropagacao(distancia/200000);//divide pela velocidade da luz na fibra

	}
	/**
	 * @param origem No origem
	 * @param destino No de destino
	 * @param distancia � a distancia em Km
	 * @param ativado configura o estado do enlace
	 * */
	public EnlaceOptico ( No origem, No destino, double distancia, boolean ativado ) {
		this(distancia, origem, destino);
		setAtivado(ativado);
	}
	/**
	 * @param origem No origem
	 * @param destino No de destino
	 * @param ativado configura o estado do enlace
	 * */
	public EnlaceOptico ( No origem, No destino, boolean ativado ) {
		this(origem, destino);
		setAtivado(ativado);
	}

	@Override
	public No getNoDireita() {
		return noDireita;
	}

	@Override
	public No getNoEsquerda() {
		return noEsquerda;
	}

	@Override
	public void setNoDireita(No destino) {
		this.noDireita = destino;

	}

	@Override
	public void setNoEsquerda(No origem) {
		this.noEsquerda = origem;

	}

	/**
	 * Retorna o tempo de propaga��o do enlace
	 * @return tempoPropacacao � o tempo de propagacao do enlace
	 * */
	public double getTempoPropagacao() {
		return tempoPropagacao;
	}

	/**
	 * @param tempoPropacacao � o tempo de propagacao do enlace
	 * */
	public void setTempoPropagacao(double tempoPropagacao) {
		this.tempoPropagacao = tempoPropagacao;
	}

	@Override
	public double getPeso() {
		return peso;
	}

	@Override
	public void setPeso(double peso) {
		this.peso = peso;
	}

	@Override
	public boolean isAtivado() {
		return ativado;
	}

	@Override
	public void setAtivado(boolean ativado) {
		this.ativado = ativado;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append(id);
		return builder.toString();
	}


	@Override
	public String getId() {
		return id;
	}

	@Override
	public double getDistancia() {
		return distancia;
	}

	@Override
	public void setDistancia(double distancia) {
		this.distancia = distancia;

	}

	/**
	 * Starts a state table for each spectrum array in each dimension
	 * @param dimensions a int value which representing the number of dimensions in the link. Eg. The number of fibers.
	 * @param mask the mask as a boolean array be installed in each row of the state table.
	 *
	 * */
	@Override
	public void installStateTable ( int dimensions, Boolean[] mask ) {
		linkStateTable.installStateTable(dimensions, mask);
	}

	/**
	 * Returns a boolean array representing the spectral slots of a dimension (Eg. SMF fiber or mode)
	 * @param dimension is the index of the dimension
	 * @return {@link Boolean}[] a boolean array
	 * */
	@Override
	public Boolean[] getStateSpectralArray (int dimension) {
		return this.linkStateTable.getStateSpectralArray(dimension);
	}

	/**
	 * @return the linkStateTable
	 */
	public LinkStateTable getLinkStateTable() {
		return linkStateTable;
	}

	/**
	 * @param linkStateTable the linkStateTable to set
	 */
	public void setLinkStateTable(LinkStateTable linkStateTable) {
		this.linkStateTable = linkStateTable;
	}
	
	/**
	 * @return the dimensionPathTable
	 */
	public HashMap<Integer,Caminho> getDimensionPathTable() {
		return dimensionPathTable;
	}
	
	


	/**
	 * @param dimensionPathTable the dimensionPathTable to set
	 */
	public void setDimensionPathTable(HashMap<Integer,Caminho> dimensionPathTable) {
		this.dimensionPathTable = dimensionPathTable;
	}
	
	
	/**
	 * Adds the dimension as primary key and the path in a hashmap
	 * @param dimension
	 * @param path
	 */
	public void addDimensionInPathTable(Integer dimension, Caminho path) {
		this.dimensionPathTable.put(dimension, path);
	}
	
	/**
	 * Returns the path which is using the dimension specified
	 * @param dimension
	 * @return
	 */
	public Caminho getPathInDimensionPathTable(int dimension) {
		return this.dimensionPathTable.get(dimension);
	}
	
	public boolean isDimensionBelongsPath (int dimension, Caminho path) {
		Caminho pathSaved = this.dimensionPathTable.get(dimension);
		if (path.equals(pathSaved)) {
			return true;
		}
		return false;				
	}



}
