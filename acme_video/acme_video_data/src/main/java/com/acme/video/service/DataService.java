package com.acme.video.service;

public interface DataService {

	/**
	 * Processes and stores all the data available in the passed filelocation
	 *  
	 * @param fileLocation
	 * @throws Exception
	 */
	public void persistData(String fileLocation) throws Exception;
	
	/**
	 * @return total items stored in repository which this service represents
	 */
	public long totalItems();
	
}
