package com.acme.video.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acme.video.data.model.Cast;
import com.acme.video.data.model.Movie;
import com.acme.video.util.FileUtil;

/**
 * Provides helper methods to retrieve cast data and save them under {@link Movie}
 * 
 * @author amitkhanal
 *
 */
@Service
public class PrincipalService extends AbstractService implements DataService {
	
	@Autowired
	private CastService castService;

	@Autowired
	private MovieService movieService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PrincipalService.class);

	/* (non-Javadoc)
	 * @see com.acme.video.data.service.DataService#persistData(java.lang.String)
	 */
	@Override
	public void persistData(String principalDataLocation) throws IOException{
		File[] unprocessedfiles = FileUtil.getFiles(principalDataLocation);
		if(unprocessedfiles == null){
			LOGGER.error("persistPrincipalData: There were no cast data to process");
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
								LOGGER.debug("Principal: Processed line {}",index);
							}
					});
			}catch(Exception e){
				LOGGER.error("File "+file.getAbsolutePath() + " could not be processed. Skipping it.", e);
			}
			moveFileToProcessed(file.toPath(), Paths.get(getConfiguration().getProcessedPrincipals()));
		}
	}
	
	/**
	 * Retrieves {@link Cast} from the passed data and saves them under {@link Movie}
	 * @param data
	 */
	private void save(String data) {
		String[] principalAttribute = data.split("\t");
		if (principalAttribute.length != 2) {
			//LOGGER.error("Principal data is missing column(s). Will skip processing -> {}" , data);
			FileUtil.addErrorEntry(getConfiguration().getErrorPrincipals(), data);
			return;
		}

		String[] castIds = principalAttribute[1].split(",");
		List<Cast> casts = getCastService().findCasts(castIds);
		if(casts != null && !casts.isEmpty()){
			getMovieService().saveCasts(principalAttribute[0],casts);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.data.service.DataService#totalItems()
	 * 
	 * This is a no op for this class. All pricipals are stored with Movie entity
	 */
	@Override
	public long totalItems() {
		return -1;
	}
	
	public CastService getCastService() {
		return castService;
	}

	public void setCastService(CastService castService) {
		this.castService = castService;
	}

	public MovieService getMovieService() {
		return movieService;
	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
	
	
	
}
