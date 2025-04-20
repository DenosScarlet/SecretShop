package com.secretShop.dtl.service.impl;

import com.secretShop.dtl.entity.Quest;
import com.secretShop.dtl.repository.QuestRepository;
import com.secretShop.dtl.service.IQuestService;
import com.secretShop.dtl.service.convertor.QuestMapper;
import com.secretShop.dtl.service.dto.QuestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IQuestServiceImpl implements IQuestService {

    private final QuestRepository questRepository;
    private final QuestMapper questMapper;


    @Override
    public List<QuestDTO> findAll() {
        return questMapper.toListDto(questRepository.findAll());
    }

    @Override
    public QuestDTO findById(UUID id) {
        return Optional.of(getById(id)).map(questMapper::modelToDto).get();
    }

    @Override
    @Transactional
    public QuestDTO save(QuestDTO quest) {
        return questMapper.modelToDto(questRepository.save(
                questMapper.dtoToModel(quest)));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        var quest = getById(id);
        questRepository.delete(quest);
    }

    private Quest getById(UUID id) {
        return questRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Квест с id: " + id + " не найден :("));
    }
}
