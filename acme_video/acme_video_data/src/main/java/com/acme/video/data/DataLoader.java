package com.acme.video.data;


/**
 * @author amitkhanal
 * This interface will load the various datasets from file system to Mongo DB
 */
public interface DataLoader {

	/**
	 * Performs the dataset load in Mongo DB
	 * 
	 * @throws Exception
	 */
	public void load() throws Exception;
	
}
