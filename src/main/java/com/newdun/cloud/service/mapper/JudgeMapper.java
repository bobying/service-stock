package com.newdun.cloud.service.mapper;

import com.newdun.cloud.domain.*;
import com.newdun.cloud.service.dto.JudgeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Judge and its DTO JudgeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface JudgeMapper extends EntityMapper <JudgeDTO, Judge> {
    
    
    default Judge fromId(Long id) {
        if (id == null) {
            return null;
        }
        Judge judge = new Judge();
        judge.setId(id);
        return judge;
    }
}
