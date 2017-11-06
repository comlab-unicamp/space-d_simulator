/*
 * criado em 09/09/2008 
 */
package event;

import java.io.Serializable;

import distribution.Distribution;

/**
 * Creates events with exponentially distributed times
 * @author Alaelson
 * @version 1.0
 * updated on July 06, 2017
 */

public class EventGenerator implements Serializable, Comparable<EventGenerator>{
	private static final long serialVersionUID = 1L;
	/**
	 * start time
	 */
	protected double startTime;

	/**
	 * Next time to generate a event.
	 */
	protected double nextTime;

	/**
	 * random distribution.
	 */
	protected Distribution distribution;

	/**
	 * Generator's source.
	 */
	protected SourceGenerator generator;

	/**
	 * Constructor. Creates events by which times are sorted by specified distribution
	 * @param distribution
	 */
	public EventGenerator(Distribution distribution) {
		this(distribution,0.0);
	}

	/**
	 *  Constructor. Creates events by which times are sorted by specified distribution, starting in the specified time
	 * @param distribution
	 * @param startTime
	 */
	public EventGenerator(Distribution distribution, double startTime) {
		this.distribution = distribution;
		this.startTime = startTime;
		this.nextTime = startTime;
	}

	/**
	 * Compares to generators by its next time
	 */
	@Override
	public int compareTo(EventGenerator generator) {
		if (generator.getNextTime() < this.getNextTime())
			return 1;
		else if (generator.getNextTime() > this.getNextTime())
			return -1;
		else return 0;	
	}

	/**
	 * @return the distribution
	 */
	public Distribution getDistribution() {
		return distribution;
	}

	/**
	 * @param distribution the distribution to set
	 */
	public void setDistribuicao(Distribution distribution) {
		this.distribution = distribution;
	}

	/**
	 * @return the source generator
	 */
	public SourceGenerator getGenerator() {
		return generator;
	}

	/**
	 * @param generator the source generator to set
	 */
	public void setFonte(SourceGenerator generator) {
		this.generator = generator;
	}

	/**
	 * Returns the time between arrival times
	 * @return 
	 */
	public double getTimeBetweenArrivals(){
		return distribution.getTimeBetweenArrivals();
	}

	/**
	 * Returns the start time
	 * @return the start time
	 */
	public double getStartTime() {
		return startTime;
	}

	/**
	 * Set the start tipe
	 * @param startTime the start time to set
	 */
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}


	/**
	 * Returns the next time
	 * @return the next time
	 */
	public double getNextTime() {
		return nextTime;
	}

	/**
	 * Sets the next time of the new event
	 * @param nextTime the next time to create a new event.
	 */
	public void setNextTime(double nextTime) {
		this.nextTime = nextTime;
	}

	
	/**
	 * Creates a new event
	 * @param time the event time
	 * @return the created event
	 */
	public Event create(double time){
		return new Event(time, generator.getEventType(), generator.getContent());	
	}
	
	/**
	 * Creates a new event
	 * @param time the event time
	 * @param generator uses a specified generator to get the content 
	 * @return the created event
	 */
	public Event create(double time, SourceGenerator generator){
		return new Event(time, generator.getEventType(), generator.getContent());	
	}
	
	/**
	 * Creates a new event
	 * @param time the event time
	 * @param content the event content 
	 * @return the created event
	 */
	public Event create(double time, Object content){
		return new Event(time, generator.getEventType(), generator.getContent());	
	}
	
	/**
	 * Creates a new event
	 * @param time the event time
	 * @param type the event type 
	 * @param content the event content 
	 * @return the created event
	 */
	public Event create(double time, Event.Type type, Object content){
		return new Event(time, type, content);	
	}
}
