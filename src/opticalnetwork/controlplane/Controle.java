package opticalnetwork.controlplane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import algorithm.KShortestPathFirstInterface;
import algorithm.KShortestPathList;
import algorithm.YenTopKShortestBasedAU;
import event.Event;
import event.EventList;
import graph.AbstractGrafo;
import graph.Caminho;
import graph.Enlace;
import graph.ExcecaoGrafo;
import graph.Grafo;
import graph.No;

public abstract class Controle {
	private  AbstractGrafo grafo;
	protected AbstractGrafo overlaySDMGraph;
	protected AbstractGrafo overlayWDMGraph;

	protected int numbloqueiosLabelSetVazio = 0;
	protected int numbloqueiosLambdaIndisponivel = 0;
	private int numCaminhosOpticosEstabelecidos = 0;
	private int numCaminhosOpticosFechados = 0;
	private int numCaminhosOpticosBloqueados = 0;
	private int numRequisicoes=0;
	private int thresholdNumDimensions=1;

	private double tempoEntreChegadasAnterior = 0.0;
	private double tempoDurecaoAnterior = 0.0;
	private double cargaDaRede = 0.0;
	private double tempoMedioEntreChegadas = 0.0;
	private double tempoMedioDuracoes = 0.0;

	private int limiteDeRequisicoes;
	private int numLambdas;
	protected int steadyState; 
	//	protected  final ListaRotulos<Rotulo> listaRotulos;

	private HashMap<String,Boolean[]> tabelaDeEstados;
	private HashMap<String, Caminho> tabelaDeRotas;
//	private HashMap<Integer, CaminhoOptico> tabelaDeCaminhosOpticos;
	private Boolean[] mask;
	private Double[] entreChegadas;
	private Double[] duracoes;
	private ArrayList<Double> interArrivals;
	private ArrayList<Double> holdinTimes;
	protected EventList listaEventos ;
	private boolean usarRotasExplicitas;
	private KShortestPathFirstInterface kSPF;
	private ArrayList<Double> throughput;
	private HashMap<Integer, Caminho> requestPathTable;
	protected int inputNetworkLoad = 0;
	private boolean bidirectional = false;
	private int minimumSpectralSlotsPerCarrier = 4;
	private boolean debug = false;
	private boolean isIncremental = false;
	
	
	
	public Controle(int numLambdas, int limiteDeRequisicoes){

		this.numCaminhosOpticosEstabelecidos = 0;
		this.numCaminhosOpticosBloqueados = 0;
		this.tabelaDeEstados = new HashMap<String, Boolean[]>();
		this.tabelaDeRotas = new HashMap<String, Caminho>();
//		tabelaDeCaminhosOpticos = new HashMap<Integer, CaminhoOptico>();
		this.numLambdas = numLambdas;
		this.mask = new Boolean[numLambdas];
		this.listaEventos = new EventList();
		this.limiteDeRequisicoes = limiteDeRequisicoes;
		this.entreChegadas = new Double[limiteDeRequisicoes];
		this.duracoes = new Double[limiteDeRequisicoes];
		this.interArrivals = new ArrayList<Double>();
		this.holdinTimes = new ArrayList<Double>();
		this.throughput = new ArrayList<Double>();
		this.requestPathTable = new HashMap<>();
		
		this.criaMascara();
	}
	
	
	/**
	 * The Constructor
	 * @param numLambdas the number of slots
	 * @param limiteDeRequisicoes the request limit
	 * @param graph the network 
	 */
	public Controle(int numLambdas, int limiteDeRequisicoes, Grafo graph){
		this(numLambdas, limiteDeRequisicoes);
		this.grafo = graph;
		this.overlayWDMGraph = graph;
		this.overlayWDMGraph.setName("Overlay WDM");
		this.overlaySDMGraph = graph.clone();
		this.overlaySDMGraph.setName("Overlay SDM");
		this.kSPF = new YenTopKShortestBasedAU(graph);
	}

	public void setPadroesDeTrafego(double tempoEvento, double duracaoDaRequisicao){

		Double tempo = (tempoEvento - tempoEntreChegadasAnterior);
		tempoMedioEntreChegadas += tempo;
		tempoEntreChegadasAnterior = tempoEvento;
		tempoMedioDuracoes += duracaoDaRequisicao;
		cargaDaRede = (1/tempoMedioEntreChegadas)/(1/tempoMedioDuracoes); //used in old simulation in 2010.
//		cargaDaRede = tempoMedioEntreChegadas/tempoMedioDuracoes; //changed in Sep. 24, 2015
//		entreChegadas[numRequisicoes] = tempo;
		interArrivals.add(tempo);
		holdinTimes.add(tempo);
//		duracoes[numRequisicoes] = duracaoDaRequisicao;

		numRequisicoes++;
	}

