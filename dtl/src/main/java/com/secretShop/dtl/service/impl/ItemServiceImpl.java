package com.secretShop.dtl.service.impl;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.enums.Type;
import com.secretShop.dtl.repository.ItemRepository;
import com.secretShop.dtl.service.interfaces.ItemService;
import com.secretShop.dtl.service.convertor.ItemMapper;
import com.secretShop.dtl.DTO.ItemDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDTO> findAll() {
        return itemMapper.toListDto(itemRepository.findAll());
    }

    @Override
    public ItemDTO findById(UUID id) {
        return itemMapper.modelToDto(getById(id));
    }

    @Override
    @Transactional
    public ItemDTO save(ItemDTO itemDTO) {
        // Валидация перед сохранением


        Item item = itemMapper.dtoToModel(itemDTO);
        Item savedItem = itemRepository.save(item);
        return itemMapper.modelToDto(savedItem);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Item item = getById(id);
        itemRepository.delete(item);
    }

    private Item getById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Предмет с id: " + id + " не найден"
                ));
    }

    @Override
    public List<ItemDTO> searchItems(String name, String owner, Type type) {
        String searchName = (name != null && !name.isEmpty()) ? name : null;
        String searchOwner = (owner != null && !owner.isEmpty()) ? owner : null;

        List<Item> items = itemRepository.searchItems(searchName, searchOwner, type);
        return itemMapper.toListDto(items);
    }


}