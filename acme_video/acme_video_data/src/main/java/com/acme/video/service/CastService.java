package com.acme.video.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.acme.video.data.model.Cast;

public interface CastService extends DataService {

	/**
	 * Retrieve all casts and returns paginated list
	 * @param itemsPerPage
	 * @param pageNumber
	 * @return
	 */
	public Page<Cast> getAllCasts(int itemsPerPage, int pageNumber);
	
	/**
	 * Saves the passes cast entity in repository
	 * @param cast
	 */
	public void save(Cast cast);
	
	/**
	 * Returns {@link List} of casts for the passed cast ids
	 * @param castIds
	 * @return
	 */
	public List<Cast> findCasts(String[] castIds);
}
