package com.newdun.cloud.service.mapper;

import com.newdun.cloud.domain.*;
import com.newdun.cloud.service.dto.InfoDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Info and its DTO InfoDTO.
 */
@Mapper(componentModel = "spring", uses = {SourceMapper.class, })
public interface InfoMapper extends EntityMapper <InfoDTO, Info> {

    @Mapping(source = "source.id", target = "sourceId")
    @Mapping(source = "source.title", target = "sourceTitle")
    InfoDTO toDto(Info info); 

    @Mapping(source = "sourceId", target = "source")
    Info toEntity(InfoDTO infoDTO); 
    default Info fromId(Long id) {
        if (id == null) {
            return null;
        }
        Info info = new Info();
        info.setId(id);
        return info;
    }
}
