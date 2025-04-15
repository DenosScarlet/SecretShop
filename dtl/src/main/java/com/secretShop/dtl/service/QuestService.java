package com.secretShop.dtl.service;

import com.secretShop.dtl.service.dto.QuestDTO;

import java.util.List;
import java.util.UUID;

public interface QuestService {
    List<QuestDTO> findAll ();
    QuestDTO findById(UUID id);
    QuestDTO save (QuestDTO quest);
    void deleteById (UUID id);
}
