package com.acme.video.data;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.acme.video.Configuration;
import com.acme.video.service.CastService;
import com.acme.video.service.MovieService;
import com.acme.video.service.PrincipalService;
import com.acme.video.service.RatingService;

@Component
public class DataLoaderImpl implements DataLoader{

	@Autowired
	private MovieService movieService;
	
	@Autowired
	private CastService castService;
	
	@Autowired
	private PrincipalService principalService;
	
	@Autowired
	private RatingService ratingService;

	@Autowired
	private Configuration configuration;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
	
	/* (non-Javadoc)
	 * @see com.acme.video.data.DataLoader#load()
	 */
	public void load() throws Exception{
		LOGGER.info("Starting loading of datasets {}",Calendar.getInstance().getTime());
		//--- load movie data in repository
		
		LOGGER.info("Starting loading of movie dataset {}",Calendar.getInstance().getTime());
		getMovieService().persistData(getConfiguration().getUnprocessedMovies());
		LOGGER.info("Ending loading of movie dataset {}",Calendar.getInstance().getTime());
		
		LOGGER.info("Starting loading of cast dataset {}",Calendar.getInstance().getTime());
		getCastService().persistData(getConfiguration().getUnprocessedCastNames());
		LOGGER.info("Ending loading of cast dataset {}",Calendar.getInstance().getTime());
		
		LOGGER.info("Starting loading of principal dataset {}",Calendar.getInstance().getTime());
		getPrincipalService().persistData(getConfiguration().getUnprocessedPrincipals());
		LOGGER.info("Ending loading of principal dataset {}",Calendar.getInstance().getTime());
		
		LOGGER.info("Starting loading of rating dataset {}",Calendar.getInstance().getTime());
		getRatingService().persistData(getConfiguration().getUnprocessedRatings());
		LOGGER.info("Ending loading of rating dataset {}",Calendar.getInstance().getTime());
		
		LOGGER.info("Ending loading of datasets {}",Calendar.getInstance().getTime());
	}
	
	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public MovieService getMovieService() {
		return movieService;
	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}

	public CastService getCastService() {
		return castService;
	}

	public void setCastService(CastService castService) {
		this.castService = castService;
	}

	public PrincipalService getPrincipalService() {
		return principalService;
	}

	public void setPrincipalService(PrincipalService principalService) {
		this.principalService = principalService;
	}

	public RatingService getRatingService() {
		return ratingService;
	}

	public void setRatingService(RatingService ratingService) {
		this.ratingService = ratingService;
	}
	
}
