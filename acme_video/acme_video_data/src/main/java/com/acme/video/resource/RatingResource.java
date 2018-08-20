package com.acme.video.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.acme.video.data.model.Rating;
import com.acme.video.service.RatingService;

/**
 * @author amitkhanal
 *
 * This class exposes REST API to access movie ratings data
 * 
 */
@RestController
public class RatingResource {

	private static final Logger LOGGER = Logger.getLogger(RatingResource.class);
	
	@Autowired
	private RatingService ratingService;
	
	/**
	 * Returns rating by provided page size and page number. If page size and number are not provided, it will set default values.
	 * 
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	@RequestMapping(value="/rating", method=RequestMethod.GET)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getRatings(@QueryParam("pageSize") Integer pageSize, @QueryParam("pageNumber") Integer pageNumber){
		if (pageNumber == null) {
            pageNumber = ResourceConstants.DEFAULT_PAGE_NUMBER;
        }else if(pageNumber <0){
        	pageNumber=1;
        }
		if(pageSize==null){
			pageSize = ResourceConstants.DEFAULT_PAGE_SIZE;
		}
		Page<Rating> ratings = getRatingService().getAllRatings(pageSize, pageNumber);
		return Response.ok(ratings).build();
	}

	public RatingService getRatingService() {
		return ratingService;
	}

	public void setRatingService(RatingService ratingService) {
		this.ratingService = ratingService;
	}
	
}
