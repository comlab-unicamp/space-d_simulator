package distribution;

import random.MersenneTwister;

/**
 * @author Alaelson
 * @version 1.0
 */
public class Poisson implements Distribution {
	/**
	 * Creates random numbers.
	 */
	protected MersenneTwister random;

	/**
	 * Time between arrivals rate.
	 */
	protected double taxaEntreChegadas;

	/**
	 * holding time rate
	 */
	protected double holdingTimeRate;
	
	/**
	 * Load in Erlangs
	 */
	protected double load;

	/**
	 * Creates a instance of class Distribuicao providing Poison distribution functionalities
	 * @param carga the load.
	 * @param duracao the holding time in s.
	 */
	public Poisson (double carga, double duracao){
		this.load = carga;
		this.taxaEntreChegadas = carga/duracao;
		this.holdingTimeRate = 1/duracao;
		this.random = new MersenneTwister();

	}
	
	/**
	 * Creates a instance of class Distribuicao providing Poison distribution functionalities
	 * @param seed the seed for random number generator 
	 * @param load the network load in Erlangs.
	 * @param holdingTime the holding time to serve the event.
	 */
	public Poisson (long seed, double load, double holdingTime){
		this.load = load;
		this.taxaEntreChegadas = load/holdingTime;
		this.holdingTimeRate = 1/holdingTime;
		this.random = new MersenneTwister(seed);

	}


	/**
	 * Creates a instance of class Distribuicao providing Poison distribution functionalities
	 * @param chegada between arrivals rate.
	 * @param servico holding time rate.
	 * @param seed the seed.
	 */
	public Poisson (double chegada, double servico, long seed){
		this.taxaEntreChegadas = chegada;
		this.holdingTimeRate = servico;
		this.random = new MersenneTwister(seed);
	}

	/** Time between arrivals
	 * @see distribution.Distribution#getTimeBetweenArrivals()
	 */
	@Override
	public double getTimeBetweenArrivals() {
		return -(Math.log(1.0-random.nextDouble())/this.taxaEntreChegadas);
	}

	/** Holding time
	 * @see distribuicao.Distribuicao#getTempoServi�o()
	 */
	@Override
	public double getHoldingTime() {
		return - (Math.log(1.0-random.nextDouble())/this.holdingTimeRate);
	}

	public double getTempoMedioEntreChegadas(){
		return 1.0/taxaEntreChegadas;
	}

	public double getTempoMedioDeServico(){
		return 1.0/holdingTimeRate;
	}

	public double getCarga(){
		return load;
	}


	/* (non-Javadoc)
	 * @see distribuicao.Distribution#setArrivalsRate()
	 */
	@Override
	public void setArrivalsRate(double lambda) {
		this.taxaEntreChegadas = lambda;
		
	}

}
