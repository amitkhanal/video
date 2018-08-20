package com.acme.video.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.acme.video.data.model.Movie;
import com.acme.video.data.model.Rating;
import com.acme.video.data.repository.RatingRepository;
import com.acme.video.util.FileUtil;

/**
 * Provides various helper methods to retrieve and save {@link Rating} entity in repository
 * 
 * @author amitkhanal
 *
 */
@Service
public class RatingServiceImpl extends AbstractService implements RatingService {

	@Autowired
	private RatingRepository ratingRepository;
	
	@Autowired
	private MovieService movieService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RatingService.class);

	/**
	 * @param itemsPerPage
	 * @param pageNumber
	 * @return
	 */
	@Override
	public Page<Rating> getAllRatings(int itemsPerPage, int pageNumber) {
        PageRequest request = new PageRequest((pageNumber-1), itemsPerPage, new Sort(Sort.Direction.DESC, "averageRating"));
        return getRatingRepository().findAll(request);
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.data.service.DataService#persistData(java.lang.String)
	 */
	@Override
	public void persistData(String ratingDataLocation) throws IOException{
		File[] unprocessedfiles = FileUtil.getFiles(ratingDataLocation);
		if(unprocessedfiles == null){
			LOGGER.error("persistRatingsData: There were no rating data to process");
			return;
		}
		int count=0;
		AtomicInteger index = new AtomicInteger();
		for(File file : unprocessedfiles){
			count++;
			Path filePath = Paths.get(file.getAbsolutePath());
			LOGGER.info("Processing {} of {}, file {} -> ",count, unprocessedfiles.length, file.getAbsolutePath());
			try (Stream<String> dataStream = FileUtil.lines(filePath)) {
				dataStream
					.skip(1)
					.parallel()
					.forEach(line -> {
							//--- will skip batch saving for demo purposes
							save(line);
							if(LOGGER.isDebugEnabled()) {
								index.incrementAndGet();
								LOGGER.debug("Rating: Processed line {}",index);
							}
						});
			}catch(Exception e){
				LOGGER.error("File "+file.getAbsolutePath() + " could not be processed. Skipping it.", e);
			}
			moveFileToProcessed(file.toPath(), Paths.get(getConfiguration().getProcessedRatings()));
		}
		LOGGER.info("Rating: Total entries = {}",getRatingRepository().count());
	}
	
	/**
	 * @param data
	 */
	private void save(String data){
		String[] ratingAttribute = data.split("\t");
		if (ratingAttribute.length != 3) {
			//LOGGER.error("Rating data is missing column(s). Will skip processing -> {}" , data);
			FileUtil.addErrorEntry(getConfiguration().getErrorRatings(), data);
			return;
		}

		try {
			Rating rating = getRatingRepository().findFirstByRatingId(ratingAttribute[0]);
			if (rating == null) {
				rating = new Rating(ratingAttribute[0].trim().toLowerCase(),
						ratingAttribute[1].trim().equals("\\N") ? 0f : Float.valueOf(ratingAttribute[1].trim()),
						ratingAttribute[2].trim().equals("\\N") ? 0 : Integer.valueOf(ratingAttribute[2].trim()));
						
			}
			Rating savedRating = getRatingRepository().save(rating);
			Movie movie = getMovieService().findByMovieId(ratingAttribute[0]);
			if(movie!=null){
				 //--- we're only saving rating in movie if it's not already set. This is to save processing time for this demo application
				if(movie.getRating() == null) {
					movie.setRating(savedRating);
					getMovieService().save(movie);
				}
			}
			
		} catch (Exception e) {
			//LOGGER.error("Error occurred while adding movie data -> {}" , data);
			FileUtil.addErrorEntry(getConfiguration().getErrorRatings(), data);
		}
	}
	
	/**
	 * Saves the passed rating entity in Mongo repository
	 * @param rating
	 */
	@Override
	public void save(Rating rating){
		if(rating!=null){
			getRatingRepository().save(rating);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.data.service.DataService#totalItems()
	 */
	@Override
	public long totalItems() {
		return getRatingRepository().count();
	}
	
	public RatingRepository getRatingRepository() {
		return ratingRepository;
	}

	public void setRatingRepository(RatingRepository ratingRepository) {
		this.ratingRepository = ratingRepository;
	}

	public MovieService getMovieService() {
		return movieService;
	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
	
}
