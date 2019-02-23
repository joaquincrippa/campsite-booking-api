package com.upgrade.campsitebookingapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.upgrade.campsitebookingapi.domain.Availability;
import com.upgrade.campsitebookingapi.domain.Booking;
import com.upgrade.campsitebookingapi.repository.AvailabilityRepository;
import com.upgrade.campsitebookingapi.repository.BookingRepository;
import com.upgrade.campsitebookingapi.web.rest.dto.BookingDTO;
import com.upgrade.campsitebookingapi.web.rest.mapper.BookingMapper;

import javassist.NotFoundException;

@Service
public class BookingService {
	
	private BookingMapper bookingMapper;
	
	private AvailabilityRepository availabilityRepository;
	
	private BookingRepository bookingRepository;
	
	public BookingService(BookingMapper bookingMapper, AvailabilityRepository availabilityRepository,
			BookingRepository bookingRepository) {
		this.bookingMapper = bookingMapper;
		this.availabilityRepository = availabilityRepository;
		this.bookingRepository = bookingRepository;
	}

	
	/**
	 * Create a booking and update the availability of booking days.
	 * For validations, @see the {@link #checkValidations(BookingDTO)} method.
	 * 
	 * @param bookingDTO the data of booking to create
	 * @return the created entity
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Booking create(BookingDTO bookingDTO) throws IllegalArgumentException{		
		checkValidations(bookingDTO);
		Booking booking = bookingMapper.toEntity(bookingDTO);
		/* Find the availability for every days between arrival and departure date */
		List <Availability> avs = availabilityRepository.findByDateBetween(
				booking.getArrivalDate(), booking.getDepartureDate());
		checkAvailabilities(booking, avs);
		/* After check the availability for every days, it updates in DB the new availability values
  		   and create the new booking */
		availabilityRepository.saveAll(avs);
		return bookingRepository.save(booking);
	}

	/**
	 * Update an existing booking and update the news in the availabilities of previous 
	 * and current booking days. 
	 * The existing booking must not be active (the arrivalDate has to be after today).
	 * For validations about the booking data to update, @see the {@link #checkValidations(BookingDTO)} method.
	 *
	 * @param id the id of an existing entity
	 * @param bookingDTO 
	 * @return the updated booking
	 */
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Booking update(Long id, BookingDTO bookingDTO) throws NotFoundException, IllegalArgumentException {
		Optional<Booking> optBooking = bookingRepository.findById(id);
		if(!optBooking.isPresent()) {
			throw new NotFoundException("The booking does not exist");
		}
		checkValidations(bookingDTO);
		Booking oldBooking = optBooking.get();
		/* Check if booking is not active */
		if (oldBooking.getArrivalDate().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("The booking is active, so it can't be updated");
		}
		Booking updatedBooking = bookingMapper.toEntity(bookingDTO);
		updatedBooking.setId(id);
		LocalDate minDate = oldBooking.getArrivalDate().isAfter(updatedBooking.getArrivalDate()) ?
				updatedBooking.getArrivalDate() : oldBooking.getArrivalDate();
		LocalDate maxDate = oldBooking.getDepartureDate().isBefore(updatedBooking.getDepartureDate()) ?
				updatedBooking.getDepartureDate() : oldBooking.getDepartureDate();
		/* Find the availability for every days between min and max date */
		List <Availability> availabilities = availabilityRepository.findByDateBetween(
				minDate, maxDate);
		rollbackBooking(oldBooking, availabilities);
		checkAvailabilities(updatedBooking, availabilities);
		availabilityRepository.saveAll(availabilities);
		return bookingRepository.save(updatedBooking);
	}
	
	/**
	 * Check the following validations:
	 * 1. departureDate must not be later than one year.
	 * 2. departureDate has to be after arrivalDate.
	 * 3. maximum booking time is three days.
	 * 4. arrivalDate has to be after today.
	 * 
	 * @param bookingDTO the booking data 
	 */
	private void checkValidations(BookingDTO bookingDTO) {
		if (bookingDTO.getDepartureDateAsLocalDate().isAfter(LocalDate.now().plusYears(1L))) {
			throw new IllegalArgumentException("departureDate can not be later than one year");
		}
		if (!bookingDTO.getDepartureDateAsLocalDate().isAfter(bookingDTO.getArrivalDateAsLocalDate())) {
			throw new IllegalArgumentException("departureDate has to be after arrivalDate");	
		}
		if (bookingDTO.getDepartureDateAsLocalDate().isAfter(bookingDTO.getArrivalDateAsLocalDate().plusDays(3L))) {
			throw new IllegalArgumentException("The maximum booking time is three days");	
		}
		if (!bookingDTO.getArrivalDateAsLocalDate().isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("arrivalDate has to be after today");
		}
	}
	
	/**
	 * Undo changes of a booking in availabilities.
	 * From arrivalDate until departureDate, add "people" value to availability value for each day. 
	 * 
	 * @param oldBooking the booking to rollback
	 * @param availabilities the affected availabilities
	 * @return the updated availabilities
	 */
	private List<Availability> rollbackBooking(Booking oldBooking, List <Availability> availabilities) {
		/* For each day, rollback the availability considering the people of the booking */
		LocalDate currentDate = oldBooking.getArrivalDate();
		while (currentDate.isBefore(oldBooking.getDepartureDate())) {
			final LocalDate currentDateAux = currentDate;
			Optional<Availability> currentAv = availabilities.stream()
					.filter((Availability av) -> av.getDate().equals(currentDateAux))
					.findFirst();			
			if (!currentAv.isPresent()) {
				throw new IllegalArgumentException("An error occurred. An availability couldn't be found.");
			}
			currentAv.get().setValue(currentAv.get().getValue() + oldBooking.getPeople());
			currentDate = currentDate.plusDays(1L);
		}
		return availabilities;
	}
	
	/**
	 * Check if everyday has availability to accept the booking.
	 * If one day doesn't have enough availability, throw an exception reporting the conflicting day
	 * and its current availability 
	 * 
	 * @param newBooking the booking to check availability
	 * @param availabilities the affected availabilities
	 * @return the updated availabilities
	 */	
	private List<Availability> checkAvailabilities(Booking newBooking, List <Availability> availabilities) {
		/* For each day check it has the required availability */
		LocalDate currentDate = newBooking.getArrivalDate();
		while (currentDate.isBefore(newBooking.getDepartureDate())) {
			final LocalDate currentDateAux = currentDate;
			Optional<Availability> currentAv = availabilities.stream()
					.filter((Availability av) -> av.getDate().equals(currentDateAux))
					.findFirst();
			if (!currentAv.isPresent()) {
				throw new IllegalArgumentException("The campsite is not available for that date");
			}
			if (currentAv.get().getValue() < newBooking.getPeople()) {
				throw new IllegalArgumentException("The campsite available availability for " +
						currentDate + " is " + currentAv.get().getValue());
			}
			/* If there's the required capacity for that day, it updates the new available value */
			currentAv.get().setValue(currentAv.get().getValue() - newBooking.getPeople());
			currentDate = currentDate.plusDays(1L);
		}
		return availabilities;
	}

	
}
