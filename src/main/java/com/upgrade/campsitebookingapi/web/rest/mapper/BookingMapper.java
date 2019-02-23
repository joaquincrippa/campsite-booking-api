package com.upgrade.campsitebookingapi.web.rest.mapper;

import org.mapstruct.Mapper;

import com.upgrade.campsitebookingapi.domain.Booking;
import com.upgrade.campsitebookingapi.web.rest.dto.BookingDTO;

/**
 * Mapper for the entity Booking and its DTO BookingDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BookingMapper extends EntityMapper<BookingDTO, Booking> {



    default Booking fromId(Long id) {
        if (id == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setId(id);
        return booking;
    }
}
