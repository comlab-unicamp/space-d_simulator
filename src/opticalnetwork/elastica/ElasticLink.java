/**
 *
 */
package opticalnetwork.elastica;

import graph.No;
import opticalnetwork.EnlaceOptico;

import java.util.Calendar;

/**
 * Created in 24/08/2015
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class ElasticLink extends EnlaceOptico {

	private boolean ActivedMMF = false;
	private Calendar installedMMFDate = Calendar.getInstance();
//	private int distance;
	private int numberOfModes =0;
	private int bandwidth = 0;
	private int numberOfFibersSMFInstalled = 0;
	private int numberOFCablesSMFInstalled = 0;
	private int numberOfFibersMMFInstalled = 0;
	private int numberOFCablesMMFInstalled = 0;
	private boolean smfFiberFull;
	private boolean mmfFiberFull;

	/**
	 * The constructor to instantiate a new object
	 * @param source a {@link No} object as source node
	 * @param destination a {@link No} object as destination node
	 *
	 */
	public ElasticLink(No source, No destination) {
		super(source,destination);

	}

	/**
	 * The constructor to instantiate a new object
	 */
	public ElasticLink(No source, No destination, int distance) {
		super(source,destination);
		setDistancia(distance);

	}

	public void installSingleModeFiber(){
		numberOFCablesSMFInstalled++;
	}

	public void installMultiModeFiber(){
		numberOFCablesMMFInstalled++;
	}

	/**
	 * Install a fiber cable with a number of fibers inside;
	 * The type is Multimode Fiber ou Single Mode Fiber (see {@link LinkType})
	 * @param type a {@link LinkType} object
	 * @param numberOfFibers the number of fibers that a cable contains.
	 * */
	public void installCable (LinkType type, int numberOfFibers) {
		if (type.equals(LinkType.MMF_LINK)) {
			numberOFCablesMMFInstalled++;
			numberOfFibersMMFInstalled += numberOfFibers;
			setActivedMMF(true);
		} else {
			numberOFCablesSMFInstalled++;
			numberOfFibersSMFInstalled += numberOfFibers;
		}
	}

	/**
	 * @return the activedMMF
	 */
	public boolean isActivedMMF() {
		return ActivedMMF;
	}

	/**
	 * @param activedMMF the activedMMF to set
	 */
	public void setActivedMMF(boolean activedMMF) {
		ActivedMMF = activedMMF;
	}

	/**
	 * @return the installedMMFDate
	 */
	public Calendar getInstalledMMFDate() {
		return installedMMFDate;
	}

	/**
	 * @param installedMMFDate the installedMMFDate to set
	 */
	public void setInstalledMMFDate(Calendar installedMMFDate) {
		this.installedMMFDate = installedMMFDate;
	}

	/**
	 * @return the numberOfModes
	 */
	public int getNumberOfModes() {
		return numberOfModes;
	}

	/**
	 * @param numberOfModes the numberOfModes to set
	 */
	public void setNumberOfModes(int numberOfModes) {
		this.numberOfModes = numberOfModes;
	}

	/**
	 * @return the bandwidth
	 */
	public int getBandwidth() {
		return bandwidth;
	}

	/**
	 * @param bandwidth the bandwidth to set
	 */
	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	/**
	 * @return the numberOfFibersSMFInstalled
	 */
	public int getNumberOfFibersSMFInstalled() {
		return numberOfFibersSMFInstalled;
	}

	/**
	 * @param numberOfFibersSMFInstalled the numberOfFibersSMFInstalled to set
	 */
	public void setNumberOfFibersSMFInstalled(int numberOfFibersSMFInstalled) {
		this.numberOfFibersSMFInstalled = numberOfFibersSMFInstalled;
	}

	/**
	 * @return the numberOFCablesSMFInstalled
	 */
	public int getNumberOFCablesSMFInstalled() {
		return numberOFCablesSMFInstalled;
	}

	/**
	 * @param numberOFCablesSMFInstalled the numberOFCablesSMFInstalled to set
	 */
	public void setNumberOFCablesSMFInstalled(int numberOFCablesSMFInstalled) {
		this.numberOFCablesSMFInstalled = numberOFCablesSMFInstalled;
	}

	/**
	 * @return the numberOfFibersMMFInstalled
	 */
	public int getNumberOfFibersMMFInstalled() {
		return numberOfFibersMMFInstalled;
	}

	/**
	 * @param numberOfFibersMMFInstalled the numberOfFibersMMFInstalled to set
	 */
	public void setNumberOfFibersMMFInstalled(int numberOfFibersMMFInstalled) {
		this.numberOfFibersMMFInstalled = numberOfFibersMMFInstalled;
	}

	/**
	 * @return the numberOFCablesMMFInstalled
	 */
	public int getNumberOFCablesMMFInstalled() {
		return numberOFCablesMMFInstalled;
	}

	/**
	 * @param numberOFCablesMMFInstalled the numberOFCablesMMFInstalled to set
	 */
	public void setNumberOFCablesMMFInstalled(int numberOFCablesMMFInstalled) {
		this.numberOFCablesMMFInstalled = numberOFCablesMMFInstalled;
	}

	/**
	 * @return the smfFiberFull
	 */
	public boolean isSmfFiberFull() {
		return smfFiberFull;
	}

	/**
	 * @param smfFiberFull the smfFiberFull to set
	 */
	public void setSmfFiberFull(boolean smfFiberFull) {
		this.smfFiberFull = smfFiberFull;
	}

	/**
	 * @return the mmfFiberFull
	 */
	public boolean isMmfFiberFull() {
		return mmfFiberFull;
	}

	/**
	 * @param mmfFiberFull the mmfFiberFull to set
	 */
	public void setMmfFiberFull(boolean mmfFiberFull) {
		this.mmfFiberFull = mmfFiberFull;
	}

	@Override
	public String toString(){
		String separator = System.getProperty("line.separator");
		StringBuilder builder = new StringBuilder();
		builder.append(getId()).append(": ").append(separator);
		builder.append("Number of SMF Cables: ").append(numberOFCablesSMFInstalled).append(";").append(separator);
		builder.append("Number of MMF Cables: ").append(numberOFCablesMMFInstalled).append(";").append(separator);
		builder.append("Number of SMF: ").append(numberOfFibersSMFInstalled).append(";").append(separator);
		builder.append("Number of MMF: ").append(numberOfFibersMMFInstalled).append(".").append(separator);


		return null;
	}



}
