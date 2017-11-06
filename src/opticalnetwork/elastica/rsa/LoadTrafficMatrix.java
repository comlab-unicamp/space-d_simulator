package opticalnetwork.elastica.rsa;

import graph.AbstractGrafo;
import graph.Caminho;
import graph.ExcecaoGrafo;
import graph.No;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

public class LoadTrafficMatrix {
	protected TrafficMatrixFactory factory ;
	protected AbstractGrafo graph;
//	private String filename;
//	private HashMap<String,Caminho> tabelaDeRotas;

	public LoadTrafficMatrix(AbstractGrafo grafo) throws Exception {
		this.graph = grafo;
		factory = new TrafficMatrixFactory(grafo);
//		this.tabelaDeRotas = factory.doRouteTable(grafo);
	}


//	public LoadTrafficMatrix(AbstractGrafo grafo, String nomeDoArquivo) throws Exception {
//		this.graph = grafo;
//		this.filename = nomeDoArquivo;
//		factory = new TrafficMatrixFactory(grafo);
//		this.tabelaDeRotas = new HashMap<String,Caminho>();
//	}


	public HashMap<String,Caminho> getRouteTable() throws ExcecaoGrafo{
		return factory.getRouteTable();
	}

	public No getNodeByName (String name) {
		for (Iterator<No> it = graph.getNos().getIterator(); it.hasNext(); ) {
			No node = it.next();
			if (node.getName().equalsIgnoreCase(name)) {
				return	node;
			}
		}

		return null;
	}

	public TrafficMatrix load (String filename) throws Exception {
		int n = graph.getNos().tamanho()+1;
		String tokens[] = new String[n]; //Vetor onde deveria armazenar os valores do arquivo
		InputStream e = new FileInputStream(filename);
		InputStreamReader er = new InputStreamReader(e);
		BufferedReader ebr = new BufferedReader(er);
		String line = ebr.readLine(); // O método readLine() apenas lê uma linha do arquivo
		String[] collumns = line.split(";");
//		HashMap<Integer,String> collumnPosition = new HashMap<>();
		line = ebr.readLine();

		while( line != null){
			tokens = line.split(";");
			String sourceName = tokens[0];
			No source = graph.getNodeByName(sourceName);
			No destination = null;

			for(int k = 1; k < tokens.length; k++) {

				if (tokens[k].equals("")){
					k++;
				}
				String destName = collumns[k];
				destination = graph.getNodeByName(destName);
				Double num = Double.parseDouble(tokens[k]);
				String key = null;

				if (!source.getId().equals(destination.getId())) {
					key = source.getId()+"-"+destination.getId();
					factory.setTraffic(key, num);

				}
			} // end for
			line = ebr.readLine();
		} //end while



		ebr.close();
		factory.calculateMatrixTotalCapacity();
		return factory.getMatrix();

	}




}
