/**
 *
 */
package simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created in 05/07/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class Results {
	
	private List<DataSet> dataSet;
	
	public Results () {
		setDataSet(new ArrayList<DataSet>());
	}

	/**
	 * @return the dataSet
	 */
	public List<DataSet> getDataSet() {
		return dataSet;
	}

	/**
	 * @param dataSet the dataSet to set
	 */
	public void setDataSet(List<DataSet> dataSet) {
		this.dataSet = dataSet;
	}
	
	public void insert (DataSet data)  {
		this.dataSet.add(data);
	}


}
