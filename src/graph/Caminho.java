package graph;

import java.util.Iterator;
import java.util.Vector;

import opticalnetwork.EnlaceOptico;

public class Caminho extends AbstractGrafo implements Comparable<Caminho>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private No origem;
	private No destino;

	private double distancia;
	public Caminho(){

	}
	public Caminho(No origem, No destino){
		this.origem = origem;
		this.destino = destino;
	}

	public void limpar(){
		origem = null;
		destino = null;
		distancia = 0.0;
		getEnlaces().limpar();
		getNos().limpar();
	}

	@Override
	public void adicionarEnlace(No esq, No dir) throws ExcecaoGrafo {
		Enlace enlace = new EnlaceOptico(esq,dir);
		getEnlaces().adicionarEnlace(enlace);
	}

	@Override
	public void adicionarEnlace(Enlace enlace) throws ExcecaoGrafo {
		getEnlaces().adicionarEnlace(enlace);
	}

	@Override
	public void adicionarEnlace(No esq, No dir, double peso)
			throws ExcecaoGrafo {
//		Enlace enlace = new EnlaceOptico(esq,dir,peso);
//		getEnlaces().adicionarEnlace(enlace);
	}

	public No getOrigem() {
		return origem;
	}

	public void setOrigem(No origem) {
		this.origem = origem;
	}

	public No getDestino() {
		return destino;
	}

	public void setDestino(No destino) {
		this.destino = destino;
	}



	public double getDistancia() {
		return distancia;
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}

	@Override
	public Caminho clone() {
		Caminho novo = new Caminho(this.origem, this.destino);
		novo.setEnlaces(this.getEnlaces());
		novo.setNos(this.getNos());
		novo.setDistancia(this.distancia);

		return novo;
	}
	@Override
	public String toString(){
		return getNos().toString();
	}

	public String toStringNodeNames(){
		Vector<String> names = new Vector<>();
		for (Iterator<No> iterator = getNos().getIterator(); iterator.hasNext();) {
			No no = iterator.next();
			names.add(no.getName());

		}
		return names.toString();
	}

	public void inverter(){
		getEnlaces().inverter();
		getNos().inverter();
	}

	/**
	 * Returns the index of node in the list
	 * @return the index
	 */
	public int getIndexInPath(No node) {
		Vector<No> nosList = new Vector<>();
		nosList.addAll(getNos().valores());
		return nosList.indexOf(node);
	}

	/**
	 * Used to sort this object in a heap for shortest paths
	 * @param o The path to compare
	 *
	 */
	@Override
	public int compareTo(Caminho o) {
		if (o.getDistancia() != this.getDistancia()) {
			if (o.getDistancia() < this.getDistancia())
				return 1;
			else if(o.getDistancia() > this.getDistancia())
				return -1;
			else return 0;
		} else {
			if (o.getNos().tamanho() < this.getNos().tamanho())
				return 1;
			else if(o.getNos().tamanho() > this.getNos().tamanho())
				return -1;
			else return 0;
		}
	}

	/**
     * Returns the subpath from nodeStart (inclusive) to nodeStop (exclusive)
     */
    public Caminho subPath(No nodeStart, No nodeStop) throws Exception {
    	Caminho subPath = new Caminho();
    	No source = null;
    	No destination = null;
    	boolean crawlingSubPath = false;
    	double distance = 0.0;
    	double count = 0.0;
    	double distanceBeforeStart=0.0;
    	for (Iterator<Enlace> it = getEnlaces().valores().iterator(); it.hasNext() ; ) {
    		Enlace link = it.next();
    		No left = link.getNoEsquerda();
    		No right = link.getNoDireita();
    		if (left.equals(nodeStart)) {
    			source = left;
    			crawlingSubPath = true;
//    			subPath.adicionarNo(left);
//    			subPath.adicionarEnlace(link);
    			distanceBeforeStart = count;
    		}

    		if (crawlingSubPath) {
    			subPath.adicionarNo(left);

    			if (right.equals(nodeStop)) {
//    				subPath.adicionarNo(right);
    				destination = right;
    				distance = count - distanceBeforeStart;
    				break;
    			} else {
    				subPath.adicionarEnlace(link);
    			}

    		}
    		count += link.getDistancia();

    	}
    	subPath.setDistancia(distance);
    	subPath.setOrigem(source);
    	subPath.setDestino(destination);

    	if (source == null || destination == null || subPath.getEnlaces().tamanho() == 0) {
            throw new Exception("There is no such sub path!");
        } else {
//            return path.subList(startIndex,stopIndex);
        	return subPath;
        }
/*
        int startIndex, stopIndex;
        Vector<No> path = new Vector<>();
        path.addAll(this.getNos().valores());
        startIndex = path.indexOf(nodeStart);
        stopIndex = path.indexOf(nodeStop,startIndex);

        if (startIndex == -1 || stopIndex == -1) {
            throw new Exception("There is no such sub path!");
        } else {
//            return path.subList(startIndex,stopIndex);
        	return subPath;
        }
*/
    }


    /**
     * Returns the subpath from nodeStart (inclusive) to nodeStop including the last one depending of
     * the boolean parameter isInclusiveNodeStop has been setup to <code>true</code> .
     */
    public Caminho subPath(No nodeStart, No nodeStop, boolean isInclusiveNodeStop) throws Exception {
    	Caminho subPath = new Caminho();
    	No source = null;
    	No destination = null;
    	boolean crawlingSubPath = false;
    	double distance = 0.0;
    	double count = 0.0;
    	double distanceBeforeStart=0.0;
    	for (Iterator<Enlace> it = getEnlaces().valores().iterator(); it.hasNext() ; ) {
    		Enlace link = it.next();
    		No left = link.getNoEsquerda();
    		No right = link.getNoDireita();
    		if (left.equals(nodeStart)) {
    			source = left;
    			crawlingSubPath = true;
    			distanceBeforeStart = count;
    		}

    		if (crawlingSubPath) {
    			subPath.adicionarNo(left);

    			if (right.equals(nodeStop)) {
    				if (isInclusiveNodeStop) {
    					subPath.adicionarNo(right);
    					subPath.adicionarEnlace(link);
    					count += link.getDistancia();
    				}
    				destination = right;
    				distance = count - distanceBeforeStart;
    				break;
    			} else {
    				subPath.adicionarEnlace(link);
    			}

    		}
    		count += link.getDistancia();

    	}
    	subPath.setDistancia(distance);
    	subPath.setOrigem(source);
    	subPath.setDestino(destination);

    	if (source == null || destination == null || subPath.getEnlaces().tamanho() == 0) {
            throw new Exception("There is no such sub path!");
        } else {
        	return subPath;
        }

    }


    public Caminho concatenate (Caminho anotherPath) {
    	this.getNos().adicionarColecaoNos(anotherPath.getNos().getNos());
    	this.getEnlaces().adicionarColecaoEnlaces(anotherPath.getEnlaces().getEnlaces());
    	this.setDestino(anotherPath.getDestino());
    	double distance = this.getDistancia() + anotherPath.getDistancia();
		this.setDistancia(distance);
    	return this;
    }
    
    public boolean isBidirectionalEquals (Caminho otherPath) {
    	String pathString = this.toString();
    	Caminho invert = this.clone();
    	invert.inverter();
    	String pathInverse = invert.toString();
    	
    	if (pathString.equals(otherPath.toString()) || pathInverse.equals(otherPath.toString())) {
    		return true;
    	}
    	
    	return false;
    }

}
