package com.upgrade.campsitebookingapi.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.upgrade.campsitebookingapi.domain.Availability;
import com.upgrade.campsitebookingapi.repository.AvailabilityRepository;

@Service
public class AvailabilityService {
	
	@Value("${configuration.campsite.capacity}")
	private Integer campsiteCapacity;
	
	private AvailabilityRepository availabilityRepository;
	
	public AvailabilityService(AvailabilityRepository availabilityRepository) {
		this.availabilityRepository = availabilityRepository;
	}

	
	/**
	 * Search entities that match the criteria.
	 * 
	 * @param from the "from" date
	 * @param until the "until" date
	 * @param onlyAvailable if true includes only days with availability greater than 0. Default: true. 
	 * @param pageable the pagination information
	 * @return the entities that match the search criteria
	 */
	public Page<Availability> search(LocalDate from, LocalDate until, Boolean onlyAvailable, Pageable pageable) throws IllegalArgumentException {
		/* Validations and default values */
		if (from == null) {
			from = LocalDate.now();
		} else if (from.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("The 'from' date must be after to now");
		}
		if (until == null) {
			until = from.plusMonths(1);
		}
		else if (until.isBefore(from)) {
			throw new IllegalArgumentException("The 'until' date must be after to 'from' date");
		}
		if (onlyAvailable == null) {
			onlyAvailable = true;
		}
		/* End of validations and default values */
		
		if (onlyAvailable) {
			return availabilityRepository.findByDateBetweenAndValueGreaterThan(from, until, 0, pageable);
		} else {
			return availabilityRepository.findByDateBetween(from, until, pageable);
		}
	}
	
	/**
	 * Each one hour create the new availability to complete one year
	 * It could be executed once an day but if it fails there could be
	 * a hole in the calendar. So if the first fails there are eleven opportunities left
	 * to create the new availability.
	 */
	@Scheduled(cron = "0 0 * * * *")
	private void createNewAvailability () {
		LocalDate oneYearFromToday = LocalDate.now().plusDays(365);
		// Check if it already exists
		Optional<Availability> av = availabilityRepository.findByDate(oneYearFromToday);
		if(!av.isPresent()) {
			//If it doesn't exist create the new one
			Availability availability = new Availability();
			availability.setDate(oneYearFromToday);
			availability.setValue(campsiteCapacity);
			availabilityRepository.save(availability);
		}
	}
}
