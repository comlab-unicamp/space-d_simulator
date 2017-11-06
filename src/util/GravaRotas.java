package util;

import graph.Caminho;
import graph.Grafo;
import graph.No;
import topology.NTTNet;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

public class GravaRotas {
	
	public static void main(String[] args){
		Grafo grafo = new NTTNet();
		try {
			FileOutputStream outFile = new FileOutputStream("rotasNTTNet.txt");
			PrintWriter writer = new PrintWriter(outFile);
			
			Iterator<No> it1 = grafo.getNos().getIterator();
			Caminho c = null;
			while(it1.hasNext()){
				No n= it1.next();
				Iterator<No> it2 = grafo.getNos().getIterator();
				while(it2.hasNext()){
					No n2 = it2.next();
					if(!n.equals(n2)){
						c = n.rotear(n2);
						if(c != null){
							writer.print("[");
							Iterator<No> it3 = c.getNos().getIterator();
							while(it3.hasNext()){
								No nc = it3.next();
								writer.print(nc);
								if(it3.hasNext())
									writer.print("-");
							}
							writer.println("]");
						}
					}
				}
				writer.println();
			}
			
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
