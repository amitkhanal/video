package com.acme.video.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.acme.video.data.model.Cast;
import com.acme.video.service.CastService;

@RestController
public class CastResource {

	private final static Logger LOGGER = LoggerFactory.getLogger(CastResource.class);
	
	@Autowired
	private CastService castService;
	
	/**
	 * Returns cast by provided page size and page number. If page size and number are not provided, it will set default values.
	 * 
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	@RequestMapping(value="/cast", method=RequestMethod.GET)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getCasts(@QueryParam("pageSize") Integer pageSize, @QueryParam("pageNumber") Integer pageNumber){
		if (pageNumber == null) {
            pageNumber = ResourceConstants.DEFAULT_PAGE_NUMBER;
        }else if(pageNumber <0){
        	pageNumber=1;
        }
		if(pageSize==null){
			pageSize = ResourceConstants.DEFAULT_PAGE_SIZE;
		}
		Page<Cast> casts = getCastService().getAllCasts(pageSize, pageNumber);
		return Response.ok(casts).build();
	}

	public CastService getCastService() {
		return castService;
	}

	public void setCastService(CastService castService) {
		this.castService = castService;
	}
	
	
}
