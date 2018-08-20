package com.acme.video.data.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author amitkhanal
 * 
 * This class represents the movie rating data stored in Mongo
 */
@Document
public class Rating {

	@JsonIgnore
	@Id
	private ObjectId id;
	
	@Indexed
	private String ratingId;
	private float averageRating;
	private int numVotes;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public Rating(String ratingId, float averageRating, int numVotes){
		this.ratingId = ratingId;
		this.averageRating = averageRating;
		this.numVotes = numVotes;
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getRatingId() {
		return ratingId;
	}
	public void setRatingId(String ratingId) {
		this.ratingId = ratingId;
	}
	public float getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}
	public int getNumVotes() {
		return numVotes;
	}
	public void setNumVotes(int numVotes) {
		this.numVotes = numVotes;
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
