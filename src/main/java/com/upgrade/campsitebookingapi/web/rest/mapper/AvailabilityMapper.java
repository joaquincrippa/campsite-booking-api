package com.upgrade.campsitebookingapi.web.rest.mapper;

import com.upgrade.campsitebookingapi.domain.Availability;
import com.upgrade.campsitebookingapi.web.rest.dto.AvailabilityDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Availability and its DTO AvailabilityDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface AvailabilityMapper extends EntityMapper<AvailabilityDTO, Availability> {



    default Availability fromId(Long id) {
        if (id == null) {
            return null;
        }
        Availability task = new Availability();
        task.setId(id);
        return task;
    }
}
