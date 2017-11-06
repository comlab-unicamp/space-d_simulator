/**
 *
 */
package opticalnetwork.elastica.rsa;

import opticalnetwork.controlplane.ExcecaoControle;

/**
 * Created in 04/07/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class RequestException extends ExcecaoControle{

	/**
	 * @param mensagem
	 */
	public RequestException(String mensagem) {
		super(mensagem);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
