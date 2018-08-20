package com.acme.video.data.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author amitkhanal
 * 
 * This class represents the movie data stored in Mongo
 */
@Document
public class Movie {

	@JsonIgnore
	@Id
	private ObjectId id;
	
	@Indexed
	private String movieId;
	
	@Indexed
	private String primaryTitle;
	
	private String originalTitle;
	private boolean isAdult;
	private int startYear;
	private int endYear;
	private int runtimeMinutes;
	private String genres;
	
	public boolean isSeason;
//	public int rating;
	
	@DBRef
	private Rating rating;
	
	@DBRef
	private List<Cast> casts;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public Movie(String movieId, String primaryTitle, String originalTitle, boolean isAdult, int startYear, int endYear, int runtimeMinutes, String genres){
		this.movieId = movieId;
		this.primaryTitle = primaryTitle;
		this.originalTitle = originalTitle;
		this.isAdult = isAdult;
		this.startYear = startYear;
		this.endYear = endYear;
		this.runtimeMinutes = runtimeMinutes;
		this.genres = genres;
	}
	
	public Rating getRating() {
		return rating;
	}
	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public List<Cast> getCasts() {
		return casts;
	}

	public void setCasts(List<Cast> casts) {
		this.casts = casts;
	}

	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getMovieId() {
		return movieId;
	}
	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}
	public String getPrimaryTitle() {
		return primaryTitle;
	}
	public void setPrimaryTitle(String primaryTitle) {
		this.primaryTitle = primaryTitle;
	}
	public String getOriginalTitle() {
		return originalTitle;
	}
	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}
	public boolean isAdult() {
		return isAdult;
	}
	public void setAdult(boolean isAdult) {
		this.isAdult = isAdult;
	}
	public int getStartYear() {
		return startYear;
	}
	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}
	public int getEndYear() {
		return endYear;
	}
	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}
	public int getRuntimeMinutes() {
		return runtimeMinutes;
	}
	public void setRuntimeMinutes(int runtimeMinutes) {
		this.runtimeMinutes = runtimeMinutes;
	}
	public String getGenres() {
		return genres;
	}
	public void setGenres(String genres) {
		this.genres = genres;
	}
	
	@Override
	public String toString() {
		try {
			return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}
}
