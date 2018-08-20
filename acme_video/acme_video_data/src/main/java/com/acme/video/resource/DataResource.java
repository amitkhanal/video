package com.acme.video.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acme.video.Configuration;
import com.acme.video.data.DataLoader;
import com.acme.video.data.DatasetRetriever;

@RestController
@RequestMapping("/data")
public class DataResource implements ErrorController {

	@Autowired
	private Configuration configuration;
	
	@Autowired
	private DataLoader dataLoader;
	
	@Autowired
	private DatasetRetriever datasetRetriever;

	private static final Logger LOGGER = Logger.getLogger(DataResource.class);

	private static final String PATH = "/error";
	
	public DataLoader getDataLoader() {
		return dataLoader;
	}

	public void setDataLoader(DataLoader dataLoader) {
		this.dataLoader = dataLoader;
	}

    @Override
    public String getErrorPath() {
        return PATH;
    }
    
    @RequestMapping(value=PATH)
    Response error (@Context HttpServletRequest request){
    	LOGGER.error("Unsupported operation for request "+request);
    	return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    /**
     * Invokes {@link DataLoader} to load the unprocessed data into repository
     * 
     * @return
     */
    @RequestMapping("load")
    public Response load(){
    	try {
			getDataLoader().load();
		} catch (Exception e) {
			LOGGER.error("Error occurred while loading datasets into repository", e);
			return Response.serverError().entity("Internal error occurred while saving data into repository. Please contact support").build();
		}
    	return Response.status(200).build();
    }
    
    /**
     * Invokes {@link DataLoader} to load the unprocessed data into repository
     * 
     * @return
     */
    @RequestMapping("retrieve")
    public Response retrieve(){
    	try {
			getDatasetRetriever().getDatasets(getConfiguration().getS3Bucket(), getConfiguration().getS3BucketKey());
		} catch (Exception e) {
			LOGGER.error("Error occurred while retrieving datasets from AWS S3", e);
			return Response.serverError().entity("Internal error occurred while retrieving datasets from AWS S3. Please contact support").build();
		}
    	return Response.status(200).build();
    }
    
    public Configuration getConfiguration() {
		return configuration;
	}

    public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public DatasetRetriever getDatasetRetriever() {
		return datasetRetriever;
	}

	public void setDatasetRetriever(DatasetRetriever datasetRetriever) {
		this.datasetRetriever = datasetRetriever;
	}
 
    
}