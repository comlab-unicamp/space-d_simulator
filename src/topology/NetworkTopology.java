/**
 * Created on 22/01/2016
 */
package topology;

import graph.AbstractGrafo;
import graph.Grafo;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public enum NetworkTopology {

	GERMAN("German Network"),
	US_NETWORK("US Network"),
	US_NETWORK_17("US Network with 17 Nodes"),
	EUROPEAN("European Network"),
	SPANISH("Spanish Network"),
	NSFNET("NSF Network"),
	SINGLE_LINK("Single Link"),
	KSP_PAVANI_TESE("K Shortest Path - Tese Gustavo Pavani"), 
	NETWORK4NODES ("Network with 4 nodes as a ring"),
	JNP12 ("Japan Photonic Network with 12 nodes ");

	String name;

	NetworkTopology(String name){
		this.name=name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString(){
		return getName();
	}

	public static AbstractGrafo getGraph(NetworkTopology net) {
		AbstractGrafo graph = null;
		switch (net) {
		case GERMAN:
			Grafo german = new GermanNetwork2();
			graph = german;
			break;
		case US_NETWORK:
			Grafo usnet = new USNetwork();
			graph = usnet;
			break;
		case US_NETWORK_17:
			Grafo usnet17 = new USNetwork_17();
			graph = usnet17;
			break;
		case EUROPEAN:
			Grafo euro = new EuropeanNetwork();
			graph = euro;
			break;
		case SPANISH:
			Grafo spanish = new SpanishNetwork();
			graph = spanish;
			break;
		case NSFNET:
			Grafo nsfnet = new NSFNet();
			graph = nsfnet;
			break;
		case SINGLE_LINK:
			Grafo sl = new SingleLink();
			graph = sl;
			break;
		case KSP_PAVANI_TESE:
			Grafo pavani = new KSPTesePavaniNetwork();
			graph = pavani;
			break;
		case NETWORK4NODES:
			graph = new Network4Nodes();
			break;
		case JNP12:
			graph = new JNP12();
			break;
		default:
			break;
		}
		return graph;
	}

	public static void main (String[] args) {
		AbstractGrafo graph = getGraph(NetworkTopology.valueOf("SPANISH"));
		System.out.println(graph.getNetwork());
	}

}
