package com.acme.video.service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.acme.video.data.model.Cast;
import com.acme.video.data.model.Episode;
import com.acme.video.data.model.Movie;
import com.acme.video.data.model.Rating;
import com.acme.video.data.repository.MovieRepository;
import com.acme.video.util.FileUtil;

/**
 * This class provides various helper methods to retrieve and save {@link Movie} in repository
 * 
 * @author amitkhanal
 *
 */
@Service
public class MovieServiceImpl extends AbstractService implements MovieService {

	@Autowired
	private MovieRepository movieRepository;
	
	private EpisodeService episodeService;

	private final Logger LOGGER = LoggerFactory.getLogger(MovieService.class);
	
	/* (non-Javadoc)
	 * @see com.acme.video.service.MovieService#findByMovieId(java.lang.String)
	 */
	@Override
	public Movie findByMovieId(String id){
		if(StringUtils.isEmpty(id)){
			return null;
		}
		return getMovieRepository().findFirstByMovieId(id);
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.service.MovieService#saveRating(java.lang.String, com.acme.video.data.model.Rating)
	 */
	@Override
	public void saveRating(String movieId, Rating rating){
		if(StringUtils.isEmpty(movieId)){
			throw new IllegalArgumentException("Movie id is null");
		}
		if(rating==null){
			throw new IllegalArgumentException("Rating is null");
		}
		Movie movie = findByMovieId(movieId.trim().toLowerCase());
		if(movie == null){
			throw new IllegalArgumentException("Movie not found for id "+movieId);
		}
		movie.setRating(rating);
		save(movie);
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.service.MovieService#saveCasts(java.lang.String, java.util.List)
	 */
	@Override
	public void saveCasts(String movieId, List<Cast> casts){
		if(StringUtils.isEmpty(movieId)){
			//throw new IllegalArgumentException("Movie id is null");
		}
		
		Movie movie = findByMovieId(movieId.trim().toLowerCase());
		if(movie == null){
			//throw new IllegalArgumentException("Movie not found for id "+movieId);
			//We will ignore this since we're skipping titleType that are not 'movie'
			return;
		}
		
		if(casts==null || casts.isEmpty()){
			throw new IllegalArgumentException("Principals are null or empty");
		}
		
		movie.setCasts(casts);
		save(movie);
	}
	/**
	 * @param itemsPerPage
	 * @param pageNumber
	 * @return
	 */
	/* (non-Javadoc)
	 * @see com.acme.video.service.MovieService#getAllMovies(int, int)
	 */
	@Override
	public Page<Movie> getAllMovies(int itemsPerPage, int pageNumber) {
		PageRequest request = new PageRequest((pageNumber - 1), itemsPerPage, new Sort(Sort.Direction.ASC, "primaryTitle"));
		return getMovieRepository().findAll(request);
	}
	
	/**
	 * @param searchTerm
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	/* (non-Javadoc)
	 * @see com.acme.video.service.MovieService#search(java.lang.String, int, int)
	 */
	@Override
	public Page<Movie> search(String searchTerm, int pageNumber, int pageSize){
		LOGGER.info("Searching for term {}",searchTerm);;
        PageRequest request = new PageRequest((pageNumber-1), pageSize, new Sort(Sort.Direction.DESC, "primaryTitle"));
        return getMovieRepository().search(searchTerm.trim(), request);
	}

	/* (non-Javadoc)
	 * @see com.acme.video.data.service.DataService#persistData(java.lang.String)
	 */
	@Override
	public void persistData(String movieDataLocation) throws Exception{
		File[] unprocessedfiles = FileUtil.getFiles(movieDataLocation);
		if(unprocessedfiles == null){
			return;
		}
		
		AtomicInteger index = new AtomicInteger();
		int count = 0;
		for(File file : unprocessedfiles){
			count++;
			Path filePath = Paths.get(file.getAbsolutePath());
			//Stream<String> dataStream = FileUtil.lines(filePath);
			LOGGER.info("Processing {} of {}, file -> {}",count, unprocessedfiles.length, file.getAbsolutePath());
			try (Stream<String> dataStream = FileUtil.lines(filePath)) {
				dataStream
					.skip(1)
					.parallel()
					//filter(line -> isMovie(line))
					.forEach(line -> { 
							//--- will skip batch saving for demo purposes
							save(line);
							if(LOGGER.isDebugEnabled()) {
								index.incrementAndGet();
								LOGGER.debug("Movie: Processed line {}",index);
							}
						});
			}catch(Exception e){
				LOGGER.error("File "+file.getAbsolutePath() + " could not be processed. Skipping it.", e);
			}
			moveFileToProcessed(file.toPath(), Paths.get(getConfiguration().getProcessedMovies()));
		}
		LOGGER.info("Movie: Total entries = {}",totalItems());
	}
	
	/**
	 * Saves the passed data as a {@link Movie} entity in repository
	 * 
	 * @param data
	 */
	private void save(String data){
		String[] movieAttribute = data.split("\t");
		if(movieAttribute.length!=9){
			//LOGGER.error("Movie data is missing column(s);. Will skip processing -> {} ", data);
			FileUtil.addErrorEntry(getConfiguration().getErrorMovies(), data);
			return;
		}
		try{
		Movie movie = getMovieRepository().findFirstByMovieId(movieAttribute[0]);
			if(movie == null){
				movie = new Movie(movieAttribute[0].trim().equals("\\N")?"":movieAttribute[0].toLowerCase(),
						movieAttribute[2].trim().equals("\\N")?"":movieAttribute[2],
						movieAttribute[3].trim().equals("\\N")?"":movieAttribute[3],
						(movieAttribute[4].trim().equals("\\N") || (Integer.valueOf(movieAttribute[4].trim()))==0 ?false:true),
						movieAttribute[5].trim().equals("\\N")?0:Integer.valueOf(movieAttribute[5].trim()),
						movieAttribute[6].trim().equals("\\N")?0:Integer.valueOf(movieAttribute[6].trim()),
						movieAttribute[7].trim().equals("\\N")?0:Integer.valueOf(movieAttribute[7].trim()),
						movieAttribute[8].trim());
			}
			
			if(movie.isSeason){
				List<Episode> episodes = getEpisodeService().getEpisodes(movie.getMovieId());
				int sumOfRating = 0;
				for(int i=0;i<episodes.size();i++){
					sumOfRating += episodes.get(i).getRating();
				}
				int averageRating = sumOfRating/episodes.size();
				
				Rating rating = new Rating(movie.getMovieId(), averageRating, 1);
				movie.setRating(rating);
			}
			
		getMovieRepository().save(movie);
		
		}catch(Exception e){
			//LOGGER.error("Error occurred while adding movie data -> {} ",data);
			FileUtil.addErrorEntry(getConfiguration().getErrorMovies(), data);
		}
	}
	
	@Override
	public void save(Movie movie){
		if(movie!=null) {
			getMovieRepository().save(movie);
		}
	}
	
	/**
	 * @param line
	 * @return
	 */
	/*private boolean isMovie(String line){
		String[] movieData = line.split("\t");
		return movieData[1].equalsIgnoreCase("movie");
	}*/
	
	@Override
	public long totalItems() {
		return getMovieRepository().count();
	}
	
	public MovieRepository getMovieRepository() {
		return movieRepository;
	}

	public void setMovieRepository(MovieRepository movieRepository) {
		this.movieRepository = movieRepository;
	}

	public EpisodeService getEpisodeService() {
		return episodeService;
	}

	public void setEpisodeService(EpisodeService episodeService) {
		this.episodeService = episodeService;
	}
	
	
}
