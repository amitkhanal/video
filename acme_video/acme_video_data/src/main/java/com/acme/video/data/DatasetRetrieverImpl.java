package com.acme.video.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.acme.video.Configuration;

/**
 * @author amitkhanal
 *
 * This class retrieves movie and it's related datasets from AWS S3 folder. 
 * For information lSee <a href="http://www.imdb.com/interfaces">http://www.imdb.com/interfaces</a>
 * 
 */
@Component
public class DatasetRetrieverImpl implements DatasetRetriever {

	private final static Logger LOGGER = LoggerFactory.getLogger(DatasetRetriever.class);
	
	@Autowired
	private Configuration configuration;
	
	/**
	 * Creates the {@link AmazonS3} object and returns based on the credentials file and profile set in {@link Configuration}
	 * 
	 * @return
	 * 
	 */
	private AmazonS3 getS3Client(){
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider(getConfiguration().
				getCredentialsFile(),getConfiguration().getProfileName());
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(getConfiguration().getRegion()).withCredentials(credentialsProvider).build();
		return s3Client;
	}

	/* (non-Javadoc)
	 * @see com.acme.video.data.DatasetRetriever#getDatasets(java.lang.String, java.lang.String)
	 */
	public void getDatasets(String bucketName, String key) throws IOException, InterruptedException{
		try {
			AmazonS3 s3Client = getS3Client();			
			List<String> datasets = getDatasetList(bucketName, key);
			int count=0;
			for(String dataset : datasets){
				count++;
				//--- using requester pays
				GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, dataset).withRequesterPays(true);
				LOGGER.info("Downloading object from bucker {} and key {}, {} of {}", bucketName, key, count, datasets.size());

				S3Object s3object = s3Client.getObject(getObjectRequest);
				writeFile(s3object.getObjectContent(), dataset);
			}
		} catch (AmazonServiceException ase) {
			logErrorDetails(ase);
		} catch (AmazonClientException ace) {
			LOGGER.debug("Caught an AmazonClientException, which means"+
					" the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			LOGGER.debug("Error Message: " + ace.getMessage());
		}
	}

	/**
	 * This method queries AWS S3 bucket to retrieve the list of objects based on the key passed to it
	 * 
	 * @param bucketName
	 * @param key
	 * @return
	 */
	private List<String> getDatasetList(String bucketName, String key){
		List<String> filteredList = new ArrayList<String>();
		try {
			LOGGER.debug("Listing objects start from bucket {} and key {}");
			final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(key)
					.withRequesterPays(true);

			AmazonS3 s3Client = getS3Client();
			ListObjectsV2Result result;
			
			do {               
				result = s3Client.listObjectsV2(req);
				List<String> filteredObjectSummaries = getFilteredList(result.getObjectSummaries());
				filteredList.addAll(filteredObjectSummaries);
				LOGGER.debug("Next Continuation Token : {} ", result.getNextContinuationToken());
				req.setContinuationToken(result.getNextContinuationToken());
			} while(result.isTruncated() == true ); 
			LOGGER.debug("Listing objects end from bucket {} and key {}");
		} catch (AmazonServiceException ase) {
			logErrorDetails(ase);
		} catch (AmazonClientException ace) {
			LOGGER.error("Caught an AmazonClientException, " +
					"which means the client encountered " +
					"an internal error while trying to communicate" +
					" with S3, " +
					"such as not being able to access the network.",ace);
		}
		return filteredList;
	}

	/**
	 * Filters the incoming collection of {@link S3ObjectSummary} to see if the object needs to be processed or not. 
	 * This method retuns filtered S3 bucket keys of movies, castnames, principals and ratings only. Others are filtered out.
	 * @param objectSummaries
	 * @return
	 */
	private List<String> getFilteredList(List<S3ObjectSummary> objectSummaries){
		if(objectSummaries.size() == 0){
			return null;
		}
		
		List<String> filteredList =
			    objectSummaries
			        .stream()
			        .parallel()
			        .filter(p -> isKeyValidForProcessing(p.getKey()))
			        .map(p -> p.getKey())
			        .collect(Collectors.toList());
		return filteredList;
	}
	
