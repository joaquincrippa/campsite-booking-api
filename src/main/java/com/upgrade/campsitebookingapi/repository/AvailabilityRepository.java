package com.upgrade.campsitebookingapi.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upgrade.campsitebookingapi.domain.Availability;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long>{

	Availability findByDate(LocalDate date);

	Page<Availability> findByDateBetween(
			LocalDate from, LocalDate until, Pageable pageable);

	Page<Availability> findByDateBetweenAndValueGreaterThan(
			LocalDate from, LocalDate until, Integer value, Pageable pageable);
}