	@Deprecated
	public void mostrarPadraoDeTrafego(){
		double media1 = 0.0;
		for (int i = 0 ; i < entreChegadas.length ; i++){
			media1 += entreChegadas[i];
		}

		media1 = media1/getLimiteDeRequisicoes();

		double media2 = 0.0;
		for (int i = 0 ; i < duracoes.length ; i++){
			media2 += duracoes[i];
		}

		media2 = media2/getLimiteDeRequisicoes();

		double c = (1/media1)/(1/media2);

		System.out.println(media2 + "\t" + media1 + "\t" + c);

	}

	public abstract EventList receberEvento(Event evento) throws Exception;


	void iniciaTablelaDeEstados(){
		for(Enlace e : grafo.getEnlaces().valores()){
			tabelaDeEstados.put(e.getId(), getMascara());
		}
	}

	/**
	 * Creates a spectrum mask with all slots setup to <code>true</code>, which represents the empty slot
	 */
	protected void criaMascara(){
		for(int i = 0 ; i < mask.length ; i++){
			mask[i] = true; //inicia com o valor "false"
		}
	}

	/**
	 * Gets a clone copy of the mask spectrum
	 * @return the clone of the mask spectrum
	 */
	public Boolean[] getMascara(){
		return this.mask.clone();
	}

	public double probabilidadeBloqueio(){
		return (double)this.numCaminhosOpticosBloqueados/(this.limiteDeRequisicoes-steadyState);
	}

	public void setTabelaDeRotas(HashMap<String,Caminho> tabelaDeRotas){
		this.tabelaDeRotas = tabelaDeRotas;
	}

//	public CaminhoOptico getCaminho(Integer key){
//		return tabelaDeCaminhosOpticos.get(key);
//	}

	public Caminho getRota(String key){
		return tabelaDeRotas.get(key);
	}

	public double getTempoMedioDuracoes() {
		return tempoMedioDuracoes/numRequisicoes;
	}

	public double getCarga() {
		return cargaDaRede;
	}

	public void setCarga(double carga){
		this.cargaDaRede = carga;
	}

	public double getTempoMedioEntreChegadas() {
		return tempoMedioEntreChegadas/numRequisicoes;
	}


	public void mostrarEstabelecidosFinalizados() {
		System.out.println("Estabelecidos: "+ numCaminhosOpticosEstabelecidos + ", Finalizados: " + numCaminhosOpticosFechados);
	}

	public void mostrarEstado(Boolean[] estado){
		System.out.println(Arrays.toString(estado));
	}

	/**
	 * @return the usarRotasExplicitas
	 */
	public boolean isUsarRotasExplicitas() {
		return usarRotasExplicitas;
	}

	/**
	 * @param usarRotasExplicitas the usarRotasExplicitas to set
	 */
	public void setUsarRotasExplicitas(boolean usarRotasExplicitas) {
		this.usarRotasExplicitas = usarRotasExplicitas;
	}

	/**
	 * @return the numbloqueiosLabelSetVazio
	 */
	public int getNumbloqueiosLabelSetVazio() {
		return numbloqueiosLabelSetVazio;
	}

	/**
	 * @param numbloqueiosLabelSetVazio the numbloqueiosLabelSetVazio to set
	 */
	public void setNumbloqueiosLabelSetVazio(int numbloqueiosLabelSetVazio) {
		this.numbloqueiosLabelSetVazio = numbloqueiosLabelSetVazio;
	}

	/**
	 * @return the numbloqueiosLambdaIndisponivel
	 */
	public int getNumbloqueiosLambdaIndisponivel() {
		return numbloqueiosLambdaIndisponivel;
	}

	/**
	 * @param numbloqueiosLambdaIndisponivel the numbloqueiosLambdaIndisponivel to set
	 */
	public void setNumbloqueiosLambdaIndisponivel(
			int numbloqueiosLambdaIndisponivel) {
		this.numbloqueiosLambdaIndisponivel = numbloqueiosLambdaIndisponivel;
	}

	/**
	 * @return the numCaminhosOpticosEstabelecidos
	 */
	public int getNumCaminhosOpticosEstabelecidos() {
		return numCaminhosOpticosEstabelecidos;
	}

	/**
	 * @param numCaminhosOpticosEstabelecidos the numCaminhosOpticosEstabelecidos to set
	 */
	public void setNumCaminhosOpticosEstabelecidos(
			int numCaminhosOpticosEstabelecidos) {
		this.numCaminhosOpticosEstabelecidos = numCaminhosOpticosEstabelecidos;
	}

	/**
	 * @return the numCaminhosOpticosFechados
	 */
	public int getNumCaminhosOpticosFechados() {
		return numCaminhosOpticosFechados;
	}

