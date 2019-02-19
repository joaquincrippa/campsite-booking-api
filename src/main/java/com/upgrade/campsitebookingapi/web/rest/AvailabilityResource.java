package com.upgrade.campsitebookingapi.web.rest;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.campsitebookingapi.service.AvailabilityService;
import com.upgrade.campsitebookingapi.web.rest.dto.AvailabilityDTO;
import com.upgrade.campsitebookingapi.web.rest.mapper.AvailabilityMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin(
		origins = "*",
		maxAge = 3600,
		exposedHeaders = {"current-page", "total-count", "total-items"})
public class AvailabilityResource {
	
    private final Logger log = LoggerFactory.getLogger(AvailabilityResource.class);

    private final AvailabilityService availabilityService;
    
    private final AvailabilityMapper availabilityMapper;

    public AvailabilityResource (AvailabilityService availabilityService, AvailabilityMapper availabilityMapper) {
    	this.availabilityService = availabilityService;
    	this.availabilityMapper = availabilityMapper;
    }
    
    /**
     * GET  /availabilities : Find out when the campsite is available.
     *
     * @param from @description the minimum date
     * @param until the maximum date
     * @param onlyAvailable the number of available places has to be greater than 0  
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of availabilities in body
     */
	@GetMapping("/availabilities")
	public ResponseEntity<Object> getAvailabilities(
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate until,
			Boolean onlyAvailable, Pageable pageable) {

		log.info("REST request to get Availabilities: from: {}, until: {}, onlyAvailable: {}, page: {}",
				from, until, onlyAvailable, pageable);
		try {
			Page <AvailabilityDTO> page = availabilityService
					.find(from, until, onlyAvailable, pageable)
					.map(availabilityMapper::toDto);
			
			HttpHeaders headers = generatePaginationHttpHeaders(page, "/api/availabilities");
			return ResponseEntity.ok().headers(headers).body(page.getContent());			
		} catch (IllegalArgumentException exc) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
		}
	}
	
    private <T> HttpHeaders generatePaginationHttpHeaders(Page<T> page, String baseUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("total-count", Long.toString(page.getTotalElements()));
        headers.add("current-page", Integer.toString(page.getNumber()));
        headers.add("total-pages", Integer.toString(page.getTotalPages()));
        return headers;
    }
}
