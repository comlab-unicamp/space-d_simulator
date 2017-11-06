/**
 *
 */
package stats;

import java.util.ArrayList;

/**
 * Created in 08/09/2016
 * By @author Alaelson Jatoba
 * @version 1.0
 */
public class JobDataBase {
	

	private int id;
	private ArrayList<Record> records;
	
	
	public JobDataBase() {
		records = new ArrayList<Record>();
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the records
	 */
	public ArrayList<Record> getRecords() {
		return records;
	}

	/**
	 * @param records the records to set
	 */
	public void setRecords(ArrayList<Record> records) {
		this.records = records;
	}
	
	/**
	 * Adds a {@link Record} object into the list's data
	 * @param record
	 */
	public void addRecod (Record record) {
		this.records.add(record);
	}
	
	/**
	 * Return a Record object into the list by its id as parameter. May return a null value
	 * @param id
	 * @return 
	 */
	public Record getRecord (int id) {
		for (Record rec : records) {
			if (rec.getId() == id) {
				return rec;
			}
		}
		return null;
	}
	
	/**
	 * Removes a Record object into the list by its id as parameter. 
	 * @param id
	 */
	public void removeRecord (int id) {
		for (Record rec : records) {
			if (rec.getId() == id) {
				this.records.remove(rec);
			}
		}
		
	}
	
	
	
	
	/**
	 * Returns a comma separated string with the records
	 * @return a string
	 */
	public String toString (){
		StringBuilder sb = new StringBuilder();
		for (Record rec : this.records) {
			sb.append(rec.toString()).append("\n");
		}
		return sb.toString();

	}

}