	/**
	 * @param numCaminhosOpticosFechados the numCaminhosOpticosFechados to set
	 */
	public void setNumCaminhosOpticosFechados(int numCaminhosOpticosFechados) {
		this.numCaminhosOpticosFechados = numCaminhosOpticosFechados;
	}

	/**
	 * @return the numCaminhosOpticosBloqueados
	 */
	public int getNumCaminhosOpticosBloqueados() {
		return numCaminhosOpticosBloqueados;
	}

	/**
	 * @param numCaminhosOpticosBloqueados the numCaminhosOpticosBloqueados to set
	 */
	public void setNumCaminhosOpticosBloqueados(
			int numCaminhosOpticosBloqueados) {
		this.numCaminhosOpticosBloqueados = numCaminhosOpticosBloqueados;
	}

	/**
	 * @return the numRequisicoes
	 */
	public int getNumRequisicoes() {
		return numRequisicoes;
	}

	/**
	 * @param numRequisicoes the numRequisicoes to set
	 */
	public void setNumRequisicoes(int numRequisicoes) {
		this.numRequisicoes = numRequisicoes;
	}

	/**
	 * @return the tempoEntreChegadasAnterior
	 */
	public double getTempoEntreChegadasAnterior() {
		return tempoEntreChegadasAnterior;
	}

	/**
	 * @param tempoEntreChegadasAnterior the tempoEntreChegadasAnterior to set
	 */
	public void setTempoEntreChegadasAnterior(
			double tempoEntreChegadasAnterior) {
		this.tempoEntreChegadasAnterior = tempoEntreChegadasAnterior;
	}

	/**
	 * @return the tempoDurecaoAnterior
	 */
	public double getTempoDurecaoAnterior() {
		return tempoDurecaoAnterior;
	}

	/**
	 * @param tempoDurecaoAnterior the tempoDurecaoAnterior to set
	 */
	public void setTempoDurecaoAnterior(double tempoDurecaoAnterior) {
		this.tempoDurecaoAnterior = tempoDurecaoAnterior;
	}

	/**
	 * @return the limiteDeRequisicoes
	 */
	public int getLimiteDeRequisicoes() {
		return limiteDeRequisicoes;
	}

	/**
	 * @param limiteDeRequisicoes the limiteDeRequisicoes to set
	 */
	public void setLimiteDeRequisicoes(int limiteDeRequisicoes) {
		this.limiteDeRequisicoes = limiteDeRequisicoes;
	}

	/**
	 * @return the numLambdas
	 */
	public int getNumLambdas() {
		return numLambdas;
	}

	/**
	 * @param numLambdas the numLambdas to set
	 */
	public void setNumLambdas(int numLambdas) {
		this.numLambdas = numLambdas;
	}

	/**
	 * @return the tabelaDeEstados
	 */
	public HashMap<String, Boolean[]> getTabelaDeEstados() {
		return tabelaDeEstados;
	}

	/**
	 * @param tabelaDeEstados the tabelaDeEstados to set
	 */
	public void setTabelaDeEstados(HashMap<String, Boolean[]> tabelaDeEstados) {
		this.tabelaDeEstados = tabelaDeEstados;
	}

	/**
	 * @return the tabelaDeRotas
	 */
	public HashMap<String, Caminho> getTabelaDeRotas() {
		return tabelaDeRotas;
	}

	/**
	 * @param tempoMedioEntreChegadas the tempoMedioEntreChegadas to set
	 */
	public void setTempoMedioEntreChegadas(double tempoMedioEntreChegadas) {
		this.tempoMedioEntreChegadas = tempoMedioEntreChegadas;
	}

	/**
	 * @param tempoMedioDuracoes the tempoMedioDuracoes to set
	 */
	public void setTempoMedioDuracoes(double tempoMedioDuracoes) {
		this.tempoMedioDuracoes = tempoMedioDuracoes;
	}

	/**
	 * @param mascara the mascara to set
	 */
	public void setMascara(Boolean[] mascara) {
		this.mask = mascara;
	}

	public void incrementaNumRequisicoes(){
		numRequisicoes++;
	}

	public void incrementaBloqueios(){
		numCaminhosOpticosBloqueados++;
	}

	public void incrementaCaminhosEstabelecidos(){
		numCaminhosOpticosEstabelecidos++;
	}

	/**
	 * @return the grafo
	 */
	public AbstractGrafo getGrafo() {
		return grafo;
	}

	/**
	 * @param grafo the grafo to set
	 */
	public void setGrafo(AbstractGrafo grafo) {
		this.grafo = grafo;
	}
	
	/**
	 * Returns the K disjoint shortest paths
	 * @param source the source node
	 * @param destination the destination node
	 * @param k the number of paths requested
	 * @return
	 * @throws ExcecaoGrafo
	 */
	public KShortestPathList getKshortestPathList (No source, No destination, int k) throws ExcecaoGrafo {
		KShortestPathList paths = new KShortestPathList();

		paths = kSPF.getKShortestPaths(this.getGrafo(), source, destination, k);
		return paths;
	}
	
