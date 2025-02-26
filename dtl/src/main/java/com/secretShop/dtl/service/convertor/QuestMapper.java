package com.secretShop.dtl.service.convertor;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.service.dto.QuestDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestMapper {
    Quest dtoToModel(QuestDTO questDTO);

    QuestDTO modelToDto(Quest quest);

    List<QuestDTO> toListDto(List<Quest> quests);
}
