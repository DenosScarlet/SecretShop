package com.secretShop.dtl.service;

import com.secretShop.dtl.DTO.UpdateBalanceDTO;
import com.secretShop.dtl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired // Добавьте эту аннотацию
    UserRepository userRepository;

    @Transactional
    public void updateBalance(UpdateBalanceDTO balanceDTO) {
        userRepository.updateBalanceById(balanceDTO.getUserId(), balanceDTO.getNewBalance());
    }
}
