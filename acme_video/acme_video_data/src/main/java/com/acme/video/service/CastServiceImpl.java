package com.acme.video.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.acme.video.data.model.Cast;
import com.acme.video.data.repository.CastRepository;
import com.acme.video.util.FileUtil;

/**
 * Provides various helpers methods to retrieve and save {@link Cast} entities in repository
 * 
 * @author amitkhanal
 *
 */
@Service
public class CastServiceImpl extends AbstractService implements CastService {

	@Autowired
	private CastRepository castRepository;
	
	@Autowired
    private MongoTemplate mongoTemplate;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CastService.class);
	
	/* (non-Javadoc)
	 * @see com.acme.video.service.CastService#getAllCasts(int, int)
	 */
	@Override
	public Page<Cast> getAllCasts(int itemsPerPage, int pageNumber) {
		PageRequest request = new PageRequest((pageNumber - 1), itemsPerPage, new Sort(Sort.Direction.ASC, "primaryName"));
		return getCastRepository().findAll(request);
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.data.service.DataService#persistData(java.lang.String)
	 */
	@Override
	public void persistData(String castDataLocation) throws IOException{
		File[] unprocessedfiles = FileUtil.getFiles(castDataLocation);
		if(unprocessedfiles == null){
			LOGGER.error("persistCastData: There were no cast data to process");
			return;
		}
		int count=0;
		AtomicInteger index = new AtomicInteger();
		for(File file : unprocessedfiles){
			Path filePath = Paths.get(file.getAbsolutePath());
			count++;
			LOGGER.info("Processing {} of {}, file {} -> ",count, unprocessedfiles.length, file.getAbsolutePath());
			try (Stream<String> dataStream = FileUtil.lines(filePath)) {
				//long total = dataStream.count();
				dataStream
					.skip(1)
					.parallel()
					.forEach(line -> {
							//--- will skip batch saving for demo purposes
							save(line);
							if(LOGGER.isDebugEnabled()) {
								index.incrementAndGet();
								LOGGER.debug("Cast: Processed line "+index);
							}
						});
			}catch(Exception e){
				LOGGER.error("File "+file.getAbsolutePath() + " could not be processed. Skipping it.", e);
			}
			moveFileToProcessed(file.toPath(), Paths.get(getConfiguration().getProcessedCastNames()));
		}
		LOGGER.info("Cast: Total entries = "+totalItems());
	}
	
	/**
	 * @param data
	 */
	private void save(String data) {
		String[] castAttribute = data.split("\t");
		if (castAttribute.length != 6) {
			//LOGGER.error("Cast data is missing column(s). Will skip processing -> " + data);
			FileUtil.addErrorEntry(getConfiguration().getErrorCastNames(), data);
			return;
		}

		try {
			Cast cast = getCastRepository().findFirstByCastId(castAttribute[0]);
			if (cast == null) {
				cast = new Cast(castAttribute[0].trim(), castAttribute[1].trim().toLowerCase(),
						castAttribute[2].trim().equals("\\N") ? 0 : Integer.valueOf(castAttribute[2].trim()),
						castAttribute[3].trim().equals("\\N") ? 0 : Integer.valueOf(castAttribute[3].trim()),
						castAttribute[4].trim(), castAttribute[5].trim());
			}
			getCastRepository().save(cast);
		} catch (Exception e) {
			//LOGGER.error("Error occurred while adding cast data -> " + data);
			FileUtil.addErrorEntry(getConfiguration().getErrorCastNames(), data);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.service.CastService#save(com.acme.video.data.model.Cast)
	 */
	public void save(Cast cast){
		if(cast!=null){
			getCastRepository().save(cast);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.service.CastService#findCasts(java.lang.String[])
	 */
	public List<Cast> findCasts(String[] castIds){
		Query query = new Query();
		query.addCriteria(Criteria.where("castId").in(Arrays.asList(castIds)));
		List<Cast> casts = getMongoTemplate().find(query,Cast.class);
		return casts;
	}
	
	/* (non-Javadoc)
	 * @see com.acme.video.data.service.DataService#totalItems()
	 */
	@Override
	public long totalItems() {
		return getCastRepository().count();
	}
	
	public CastRepository getCastRepository() {
		return castRepository;
	}

	public void setCastRepository(CastRepository castRepository) {
		this.castRepository = castRepository;
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

}
