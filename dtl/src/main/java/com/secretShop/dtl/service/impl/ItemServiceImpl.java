package com.secretShop.dtl.service.impl;

import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.enums.Type;
import com.secretShop.dtl.repository.ItemRepository;
import com.secretShop.dtl.service.ItemService;
import com.secretShop.dtl.service.convertor.ItemMapper;
import com.secretShop.dtl.service.dto.ItemDTO;
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
    public List<ItemDTO> findAll() { return itemMapper.toListDto(itemRepository.findAll()); }

    @Override
    public ItemDTO findById(UUID id) { return Optional.of(getById(id)).map(itemMapper::modelToDto).get();
    }

    @Override
    public ItemDTO save(ItemDTO item) {
        return itemMapper.modelToDto(itemRepository.save(
                itemMapper.dtoToModel(item)));
    }

    @Override
    public void deleteById(UUID id) {
        var book = getById(id);
        itemRepository.delete(book);
    }

    private Item getById(UUID id){
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Предмет с id: " + id + " не найден :("));
    }

    @Override
    public List<ItemDTO> searchItems(String name, String owner, Type type) {
        return itemMapper.toListDto(itemRepository.searchItems(
                name != null ? name : "",
                owner != null ? owner : "",
                type
        ));
    }
}
