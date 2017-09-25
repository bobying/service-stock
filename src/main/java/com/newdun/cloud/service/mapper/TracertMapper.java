package com.newdun.cloud.service.mapper;

import com.newdun.cloud.domain.*;
import com.newdun.cloud.service.dto.TracertDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Tracert and its DTO TracertDTO.
 */
@Mapper(componentModel = "spring", uses = {SourceMapper.class, })
public interface TracertMapper extends EntityMapper <TracertDTO, Tracert> {

    @Mapping(source = "source.id", target = "sourceId")
    @Mapping(source = "source.title", target = "sourceTitle")
    TracertDTO toDto(Tracert tracert); 

    @Mapping(source = "sourceId", target = "source")
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
