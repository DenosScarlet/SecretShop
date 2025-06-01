package com.secretShop.dtl.service.interfaces;

import com.secretShop.dtl.DTO.QuestDTO;

import java.util.List;
import java.util.UUID;

public interface IQuestService {
    List<QuestDTO> findAll ();
    QuestDTO findById(UUID id);
    QuestDTO save (QuestDTO quest);
    void deleteById (UUID id);
}
