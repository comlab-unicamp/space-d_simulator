package event;
import java.io.IOException;
import java.io.Serializable;
import java.util.PriorityQueue;

import graph.ExcecaoGrafo;

/*
 * criado em 09/09/2008 
 */

/**
 * Manages the event-driven simulation
 * 
 * @author Alaelson
 * @version 1.0
 */
public class Scheduler implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * the event list.
	 */
	protected EventList eventList;
	
	
	/**
	 * Returns the event list
	 * @return the event list
	 */
	public EventList getEventList() {
		return eventList;
	}
	
	/**
	 * Lista de geradores de eventos para o escalonador.
	 */
	protected PriorityQueue<EventGenerator> generatorList;
	
	/**
	 * Cria uma inst�ncia do escalonador.
	 * @throws IOException 
	 */
	public Scheduler() {
		eventList = new EventList();
		generatorList = new PriorityQueue<EventGenerator>();
		
	}
	
	/**
	 * Inserts an event in the list
	 * @param event evento a ser inserido.
	 */
	public void insertEvent(Event event) throws ExcecaoGrafo{
		eventList.insert(event); 
	}
	
	/**
	 * Inserts all elements in the list in the scheduler list
	 * @param list a list with events to be set
	 */
	public void insertEvent(EventList list) throws ExcecaoGrafo{
		while(list.size()!=0) {
			insertEvent(list.poll()) ;
		}
	}
	/**
	 * Inserts a geneartor in the list of generators
	 * @param generator to be set
	 */
	public void insertGenerator(EventGenerator generator){
		generatorList.add(generator);
	}
	/**
	 * Executes and schedule the event in the list
	 * @return
	 * @throws IOException 
	 */
	public Event exec() {
		double generatorTime, schedulerTime;
		if (generatorList.size() > 0)
			generatorTime = generatorList.peek().getNextTime();
		else
			generatorTime = Double.MAX_VALUE;
		if (eventList.size() > 0)
			schedulerTime =  eventList.peek().getTime();
		else
			schedulerTime = Double.MAX_VALUE;
		if (schedulerTime < generatorTime)
			return eventList.poll();
		else {
			EventGenerator generator = generatorList.poll();
			Event evento = generator.create(generatorTime);
			generator.setNextTime(generatorTime + generator.getTimeBetweenArrivals());
			generatorList.add(generator);
			
			return evento;
		}
	}

	
}
