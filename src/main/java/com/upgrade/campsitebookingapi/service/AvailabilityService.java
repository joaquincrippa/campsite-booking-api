package com.upgrade.campsitebookingapi.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.upgrade.campsitebookingapi.domain.Availability;
import com.upgrade.campsitebookingapi.repository.AvailabilityRepository;

@Service
public class AvailabilityService {
	
	private AvailabilityRepository availabilityRepository;
	
	public AvailabilityService(AvailabilityRepository availabilityRepository) {
		this.availabilityRepository = availabilityRepository;
	}

	public Page<Availability> find(LocalDate from, LocalDate until, Boolean onlyAvailable, Pageable pageable) throws IllegalArgumentException {
		
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
}
