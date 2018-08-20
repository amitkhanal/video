package com.acme.video;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

/**
 * @author amitkhanal
 * 
 * This class captures external configurations required for the application. Configurations are loaded from an external file as specified in the 
 * {@link PropertySource}
 */
@org.springframework.context.annotation.Configuration
@PropertySource("file:/Users/amitkhanal/Data/netflix_workspace/netflix_video/netflix_video_data/src/main/java/com/resources/Configuration.properties")
@ConfigurationProperties
public class Configuration {

	private String credentialsFile;
	private String profileName;
	private String region;
	private String s3Bucket;
	private String s3BucketKey;

	private String movieDataSetSuffix;
	private String ratingsDatasetSuffix;
	private String principalsDatasetSuffix;
	private String castNamesSuffix;

	private String[] movieSets;
	private String[] castSets;
	private String[] principalSets;
	private String[] ratingSets;
	
	private String unprocessedMovies;
	private String unprocessedRatings;
	private String unprocessedPrincipals;
	private String unprocessedCastNames;

	private String processedMovies;
	private String processedRatings;
	private String processedPrincipals;
	private String processedCastNames;

	private String errorMovies;
	private String errorRatings;
	private String errorPrincipals;
	private String errorCastNames;

			
	public String getCredentialsFile() {
		return credentialsFile;
	}
	public void setCredentialsFile(String credentialsFile) {
		this.credentialsFile = credentialsFile;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getS3Bucket() {
		return s3Bucket;
	}
	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}
	public String getS3BucketKey() {
		return s3BucketKey;
	}
	public void setS3BucketKey(String s3BucketKey) {
		this.s3BucketKey = s3BucketKey;
	}
	public String getMovieDataSetSuffix() {
		return movieDataSetSuffix;
	}
	public void setMovieDataSetSuffix(String movieDataSetSuffix) {
		this.movieDataSetSuffix = movieDataSetSuffix;
		if(!StringUtils.isEmpty(movieDataSetSuffix)){
			movieSets = movieDataSetSuffix.split(",");
		}
	}
	public String getRatingsDatasetSuffix() {
		return ratingsDatasetSuffix;
	}
	public void setRatingsDatasetSuffix(String ratingsDatasetSuffix) {
		this.ratingsDatasetSuffix = ratingsDatasetSuffix;
		if(!StringUtils.isEmpty(ratingsDatasetSuffix)){
			ratingSets = ratingsDatasetSuffix.split(",");
		}
	}
	public String getPrincipalsDatasetSuffix() {
		return principalsDatasetSuffix;
	}
	public void setPrincipalsDatasetSuffix(String principalsDatasetSuffix) {
		this.principalsDatasetSuffix = principalsDatasetSuffix;
		if(!StringUtils.isEmpty(principalsDatasetSuffix)){
			principalSets = principalsDatasetSuffix.split(",");
		}
	}
	public String getCastNamesSuffix() {
		return castNamesSuffix;
	}
	public void setCastNamesSuffix(String castNamesSuffix) {
		this.castNamesSuffix = castNamesSuffix;
		if(!StringUtils.isEmpty(castNamesSuffix)){
			castSets = castNamesSuffix.split(",");
		}
	}
	public String getUnprocessedMovies() {
		return unprocessedMovies;
	}
	public void setUnprocessedMovies(String unprocessedMovies) {
		this.unprocessedMovies = unprocessedMovies;
	}
	public String getUnprocessedRatings() {
		return unprocessedRatings;
	}
	public void setUnprocessedRatings(String unprocessedRatings) {
		this.unprocessedRatings = unprocessedRatings;
	}
	public String getUnprocessedPrincipals() {
		return unprocessedPrincipals;
	}
	public void setUnprocessedPrincipals(String unprocessedPrincipals) {
		this.unprocessedPrincipals = unprocessedPrincipals;
	}
	public String getUnprocessedCastNames() {
		return unprocessedCastNames;
	}
	public void setUnprocessedCastNames(String unprocessedCastNames) {
		this.unprocessedCastNames = unprocessedCastNames;
	}
	public String getProcessedMovies() {
		return processedMovies;
	}
	public void setProcessedMovies(String processedMovies) {
		this.processedMovies = processedMovies;
	}
	public String getProcessedRatings() {
		return processedRatings;
	}
	public void setProcessedRatings(String processedRatings) {
		this.processedRatings = processedRatings;
	}
	public String getProcessedPrincipals() {
		return processedPrincipals;
	}
	public void setProcessedPrincipals(String processedPrincipals) {
		this.processedPrincipals = processedPrincipals;
	}
	public String getProcessedCastNames() {
		return processedCastNames;
	}
	public void setProcessedCastNames(String processedCastNames) {
		this.processedCastNames = processedCastNames;
	}
	public String getErrorMovies() {
		return errorMovies;
	}
	public void setErrorMovies(String errorMovies) {
		this.errorMovies = errorMovies;
	}
	public String getErrorRatings() {
		return errorRatings;
	}
	public void setErrorRatings(String errorRatings) {
		this.errorRatings = errorRatings;
	}
	public String getErrorPrincipals() {
		return errorPrincipals;
	}
	public void setErrorPrincipals(String errorPrincipals) {
		this.errorPrincipals = errorPrincipals;
	}
	public String getErrorCastNames() {
		return errorCastNames;
	}
	public void setErrorCastNames(String errorCastNames) {
		this.errorCastNames = errorCastNames;
	}
	public String[] getMovieSets() {
		return movieSets;
	}
	public void setMovieSets(String[] movieSets) {
		this.movieSets = movieSets;
	}
	public String[] getCastSets() {
		return castSets;
	}
	public void setCastSets(String[] castSets) {
		this.castSets = castSets;
	}
	public String[] getPrincipalSets() {
		return principalSets;
	}
	public void setPrincipalSets(String[] principalSets) {
		this.principalSets = principalSets;
	}
	public String[] getRatingSets() {
		return ratingSets;
	}
	public void setRatingSets(String[] ratingSets) {
		this.ratingSets = ratingSets;
	}

	
}
