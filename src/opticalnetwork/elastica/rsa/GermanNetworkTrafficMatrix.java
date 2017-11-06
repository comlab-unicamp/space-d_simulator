/**
 * Created on 28/09/2015
 */
package opticalnetwork.elastica.rsa;

import graph.AbstractGrafo;
import graph.No;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;



/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class GermanNetworkTrafficMatrix extends LoadTrafficMatrix{

	public GermanNetworkTrafficMatrix(AbstractGrafo graph) throws Exception {
		super(graph);
	}


	/* 1	Norden	No	25099
	 * 2	Bremen	HB	546451
	 * 3	Hamburg	HH	1751775
	 * 4	Berlin	B	3562166
	 * 5	Hannover H	518386
	 * 6	Essen	E	569884
	 * 7	Dortmund Do	575944
	 * 8	Düsseldorf	D 598686
	 * 9	Leipzig	L	544479
	 * 10	Köln	K	1034175
	 * 11	Frankfurt F	701350
	 * 12	Mannheim Ma	296690
	 * 13	Nürnberg N	498876
	 * 14	Karlsruhe Ka	299103
	 * 15	Stuttgart S	604297
	 * 16	Ulm		Ul	119218
	 * 17	München	 M	1407836*/

	@Override
	public TrafficMatrix load (String filename) throws Exception {
		int n = graph.getNos().tamanho()+1;
//		String matriz[][] = new String[n][n]; // Matriz onde contém 26 linhas e 26 colunas
		//	        int n = 17*17; //
		String tokens[] = new String[n]; //Vetor onde deveria armazenar os valores do arquivo
		InputStream e = new FileInputStream(filename);
		InputStreamReader er = new InputStreamReader(e);
		BufferedReader ebr = new BufferedReader(er);
		String line = ebr.readLine(); // O método readLine()apens lê uma linha do arquivo
		String[] collumns = line.split(";");
		HashMap<Integer,String> collumnPosition = new HashMap<>();
		for (int i = 1 ; i < collumns.length ; i++) {
			No node = null;
			switch (collumns[i]) {
			case "B":
				node = getNodeByName("Berlin");
				collumnPosition.put(i, node.getId());
				break;
			case "HB":
				node = getNodeByName("Bremen");
				collumnPosition.put(i, node.getId());
				break;
			case "Do":
				node = getNodeByName("Dortmund");
				collumnPosition.put(i, node.getId());
				break;
			case "D":
				node = getNodeByName("Düsseldorf");
				collumnPosition.put(i, node.getId());
				break;
			case "E":
				node = getNodeByName("Essen");
				collumnPosition.put(i, node.getId());
				break;
			case "F":
				node = getNodeByName("Frankfurt");
				collumnPosition.put(i, node.getId());
				break;
			case "HH":
				node = getNodeByName("Hamburg");
				collumnPosition.put(i, node.getId());
				break;
			case "H":
				node = getNodeByName("Hannover");
				collumnPosition.put(i, node.getId());
				break;
			case "Ka":
				node = getNodeByName("Karlsruhe");
				collumnPosition.put(i, node.getId());
				break;
			case "K":
				node = getNodeByName("Köln");
				collumnPosition.put(i, node.getId());
				break;
			case "L":
				node = getNodeByName("Leipzig");
				collumnPosition.put(i, node.getId());
				break;
			case "Ma":
				node = getNodeByName("Mannheim");
				collumnPosition.put(i, node.getId());
				break;
			case "M":
				node = getNodeByName("München");
				collumnPosition.put(i, node.getId());
				break;
			case "No":
				node = getNodeByName("Norden");
				collumnPosition.put(i, node.getId());
				break;
			case "N":
				node = getNodeByName("Nürnberg");
				collumnPosition.put(i, node.getId());
				break;
			case "S":
				node = getNodeByName("Stuttgart");
				collumnPosition.put(i, node.getId());
				break;
			case "Ul":
				node = getNodeByName("Ulm");
				collumnPosition.put(i, node.getId());
				break;

			default:
				break;
			}
		}

		line = ebr.readLine();

		while( line != null){
			tokens = line.split(";");
			String nodeName = tokens[0];

			for(int k = 1; k < tokens.length; k++) {
				if (tokens[k].equals("")){
					k++;
				}
				Double num = Double.parseDouble(tokens[k]);
				String key = null;
				No node = null;

				if (num!=null) {

					switch (nodeName) {
					case "B":
						node = getNodeByName("Berlin");
						break;
					case "HB":
						node = getNodeByName("Bremen");
						break;
					case "Do":
						node = getNodeByName("Dortmund");
						break;
					case "D":
						node = getNodeByName("Düsseldorf");
						break;
					case "E":
						node = getNodeByName("Essen");
						break;
					case "F":
						node = getNodeByName("Frankfurt");
						break;
					case "HH":
						node = getNodeByName("Hamburg");
						break;
					case "H":
						node = getNodeByName("Hannover");
						break;
					case "Ka":
						node = getNodeByName("Karlsruhe");
						break;
					case "K":
						node = getNodeByName("Köln");
						break;
					case "L":
						node = getNodeByName("Leipzig");
						break;
					case "Ma":
						node = getNodeByName("Mannheim");
						break;
					case "M":
						node = getNodeByName("München");
						break;
					case "No":
						node = getNodeByName("Norden");
						break;
					case "N":
						node = getNodeByName("Nürnberg");
						break;
					case "S":
						node = getNodeByName("Stuttgart");
						break;
					case "Ul":
						node = getNodeByName("Ulm");
						break;

					default:
						break;
					}

					if (!node.getId().equals(collumnPosition.get(k))) {
						key = node.getId()+"-"+collumnPosition.get(k);
						factory.setTraffic(key, num);

					}

				} // end if
			} // end for
			line = ebr.readLine();
		} //end while



		ebr.close();
		factory.calculateMatrixTotalCapacity();
		return factory.getMatrix();

	}


}
