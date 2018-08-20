package com.acme.video.data;

import com.acme.video.Configuration;

public interface DatasetRetriever {
	
	/**
	 * This method retrieves the IMDB datasets from AWS S3 folder and saves them in the local file system for processing.
	 * The datasets are retrieved based on the buckeName and key passed to it.
	 * 
	 * The datsets are stored in localfile system based on file locations available in {@link Configuration} 
	 * 
	 * @param bucketName
	 * @param key
	 * @throws Exception
	 */
	public void getDatasets(String bucketName, String key) throws Exception;
	
}