	/**
	 * Returns the K disjoint shortest paths
	 * @param graph the graph to search the paths
	 * @param source the source node
	 * @param destination the destination node
	 * @param k the number of paths requested
	 * @return a list with the paths
	 * @throws ExcecaoGrafo
	 */
	public KShortestPathList getKshortestPathList (Grafo graph, No source, No destination, int k) throws ExcecaoGrafo {
		KShortestPathList paths = new KShortestPathList();

		paths = kSPF.getKShortestPaths(graph, source, destination, k);
		return paths;
	}
	
	/**
	 * @return the throughput
	 */
	public ArrayList<Double> getThroughputInstalled() {
		return throughput;
	}

	/**
	 * @param throughput the throughput to set
	 */
	public void setThroughput(ArrayList<Double> throughput) {
		this.throughput = throughput;
	}
	
	public void incrementInputNetworkLoad (int value) {
		this.inputNetworkLoad+=value;
	}
	
	/**
	 * @return the bidirectional
	 */
	public boolean isBidirectional() {
		return bidirectional;
	}

	/**
	 * @param bidirectional the bidirectional to set
	 */
	public void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}

	/**
	 * @return the requestPathTable
	 */
	public HashMap<Integer, Caminho> getRequestPathTable() {
		return requestPathTable;
	}

	/**
	 * @param requestPathTable the requestPathTable to set
	 */
	public void setRequestPathTable(HashMap<Integer, Caminho> requestPathTable) {
		this.requestPathTable = requestPathTable;
	}

	/**
	 * @return the minimumSpatialSlotsPerCarrier
	 */
	public int getMinimumSpectralSlotsPerCarrier() {
		return minimumSpectralSlotsPerCarrier;
	}

	/**
	 * @param minimumSpectralSlotsPerCarrier the minimumSpatialSlotsPerCarrier to set
	 */
	public void setMinimumSpectralSlotsPerCarrier(int minimumSpectralSlotsPerCarrier) {
		this.minimumSpectralSlotsPerCarrier = minimumSpectralSlotsPerCarrier;
	}
	
	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}


	/**
	 * @return the isIncremental
	 */
	public boolean isIncremental() {
		return isIncremental;
	}


	/**
	 * @param isIncremental the isIncremental to set
	 */
	public void setIncremental(boolean isIncremental) {
		this.isIncremental = isIncremental;
	}
	
	/**
	 * Installs a new dimension in each link of the network
	 * @param dimensionKey the new dimension key
	 * @throws ExcecaoGrafo throws a error if the dimension is already installed.
	 */
	public void upgradeDimensionIntoNetwork (int dimensionKey) throws ExcecaoGrafo{	
		for (Enlace e : getGrafo().getEnlaces().getEnlaces().values()) {
			e.getLinkStateTable().put(dimensionKey, getMascara());
		}
	}
	
	/**
	 * Installs a new spectrum size in each link of the network
	 * @param numSpectralSlots the new spectral slot size
	 * @throws ExcecaoGrafo
	 */
	public void upgradeSpectrumIntoNetwork (int numSpectralSlots) throws ExcecaoGrafo{	
		for (Enlace e : getGrafo().getEnlaces().getEnlaces().values()) {
			int dimensionSize = e.getLinkStateTable().getStateTable().size();
			for (int i = 0 ; i < dimensionSize ; i++) {
				Boolean[] spectrum = e.getLinkStateTable().getStateSpectralArray(i);
				int originalSize = spectrum.length; 
				Boolean[] newSpectrum = Arrays.copyOf(spectrum, numSpectralSlots);
				for (int k = originalSize ; k < numSpectralSlots ; k++ ) {
					if (newSpectrum[k] == null) {
						newSpectrum[k] = true;
					} else {
						throw new ExcecaoGrafo("error increazing the spectrum. It could be overwriting data. The Slot " + k + ", in link "+ e +", isn't empty (null)");
					}
				}
				
				e.getLinkStateTable().setStateSpectralArray(i, newSpectrum);
			}
			
		}
	}


	/**
	 * @return the thresholdNumDimensions
	 */
	public int getThresholdNumDimensions() {
		return thresholdNumDimensions;
	}


	/**
	 * @param thresholdNumDimensions the thresholdNumDimensions to set
	 */
	public void setThresholdNumDimensions(int thresholdNumDimensions) {
		this.thresholdNumDimensions = thresholdNumDimensions;
	}


	public KShortestPathFirstInterface getkSPF() {
		return kSPF;
	}


	public void setkSPF(KShortestPathFirstInterface kSPF) {
		this.kSPF = kSPF;
	}


	
		
		
			
	
	


	
}
