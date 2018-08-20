package com.acme.video.data.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.acme.video.data.model.Cast;

/**
 *  Repository to handle all cast data operations
 *  
 * @author amitkhanal
 */
public interface CastRepository extends MongoRepository<Cast, ObjectId> {
	
	Cast findFirstByCastId(String castId);
	
}
