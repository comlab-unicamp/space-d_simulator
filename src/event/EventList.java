package event;
import java.io.Serializable;
import java.util.PriorityQueue;

import graph.ExcecaoGrafo;

/*
 * criado em 09/09/2008 
 */

/**
 * The Scheduler's event list.
 * 
 * @author Alaelson
 * @version 2.0
 * updated on July 06, 2017
 */
public class EventList implements Serializable {
	private static final long serialVersionUID = 1L;
	/** a priority queue as event list */
	protected PriorityQueue<Event> pqList;
	
	/**
	 * Cria uma inst�ncia de lista de eventos.
	 */
	public EventList (){
		pqList = new PriorityQueue<Event>();
	}
	
	/** 
	 * Inserts a event
	 * @param event the event to be set.
	 */
	public void insert(Event event) throws ExcecaoGrafo{
		if (event != null){ 
			pqList.add(event);
		}
	}
	
	/**
	 * Removes the event on the top of list.
	 * @return event the high priority event.
	 */
	public Event poll(){
		return pqList.poll();
	}
	
	/**
	 * Gets the high priority event without remove it.
	 * @return event the high priority event.
	 */
	public Event peek(){
		return pqList.peek();
	}
	
	/**
	 * Returns the number of elements in the list
	 * @return the number of elements.
	 */
	public int size(){
		return pqList.size();
	}
}
