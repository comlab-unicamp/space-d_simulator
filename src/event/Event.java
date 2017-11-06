package event;
import java.io.Serializable;

/*
 * criado em 09/09/2008
 * updated on July 06, 2017
 */

/**
 * @author Alaelson Jatobá
 * @version 2
 */
public class Event implements Serializable, Comparable<Event> {
	private static final long serialVersionUID = 1L;

	/** the arrival time.*/
	protected double time;

	/** the event type */
	protected Type type;

	/**
	 * Event types.
	 */
	public enum Type{
		/**packet forwarding */
		PACKET_FORWARDING,
		/** new request*/
		NEW_REQUEST,
		/** RSA resquest*/
		REQUEST_RSA,
		/**dynamic simulation*/
		DYNAMIC,
		/**incremental simulation*/
		INCREMENTAL,
		/**incremental simulation with changing switching type*/
		IS_TO_FULLSS
	}

	/** the event content.*/
	protected Object content;


	/** the event constructor*/
	public Event(double time, Type type, Object content) {
		this.time = time;
		this.type = type;
		this.content = content;
	}

	/**
	 * Compare two events by the time.
	 */
	@Override
	public int compareTo(Event event) {
		if (event.getTime() < this.time)
			return 1;
		else if(event.getTime()> this.time)
			return -1;
		else return 0;
	}

	/**
	 * Returns the event time
	 * @return
	 */
	public double getTime(){
		return this.time;
	}

	/**
	 * Returns the event type
	 * @return the event type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the event type
	 * @param type the event type to set
	 */
	public void setTipo(Type type) {
		this.type = type;
	}

	/**
	 * Returns the event content.
	 * @return the content object.
	 */
	public Object getConteudo() {
		return content;
	}

	/**
	 * Sets the event content.
	 * @param the content object to set.
	 */
	public void setConteudo(Object conteudo) {
		this.content = conteudo;
	}

	/**
	 * Sets the event time
	 * @param time the event time to set.
	 */
	public void setTempo(double time) {
		this.time = time;
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("| EVENT, Type: ");
		builder.append(type.toString());
		builder.append(", arrival time: ");
		builder.append(time);
		builder.append("| \n\t{ Content: ");
		builder.append(content.toString());
		return builder.toString();
	}

	@Override
	public Event clone(){
		Event novoEvento = new Event(this.time, this.type, this.content);
		return novoEvento;
	}
}
