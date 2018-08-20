package com.acme.video.data.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.acme.video.data.model.Movie;

/**
 * Repository to handle all movie data operations
 * 
 * @author amitkhanal
 */
public interface MovieRepository extends MongoRepository<Movie, ObjectId>{

	Movie findFirstByMovieId(String movieId);
	
	@Query(value = "{'primaryTitle': {'$regex': ?0,'$options': 'i'}}")
	Page<Movie> search(String title, Pageable pageable);
}
