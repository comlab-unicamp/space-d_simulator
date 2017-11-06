package graph;



import opticalnetwork.EnlaceOptico;
import opticalnetwork.NoOptico;
import topology.NetworkTopology;

public class Grafo extends AbstractGrafo{

	private static int ID_GRAPH = 0;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Grafo(){
		setId(ID_GRAPH++);
	}

	public Grafo(NetworkTopology network){
		super(network);
		setId(ID_GRAPH++);
	}

	public void setNoOptico(NoOptico noOptico) throws ExcecaoGrafo{
		adicionarNo(noOptico);
	}

	public void setEnlaceOptico(double distancia, NoOptico esq, NoOptico dir) throws ExcecaoGrafo{
		adicionarEnlaceBidirecional(distancia, esq, dir);
	}
	/**
	 * Retorna o  tempo de propagação de um enlace dado o
	 * nó oriem e o nó destino
	 * @param esq
	 * @param dir
	 * */
	public double getTempoPropagacaoEnlace(No esq, No dir){
		return ((EnlaceOptico)getEnlace(esq, dir)).getTempoPropagacao();
	}

	@Override
	public void adicionarEnlace(No esq, No dir) throws ExcecaoGrafo {
		Enlace enlace = new EnlaceOptico(esq,dir);
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
//		Grafo clone = new Grafo(this.getNetwork());
		Grafo clone = (Grafo) NetworkTopology.getGraph(this.getNetwork());

//		try {
//
//			for (No no : this.getNos().valores()) {
//				String className = no.getClass().getName();
//				Class<?> clazz = Class.forName(className);
//				Constructor<?> constructor = clazz.getConstructor(String.class, String.class, AbstractGrafo.class);
//				Object instance = constructor.newInstance(no.getId(), no.getName(),clone);
//				No newNo = (No) instance;
//				clone.adicionarNo(newNo);
//			}
//			for (Enlace e : getEnlaces().valores()) {
//				double distance = e.getDistancia();
//				String leftId = e.getNoEsquerda().getId();
//				No left = clone.getNos().getNo(leftId);
//				String rightId = e.getNoDireita().getId();
//				No right = clone.getNos().getNo(rightId);
//				clone.adicionarEnlace(distance, left, right);
//			}
//
//
//		} catch (ExcecaoGrafo e){
//			e.printStackTrace();
//		} catch( ClassNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (InstantiationException e1) {
//			e1.printStackTrace();
//		} catch (IllegalAccessException e1) {
//			e1.printStackTrace();
//		} catch (IllegalArgumentException e1) {
//			e1.printStackTrace();
//		} catch (InvocationTargetException e1) {
//			e1.printStackTrace();
//		} catch (NoSuchMethodException e1) {
//			e1.printStackTrace();
//		} catch (SecurityException e1) {
//			e1.printStackTrace();
//		}

		return clone;
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
