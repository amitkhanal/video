package com.acme.video.service;

import org.springframework.data.domain.Page;

import com.acme.video.data.model.Rating;

public interface RatingService extends DataService {

	/**
	 * Retrieves ratings from repository
	 * 
	 * @param itemsPerPage
	 * @param pageNumber
	 * @return
	 */
	public Page<Rating> getAllRatings(int itemsPerPage, int pageNumber);
	
	/**
	 * Saves the passes rating entity in repository
	 * @param rating
	 */
	public void save(Rating rating);
}
