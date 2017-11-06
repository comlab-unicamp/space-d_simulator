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
public class DataBase {
	
	int id;
	ArrayList<JobDataBase> jobs;
	String header;
	
	/**
	 * 
	 */
	public DataBase() {
		this.jobs = new ArrayList<JobDataBase>();
	}
	
	/**
	 * Adds a {@link JobDataBase} object into the list's data
	 * @param job
	 */
	public void addJob (JobDataBase job) {
		this.jobs.add(job);
	}
	
	/**
	 * Return a JobDataBase object into the list by its id as parameter. May return a null value
	 * @param jobId
	 * @return 
	 */
	public JobDataBase getJob (int jobId) {
		for (JobDataBase job : jobs) {
			if (job.getId() == jobId) {
				return job;
			}
		}
		return null;
	}
	
	/**
	 * Removes a JobDataBase object into the list by its id as parameter. 
	 * @param jobId
	 */
	public void removeJob (int jobId) {
		for (JobDataBase job : jobs) {
			if (job.getId() == jobId) {
				this.jobs.remove(job);
			}
		}
		
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
	 * @return the jobs
	 */
	public ArrayList<JobDataBase> getJobs() {
		return jobs;
	}

	/**
	 * @param jobs the jobs to set
	 */
	public void setJobs(ArrayList<JobDataBase> jobs) {
		this.jobs = jobs;
	}
	
	public String toString (){
		StringBuilder sb = new StringBuilder();
		sb.append(getHeader());
		for (JobDataBase job : this.jobs) {
			sb.append(job.toString()).append("\n");
		}
		return sb.toString();

	}
	
	/**
	 * Returns the String Header
	 * @return the header
	 */
	public String getHeader(){
		return this.header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

}
