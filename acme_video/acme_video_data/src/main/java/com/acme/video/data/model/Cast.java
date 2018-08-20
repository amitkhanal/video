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
 * This class represents the movie cast data stored in Mongo
 */
@Document
public class Cast {

	@JsonIgnore
	@Id
	private ObjectId id;
	
	@Indexed
	private String castId;
	
	@Indexed
	private String primaryName;
	
	private int birthYear;
	private int deathYear;
	private String primaryProfession;
	private String knownForTitles;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	public Cast(String castId, String primaryName, int birthYear, int deathYear, String primaryProfession, String knownForTitles){
		this.castId = castId;
		this.primaryName = primaryName;
		this.birthYear = birthYear;
		this.deathYear = deathYear;
		this.primaryProfession = primaryProfession;
		this.knownForTitles = knownForTitles;
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getCastId() {
		return castId;
	}
	public void setCastId(String castId) {
		this.castId = castId;
	}
	public String getPrimaryName() {
		return primaryName;
	}
	public void setPrimaryName(String primaryName) {
		this.primaryName = primaryName;
	}
	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	public int getDeathYear() {
		return deathYear;
	}
	public void setDeathYear(int deathYear) {
		this.deathYear = deathYear;
	}
	public String getPrimaryProfession() {
		return primaryProfession;
	}
	public void setPrimaryProfession(String primaryProfession) {
		this.primaryProfession = primaryProfession;
	}
	public String getKnownForTitles() {
		return knownForTitles;
	}
	public void setKnownForTitles(String knownForTitles) {
		this.knownForTitles = knownForTitles;
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
