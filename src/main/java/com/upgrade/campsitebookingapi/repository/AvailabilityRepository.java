package com.upgrade.campsitebookingapi.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.upgrade.campsitebookingapi.domain.Availability;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long>{

	Optional<Availability> findByDate(LocalDate date);

	Page<Availability> findByDateBetween(
			LocalDate from, LocalDate until, Pageable pageable);
	
	@Lock(value = LockModeType.PESSIMISTIC_READ)
	List<Availability> findByDateBetween(LocalDate from, LocalDate until);


	Page<Availability> findByDateBetweenAndValueGreaterThan(
			LocalDate from, LocalDate until, Integer value, Pageable pageable);
}
