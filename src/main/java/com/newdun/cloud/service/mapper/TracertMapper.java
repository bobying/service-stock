package com.newdun.cloud.service.mapper;

import com.newdun.cloud.domain.*;
import com.newdun.cloud.service.dto.TracertDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Tracert and its DTO TracertDTO.
 */
@Mapper(componentModel = "spring", uses = {InfoMapper.class, })
public interface TracertMapper extends EntityMapper <TracertDTO, Tracert> {

    @Mapping(source = "info.id", target = "infoId")
    @Mapping(source = "info.title", target = "infoTitle")
    TracertDTO toDto(Tracert tracert); 

    @Mapping(source = "infoId", target = "info")
    Tracert toEntity(TracertDTO tracertDTO); 
    default Tracert fromId(Long id) {
        if (id == null) {
            return null;
        }
        Tracert tracert = new Tracert();
        tracert.setId(id);
        return tracert;
    }
}
