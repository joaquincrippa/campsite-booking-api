package com.upgrade.campsitebookingapi.web.rest.dto;

import java.time.LocalDate;

/**
 * A DTO for the Availability entity.
 */
public class AvailabilityDTO {

	private LocalDate date;
	
	private Integer value;

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
	
}
