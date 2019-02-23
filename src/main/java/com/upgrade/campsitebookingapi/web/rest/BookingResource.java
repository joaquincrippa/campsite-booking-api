package com.upgrade.campsitebookingapi.web.rest;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.upgrade.campsitebookingapi.service.BookingService;
import com.upgrade.campsitebookingapi.web.rest.dto.BookingDTO;
import com.upgrade.campsitebookingapi.web.rest.mapper.BookingMapper;

import javassist.NotFoundException;

@RestController
@RequestMapping("/api")
@CrossOrigin(
		origins = "*",
		maxAge = 3600,
		exposedHeaders = {"current-page", "total-count", "total-items"})
public class BookingResource {
	
	private final Logger log = LoggerFactory.getLogger(BookingResource.class);
	
	private BookingService bookingService;
	
	private BookingMapper bookingMapper;
	
	public BookingResource (BookingService bookingService, BookingMapper bookingMapper) {
		this.bookingService = bookingService;
		this.bookingMapper = bookingMapper;
	}

    /**
     * POST  /bookings : Create a new booking.
     *
     * @param bookingDTO the bookingDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookingDTO,
     * or with status 400 (Bad Request) if the bookingDTO is not valid
     */
	@PostMapping("/bookings")
	public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
		log.debug("REST request to create Booking : {}", bookingDTO);
		if (bookingDTO.getId() != null) {
			return ResponseEntity.badRequest().body("A new booking cannot already have an ID");
        }
		try {
			BookingDTO result = bookingMapper.toDto(bookingService.create(bookingDTO));
			return ResponseEntity.status(HttpStatus.CREATED).body(result);			
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
    /**
     * PUT  /bookings/:id : Update an existing booking.
     *
     * @param bookingDTO the bookingDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookingDTO,
     * or with status 400 (Bad Request) if the bookingDTO is not valid,
     * or with status 404 (Not Found) if the resource does not exist,
     * or with status 500 (Internal Server Error) if the bookingDTO couldn't be updated
     */
	@PutMapping("/bookings/{id}")
	public ResponseEntity<Object> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingDTO bookingDTO) {
		log.debug("REST request to create Booking : {}", bookingDTO);
		try {
			BookingDTO result = bookingMapper.toDto(bookingService.update(id, bookingDTO));
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NotFoundException e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
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
