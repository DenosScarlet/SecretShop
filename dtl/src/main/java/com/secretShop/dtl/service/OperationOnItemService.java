package com.secretShop.dtl.service;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.entity.OperationOnItem;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.repository.OperationOnItemRepository;
import com.secretShop.dtl.service.convertor.OperationOnItemMapper;
import com.secretShop.dtl.service.dto.CreateOperationOnItemDTO;
import com.secretShop.dtl.service.dto.OperationOnItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationOnItemService {

    private final OperationOnItemRepository repository;
    private final OperationOnItemMapper mapper;

    @Transactional
    public OperationOnItemDTO createOperation(CreateOperationOnItemDTO dto) {
        OperationOnItem operation = mapper.dtoToModel(convertToOperationDTO(dto));
        operation = repository.save(operation);
        return mapper.modelToDto(operation);
    }

    @Transactional(readOnly = true)
    public OperationOnItemDTO getOperationById(UUID id) {
        OperationOnItem operation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation not found"));
        return mapper.modelToDto(operation);
    }

    @Transactional(readOnly = true)
    public List<OperationOnItemDTO> getAllOperations() {
        return mapper.toListDto(repository.findAll());
    }

    @Transactional(readOnly = true)
    public List<OperationOnItemDTO> getOperationsByUserId(UUID userId) {
        return mapper.toListDto(repository.findByUserUserId(userId));
    }

    @Transactional(readOnly = true)
    public List<OperationOnItemDTO> getOperationsByItemId(UUID itemId) {
        return mapper.toListDto(repository.findByItemItemId(itemId));
    }

    @Transactional
    public OperationOnItemDTO updateOperation(UUID id, OperationOnItemDTO dto) {
        OperationOnItem operation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation not found"));

        // Обновляем только необходимые поля
        operation.setStatus(dto.getStatus());
        operation.setOperationHistory(dto.getOperationHistory());

        // Обновляем связи через UUID
        if (dto.getUserId() != null) {
            if (operation.getUser() == null) {
                operation.setUser(new User());
            }
            operation.getUser().setUserId(dto.getUserId());
        }

        if (dto.getItemId() != null) {
            if (operation.getItem() == null) {
                operation.setItem(new Item());
            }
            operation.getItem().setItemId(dto.getItemId());
        }


        operation = repository.save(operation);
        return mapper.modelToDto(operation);
    }

    @Transactional
    public void deleteOperation(UUID id) {
        repository.deleteById(id);
    }

    private OperationOnItemDTO convertToOperationDTO(CreateOperationOnItemDTO createDto) {
        return OperationOnItemDTO.builder()
                .userId(createDto.getUserId())
                .itemId(createDto.getItemId())
                .status(createDto.getStatus())
                .operationHistory(createDto.getOperationHistory())
                .build();
    }
}