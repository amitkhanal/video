package com.acme.video.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.acme.video.data.model.Cast;
import com.acme.video.data.model.Movie;
import com.acme.video.data.model.Rating;

public interface MovieService extends DataService{

	/**
	 * Returns Movie for passed movie id
	 * @param id
	 * @return
	 */
	public Movie findByMovieId(String id);
	
	/**
	 * Saves the rating object for the movie with passed movieId
	 * 
	 * @param movieId
	 * @param rating
	 */
	public void saveRating(String movieId, Rating rating);
	
	/**
	 * Saves casts for passed movied id
	 * 
	 * @param movieId
	 * @param casts
	 */
	public void saveCasts(String movieId, List<Cast> casts);
	
	/**
	 * Returns paginated list movies
	 * 
	 * @param itemsPerPage
	 * @param pageNumber
	 * @return
	 */
	public Page<Movie> getAllMovies(int itemsPerPage, int pageNumber);
	
	/**
	 * Performs search on {@link Movie#getPrimaryTitle()} and returns results
	 * @param searchTerm
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Movie> search(String searchTerm, int pageNumber, int pageSize);
	
	/**
	 * Saves the passed movie in repository
	 * @param movie
	 */
	public void save(Movie movie);
}
