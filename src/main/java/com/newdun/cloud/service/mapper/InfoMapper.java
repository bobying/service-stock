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
    @Mapping(source = "judge.id", target = "judgeId")
    @Mapping(source = "judge.score", target = "judgeScore")
    @Mapping(source = "judge.increase_days", target = "increaseDays")
    @Mapping(source = "judge.increase_total", target = "increaseTotal", numberFormat="#.##")
    @Mapping(source = "judge.day5", target = "increasedDay5", numberFormat="#.##")
    @Mapping(source = "judge.day10", target = "increasedDay10", numberFormat="#.##")
    @Mapping(source = "judge.day20", target = "increasedDay20", numberFormat="#.##")
    @Mapping(source = "judge.day30", target = "increasedDay30", numberFormat="#.##")
    InfoDTO toDto(Info info);

    @Mapping(source = "sourceId", target = "source")
//    @Mapping(source = "judgeId", target = "judge")
    @Mapping(target = "judge", ignore = true)
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
