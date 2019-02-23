package com.upgrade.campsitebookingapi.web.rest.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * A DTO for the Booking entity.
 */
public class BookingDTO {
	
	private Long id;
	
	@NotNull
	private String email;
	
	@NotNull
	private String fullName;
	
	@NotNull
	private LocalDate arrivalDate;
	
	@NotNull
	private LocalDate departureDate;
	
	@NotNull
	@Min(1)
	private int people;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getArrivalDate() {
		return arrivalDate.toString();
	}
	
	public LocalDate getArrivalDateAsLocalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(LocalDate arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public LocalDate getDepartureDateAsLocalDate() {
		return departureDate;
	}
	
	public String getDepartureDate() {
		return departureDate.toString();
	}

	public void setDepartureDate(LocalDate departureDate) {
		this.departureDate = departureDate;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	@Override
	public String toString() {
		return "BookingDTO{"
				+ "id: " + getId()
				+ ", email: " + getEmail()
				+ ", fullName: " + getFullName()
				+ ", arrivalDate: " + getArrivalDate()
				+ ", departureDate: " + getDepartureDateAsLocalDate()
				+ ", people: " + getPeople() + "}";
	}

}
