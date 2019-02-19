package com.upgrade.campsitebookingapi.web.rest;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.campsitebookingapi.web.rest.dto.AvailabilityDTO;

@RestController
@RequestMapping("/api")
@CrossOrigin(
		origins = "*",
		maxAge = 3600,
		exposedHeaders = {"current-page", "total-count", "total-items"})
public class AvailabilityResource {
	
    /**
     * GET  /availabilities : Find when the campsite is available.
     *
     * @param from the minimum date
     * @param to the maximum date
     * @param isAvailability the number of bookings is greater than 0  
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of availabilities in body
     */
	@GetMapping("/availabilities")
	public ResponseEntity<AvailabilityDTO> getAvailabilities(
			LocalDate from, LocalDate to, Boolean isAvailability, Pageable pageable) {
		
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}


}