	private boolean isKeyValidForProcessing(String key){
		key = key.substring(key.lastIndexOf("/")+1, key.length());
		return getConfiguration().getMovieDataSetSuffix().contains(key) ||
		getConfiguration().getCastNamesSuffix().contains(key) ||
		getConfiguration().getPrincipalsDatasetSuffix().contains(key) ||
		getConfiguration().getRatingsDatasetSuffix().contains(key);
	}
	
	/**
	 * Writes file to local system with data from the {@link InputStream} passed to it. The incoming data is gzipped so this method uncompresses the
	 * data before writing to file.
	 * This method uses the key to retrieve the file system path using {@link #getFullPath(String)}
	 * @param input
	 * @param key
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void writeFile(InputStream input, String key) throws IOException, InterruptedException {
		String formattedKey = key.replace("/", "_");//.replace(".gz", "");
		String fullFilePath = getFullPath(key,formattedKey);
		if(StringUtils.isEmpty(fullFilePath)){
			LOGGER.error("Unknown file path found for key {}. Will skip.", key);
			return;
		}
		try (OutputStream out = new FileOutputStream(fullFilePath);
			ReadableByteChannel readableByteChannel = Channels.newChannel(input);
			WritableByteChannel writableByteChannel = Channels.newChannel(out)) {
			// create buffer with capacity of 48 bytes
			ByteBuffer buf = ByteBuffer.allocate(4096);

			

			int bytesRead = readableByteChannel.read(buf); // read into buffer.
			while (bytesRead != -1) {
				buf.flip(); // make buffer ready for read
				writableByteChannel.write(buf);
				buf.clear(); // make buffer ready for writing
				bytesRead = readableByteChannel.read(buf);
			}

		}
		LOGGER.info("Completed writing file {}",fullFilePath);
	}
	
	/**
	 * Returns path stored in {@link Configuration} which could be either of {@link Configuration#getUnprocessedMovies()}, 
	 * {@link Configuration#getUnprocessedCastNames()} based on the key passed to it.
	 * 
	 * @param key
	 * @return
	 */
	private String getFullPath(String key, String formattedKey){
		if(doesKeyMatch(getConfiguration().getCastSets(), key)){
			return getConfiguration().getUnprocessedCastNames()+File.separator+formattedKey;
		}else if(doesKeyMatch(getConfiguration().getPrincipalSets(), key)){
			return getConfiguration().getUnprocessedPrincipals()+File.separator+formattedKey;
		}else if(doesKeyMatch(getConfiguration().getMovieSets(), key)){
			return getConfiguration().getUnprocessedMovies()+File.separator+formattedKey;
		}else if(doesKeyMatch(getConfiguration().getRatingSets(), key)){
			return getConfiguration().getUnprocessedRatings()+File.separator+formattedKey;
		}else{
			LOGGER.error("Uknown key found - {} ", key );
			return null;
		}
	}
	
	private boolean doesKeyMatch(String[] configuredKeys, String key){
		return Arrays.stream(configuredKeys)
				.anyMatch(k-> key.contains(k));
	}
	/**
	 * Logs common error
	 * @param ase
	 */
	private void logErrorDetails(AmazonServiceException ase){
		LOGGER.error("Caught an AmazonServiceException, which" +
				" means your request made it " +
				"to Amazon S3, but was rejected with an error response" +
				" for some reason.");
		LOGGER.error("Error Message:    {}" , ase.getMessage());
		LOGGER.error("HTTP Status Code: {}" , ase.getStatusCode());
		LOGGER.error("AWS Error Code:   {}" , ase.getErrorCode());
		LOGGER.error("Error Type:       {}" , ase.getErrorType());
		LOGGER.error("Request ID:       {}" , ase.getRequestId());
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	
}