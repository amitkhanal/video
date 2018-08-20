package com.acme.video.data.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.acme.video.data.model.Rating;

/**
 * Repository to handle all rating data operations
 * 
 * @author amitkhanal
 *
 */
public interface RatingRepository extends MongoRepository<Rating, ObjectId> {
	
	Rating findFirstByRatingId(String ratingId);

}
