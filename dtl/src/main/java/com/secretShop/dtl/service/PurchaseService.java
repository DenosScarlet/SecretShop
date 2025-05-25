package com.secretShop.dtl.service;

import com.secretShop.dtl.DTO.CreateOperationOnItemDTO;
import com.secretShop.dtl.DTO.OperationOnItemDTO;
import com.secretShop.dtl.DTO.PurchaseItemDTO;
import com.secretShop.dtl.entity.Item;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.enums.Status;
import com.secretShop.dtl.repository.ItemRepository;
import com.secretShop.dtl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OperationOnItemService operationOnItemService;

    @Transactional
    public OperationOnItemDTO purchaseItem(PurchaseItemDTO purchaseItemDTO) {
        // Получаем предмет и пользователя
        Item item = itemRepository.findById(purchaseItemDTO.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
        User user = userRepository.findById(purchaseItemDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем доступность предмета и баланс пользователя
        if (item.getCount() <= 0) {
            throw new RuntimeException("Item out of stock");
        }
        if (user.getBalance() < item.getCost()) {
            throw new RuntimeException("Insufficient funds");
        }

        // Обновляем количество предметов и баланс пользователя
        item.setCount(item.getCount() - 1);
        itemRepository.save(item);

        user.setBalance(user.getBalance() - item.getCost());
        userRepository.save(user);

        // Создаем JSON для истории операции
        String operationHistory = String.format("""
        {
            "action": "purchase",
            "itemId": "%s",
            "itemName": "%s",
            "userId": "%s",
            "userName": "%s %s",
            "cost": %d,
            "message": "Purchase completed successfully"
        }
        """,
                item.getItemId(), item.getItemName(),
                user.getUserId(), user.getFirstName(), user.getLastName(),
                item.getCost());

        // Создаем запись об операции
        CreateOperationOnItemDTO operationDTO = CreateOperationOnItemDTO.builder()
                .userId(purchaseItemDTO.getUserId())
                .itemId(purchaseItemDTO.getItemId())
                .status(Status.COMPLETE)
                .operationHistory(operationHistory)
                .build();

        return operationOnItemService.createOperation(operationDTO);
    }
}
