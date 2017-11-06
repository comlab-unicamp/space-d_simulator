/**
 * Created on 29/09/2015
 */
package opticalnetwork.elastica;

import graph.Enlace;

/**
 * @author Alaelson Jatobá
 * Version 1.0
 */
public class LinkUtilization implements Comparable<LinkUtilization>{

	private Enlace link;
	private Double utilization;

	/**
	 *
	 */
	public LinkUtilization(Enlace link, Double utilization) {
		this.link = link;
		this.utilization = utilization;
	}

	/**
	 * @return the link
	 */
	public Enlace getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(Enlace link) {
		this.link = link;
	}

	/**
	 * @return the utilization
	 */
	public Double getUtilization() {
		return utilization;
	}

	/**
	 * @param utilization the utilization to set
	 */
	public void setUtilization(Double utilization) {
		this.utilization = utilization;
	}

	/**
	 * Compares the link utilization
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LinkUtilization o) {
		if (o.getUtilization() < this.utilization)
			return -1;
		else if(o.getUtilization()> this.utilization)
			return 1;
		else return 0;
	}



}
