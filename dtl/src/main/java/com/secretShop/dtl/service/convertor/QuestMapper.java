package com.secretShop.dtl.service.convertor;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.DTO.QuestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestMapper {
    Quest dtoToModel(QuestDTO questDTO);

    QuestDTO modelToDto(Quest quest);

    List<QuestDTO> toListDto(List<Quest> quests);

    @Mapping(target = "questId", ignore = true)
    void updateFromDto(QuestDTO questDTO, @MappingTarget Quest quest);
}
