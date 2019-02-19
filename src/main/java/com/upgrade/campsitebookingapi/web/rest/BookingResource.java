package com.upgrade.campsitebookingapi.web.rest;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.campsitebookingapi.web.rest.dto.BookingDTO;

@RestController
@RequestMapping("/api")
@CrossOrigin(
		origins = "*",
		maxAge = 3600,
		exposedHeaders = {"current-page", "total-count", "total-items"})
public class BookingResource {

    /**
     * POST  /bookings : Create a new booking.
     *
     * @param bookingDTO the bookingDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookingDTO,
     * or with status 400 (Bad Request) if the bookingDTO is not valid
     */
	@PostMapping("/bookings")
	public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
		
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}
	
    /**
     * PUT  /bookings/:id : Updates an existing booking.
     *
     * @param bookingDTO the bookingDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookingDTO,
     * or with status 400 (Bad Request) if the bookingDTO is not valid,
     * or with status 500 (Internal Server Error) if the bookingDTO couldn't be updated
     */
	@PutMapping("/bookings/{id}")
	public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingDTO bookingDTO) {
		
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

    /**
     * DELETE  /bookings/:id : cancel the "id" booking.
     *
     * @param id the id of the booking to cancel
     * @return the ResponseEntity with status 200 (OK)
     */
	@DeleteMapping("/bookings/{id}")
	public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}
	
}
