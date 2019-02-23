package com.upgrade.campsitebookingapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.upgrade.campsitebookingapi.domain.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{

}
