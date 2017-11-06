/**
 * Created on 28/09/2015
 */
package opticalnetwork.elastica.rsa;

import graph.AbstractGrafo;
import topology.EuropeanNetwork;
import topology.GermanNetwork;

import java.text.DecimalFormat;
import java.util.Map.Entry;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class TrafficMatrixCreatorTest {

	@Test
	public void testLoadMatrixGerman () {
		String filename = "GERMANTRAFFICMATRICE.csv";
		AbstractGrafo graph = new GermanNetwork();
		try {
			GermanNetworkTrafficMatrix load = new GermanNetworkTrafficMatrix(graph);
			TrafficMatrix matrix = load.load(filename);
			System.out.println("Traffic Matrix German Network:");
			double total = 0.0;
			for (Entry<String ,Double> e :  matrix.getMatrixSimple().entrySet()) {
				System.out.println(e.getKey() + " = " + e.getValue());
				total+=e.getValue();
			}
			double v = total/2;
			DecimalFormat formatter = new DecimalFormat("#,##0.00");
			System.out.println("Total Traffic Volume V: " + formatter.format(v) + " Gbit/s");
			int n = graph.getNos().tamanho();
			double avgPerNode = 2*v/n;
			System.out.println("avg. traffic per node: " + formatter.format(avgPerNode) + " Gbit/s");
			double avgPerNodePair = 2*v/(n*(n-1));
			System.out.println("avg. traffic per node Pair: " + formatter.format(avgPerNodePair) + " Gbit/s");
//			double totalTrafficLoad =

			assertEquals(2.396,17, v);
			double totalCapacity = 2396.174;
			assertEquals(totalCapacity, matrix.getTotalCapacity(),.001);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testLoadMatrixEuropean () {
		String filename = "EuropeanNetwork_matrix.csv";
		AbstractGrafo graph = new EuropeanNetwork();
		try {
			LoadTrafficMatrix load = new LoadTrafficMatrix(graph);
			TrafficMatrix matrix = load.load(filename);
			System.out.println("European Network Traffic Matrix:");
			double total = 0.0;
			for (Entry<String ,Double> e :  matrix.getMatrixSimple().entrySet()) {
				System.out.println(e.getKey() + " = " + e.getValue());
				total+=e.getValue();
			}
			double v = total/2;
			DecimalFormat formatter = new DecimalFormat("#,##0.00");
			System.out.println("Total Traffic Volume V: " + formatter.format(v) + " Gbit/s");
			int n = graph.getNos().tamanho();
			double avgPerNode = 2*v/n;
			System.out.println("avg. traffic per node: " + formatter.format(avgPerNode) + " Gbit/s");
			double avgPerNodePair = 2*v/(n*(n-1));
			System.out.println("avg. traffic per node Pair: " + formatter.format(avgPerNodePair) + " Gbit/s");
//			double totalTrafficLoad =

			assertEquals(2.029,4, v);
			@SuppressWarnings("unused")
			double totalCapacity = 2396.174;
//			assertEquals(totalCapacity, matrix.getTotalCapacity(),.001);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
