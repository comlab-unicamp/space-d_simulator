/*
 * criado em 09/09/2008 
 */
package event;

import event.Event.Type;

/**
 * Determines the event type to be used by the event generator.
 * @author Alaelson Jatobá
 * @version 2
 * Updated on July 06, 2017
 */
public interface SourceGenerator {
	
	/**
	 * Returns the event content generated by the source 
	 * @return
	 */
	public Object getContent();
	
	/**
	 * Retorna o tipo relativo ao evento no gerador.
	 * @return
	 */
	public Type getEventType();
	
	/**
	 * Set the event type
	 * @param eventType the type of event
	 */
	public void setEventType(Type eventType);

}