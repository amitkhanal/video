package com.acme.video.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.acme.video.data.model.Movie;
import com.acme.video.service.MovieService;

/**
 * @author amitkhanal
 * 
 * This class exposes the REST APIs to access movie data.
 *
 */
@RestController
@RequestMapping("/movie")
public class MovieResource {

	private final static Logger LOGGER = LoggerFactory.getLogger(MovieResource.class);
	
	@Autowired
	private MovieService movieService;

	/**
	 * Returns movies by provided page size and page number. If page size and number are not provided, it will set default values.
	 * 
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMovies(@QueryParam("pageSize") Integer pageSize, @QueryParam("pageNumber") Integer pageNumber){
		if (pageNumber == null) {
            pageNumber = ResourceConstants.DEFAULT_PAGE_NUMBER;
        }else if(pageNumber <0){
        	pageNumber=1;
        }
		if(pageSize==null){
			pageSize = ResourceConstants.DEFAULT_PAGE_SIZE;
		}
		Page<Movie> movies = getMovieService().getAllMovies(pageSize, pageNumber);
		return Response.ok(movies).build();
	}

	/**
	 * Returns movies based by doing a search based on the searchTerm. If page size and number are not provided, it will set default values.
	 * @param searchTerm
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	@RequestMapping(value="search/{searchTerm}",method=RequestMethod.GET)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response searchMovies(@PathVariable("searchTerm") String searchTerm, 
				@QueryParam("pageSize") Integer pageSize, @QueryParam("pageNumber") Integer pageNumber){
	
		if(StringUtils.isEmpty(searchTerm)){
			return Response.status(Status.BAD_REQUEST).entity("Search term cannot be empty").build();
		}
		if (pageNumber == null) {
            pageNumber = ResourceConstants.DEFAULT_PAGE_NUMBER;
        }else if(pageNumber <0){
        	pageNumber=1;
        }
		if(pageSize==null){
			pageSize = ResourceConstants.DEFAULT_PAGE_SIZE;
		}
		Page<Movie> movies = getMovieService().search(searchTerm, pageNumber, pageSize);
		return Response.ok(movies).build();
	}
	
	public MovieService getMovieService() {
		return movieService;
	}

	public void setMovieService(MovieService movieService) {
		this.movieService = movieService;
	}
	
}
